package bg.bdz.schedule.features.schedule

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
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
import org.threeten.bp.LocalDate


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

    private val _recentSearches = MutableLiveData<List<String>>()
    val recentSearches: LiveData<List<String>> = _recentSearches

    private var swapInProgress = false

    init {
        // From

        _fromStation.observeForever { from ->
            state.set(KEY_FROM_STATION, from.name)
            simplePrefs.put(KEY_FROM_STATION, from.name)
        }
        _fromStation.value = Station(state.get(KEY_FROM_STATION) ?: simplePrefs.get(KEY_FROM_STATION, DEFAULT_FROM_STATION))

        // To

        _toStation.observeForever { to ->
            state.set(KEY_TO_STATION, to.name)
            simplePrefs.put(KEY_TO_STATION, to.name)
        }
        _toStation.value = Station(state.get(KEY_TO_STATION) ?: simplePrefs.get(KEY_TO_STATION, DEFAULT_TO_STATION))

        // Date

        _date.observeForever { date ->
            state.set(KEY_DATE, date)
            simplePrefs.put(KEY_DATE, date.format(ScheduleService.DATE_FORMATTER))
        }
        val savedDate = state.get(KEY_DATE) ?: LocalDate.now()
        onDateChanged(savedDate)

        // Recent searches

        _recentSearches.value = searchHistory.getList()
    }

    fun onFromStationChanged(from: Station) {
        if (from == _toStation.value && !swapInProgress) {
            swapInProgress = true
            _toStation.value = _fromStation.value
            swapInProgress = false
        }
        _fromStation.value = from
        addToRecentSearches(from.name)
    }

    fun onToStationChanged(to: Station) {
        if (to == _fromStation.value && !swapInProgress) {
            swapInProgress = true
            _fromStation.value = _toStation.value
            swapInProgress = false
        }
        _toStation.value = to
        addToRecentSearches(to.name)
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
        _fromStation.value = temp
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
                    Log.e("ScheduleViewModel", e.message)
                    emit(ScheduleState.Error(e))
                }
            }
        }
    }

    private suspend fun getSchedulePage(fromStation: Station, toStation: Station, date: LocalDate): List<Train> {
        return withContext(Dispatchers.IO) {
            val schedulePage = scheduleService.getSchedulePage(
                fromStation = fromStation.name,
                toStation = toStation.name,
                date = date.format(ScheduleService.DATE_FORMATTER)
            )
            schedulePage.trains
        }
    }

    private fun addToRecentSearches(newSearch: String) {
        searchHistory.add(newSearch)
        _recentSearches.value = searchHistory.getList()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val simplePrefs: SimplePreferences,
        private val searchHistory: SearchHistory,
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return ScheduleViewModel(handle, simplePrefs, searchHistory) as T
        }
    }

    companion object {
        private const val KEY_FROM_STATION = "fromStation"
        private const val KEY_TO_STATION = "toStation"
        private const val KEY_DATE = "date"

        private const val DEFAULT_FROM_STATION = "СОФИЯ"
        private const val DEFAULT_TO_STATION = "ВАРНА"
    }
}