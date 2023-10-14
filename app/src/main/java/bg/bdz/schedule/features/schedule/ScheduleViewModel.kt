package bg.bdz.schedule.features.schedule

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import bg.bdz.schedule.data.SimplePreferences
import bg.bdz.schedule.features.stations.SearchHistory
import bg.bdz.schedule.models.Station
import bg.bdz.schedule.models.Train
import bg.bdz.schedule.network.Bdz
import bg.bdz.schedule.network.ScheduleService
import bg.bdz.schedule.utils.combineLatest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate


class ScheduleViewModel(
    private val state : SavedStateHandle,
    private val simplePrefs: SimplePreferences,
    private val searchHistory: SearchHistory
) : ViewModel() {

    private val scheduleService: ScheduleService = Bdz.scheduleService

    private val _fromStation = MutableLiveData<Station>()
    val fromStation: LiveData<Station> = _fromStation

    private val _toStation = MutableLiveData<Station>()
    val toStation: LiveData<Station> = _toStation

    private val _date = MutableLiveData<LocalDate>()
    val date: LiveData<LocalDate> = _date

    val schedule: LiveData<ScheduleState> = combineLatest(_fromStation, _toStation, _date)
        .switchMap { (from, to, date) -> loadSchedule(from, to, date) }

    private val _recents = MutableLiveData<List<Station>>()
    val recents: LiveData<List<Station>> = _recents

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = _refreshing

    private var swapInProgress = false

    init {
        // From

        _fromStation.observeForever { from ->
            state[KEY_FROM_STATION] = from.slug.value
            simplePrefs.put(KEY_FROM_STATION, from.slug.value)
        }
        val fromSlug = state[KEY_FROM_STATION] ?: simplePrefs.get(KEY_FROM_STATION, DEFAULT_FROM_STATION)
        _fromStation.value = Bdz.stations.find { it.slug == Station.Slug(fromSlug) }

        // To

        _toStation.observeForever { to ->
            state[KEY_TO_STATION] = to.slug.value
            simplePrefs.put(KEY_TO_STATION, to.slug.value)
        }
        val toSlug = state[KEY_TO_STATION] ?: simplePrefs.get(KEY_TO_STATION, DEFAULT_TO_STATION)
        _toStation.value = Bdz.stations.find { it.slug == Station.Slug(toSlug) }

        // Date

        _date.observeForever { date ->
            state[KEY_DATE] = date
            simplePrefs.put(KEY_DATE, date.format(ScheduleService.DATE_FORMATTER))
        }
        val savedDate = state[KEY_DATE] ?: LocalDate.now()
        onDateChanged(savedDate)

        // Recent searches

        _recents.value = searchHistory.getList()
    }

    fun onFromStationChanged(from: Station) {
        if (from == _toStation.value && !swapInProgress) {
            swapInProgress = true
            _toStation.value = _fromStation.value
            swapInProgress = false
        }
        _fromStation.value = from
        addToRecentSearches(from)
    }

    fun onToStationChanged(to: Station) {
        if (to == _fromStation.value && !swapInProgress) {
            swapInProgress = true
            _fromStation.value = _toStation.value
            swapInProgress = false
        }
        _toStation.value = to
        addToRecentSearches(to)
    }

    fun onDateChanged(date: LocalDate) {
        _date.value = if (date.isBefore(LocalDate.now())) {
            // We don't want a past date
            LocalDate.now()
        } else {
            date
        }
    }

    fun onSwapButtonClicked() {
        val temp = _toStation.value
        _toStation.value = _fromStation.value
        _fromStation.value = temp!!
    }

    fun refresh() {
        _refreshing.value = true
        _fromStation.value = _fromStation.value
    }

    private fun loadSchedule(fromStation: Station?, toStation: Station?, date: LocalDate?): LiveData<ScheduleState> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (fromStation == toStation) {
                emit(ScheduleState.Success(emptyList()))
            } else if (fromStation != null && toStation != null && date != null) {
                emit(ScheduleState.Loading)
                try {
                    emit(
                        ScheduleState.Success(
                            getSchedulePage(fromStation, toStation, date)
                        )
                    )
                } catch (e: Exception) {
                    Log.e("ScheduleViewModel", "Failed to load schedule", e)
                    emit(ScheduleState.Error(e))
                } finally {
                    _refreshing.postValue(false)
                }
            }
        }
    }

    private suspend fun getSchedulePage(fromStation: Station, toStation: Station, date: LocalDate): List<Train> {
        return withContext(Dispatchers.IO) {
            val schedulePage = scheduleService.getSchedulePage(
                fromStation = fromStation.slug,
                toStation = toStation.slug,
                date = date.format(ScheduleService.DATE_FORMATTER)
            )
            schedulePage.trains
        }
    }

    private fun addToRecentSearches(station: Station) {
        searchHistory.add(station.slug)
        _recents.value = searchHistory.getList()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val simplePrefs: SimplePreferences,
        private val searchHistory: SearchHistory,
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return ScheduleViewModel(handle, simplePrefs, searchHistory) as T
        }
    }

    companion object {
        // Slugs for current from/to stations
        private const val KEY_FROM_STATION = "fromStation"
        private const val KEY_TO_STATION = "toStation"
        private const val KEY_DATE = "date"

        // Slugs for default stations
        private const val DEFAULT_FROM_STATION = "sofia" //"СОФИЯ"
        private const val DEFAULT_TO_STATION = "varna"   //"ВАРНА"
    }
}