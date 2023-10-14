package bg.bdz.schedule.features.timetable

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
import bg.bdz.schedule.models.TrainStatus
import bg.bdz.schedule.network.Bdz
import bg.bdz.schedule.network.Bdz.timeTablesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TimeTablesViewModel(
    private val state : SavedStateHandle,
    private val simplePrefs: SimplePreferences,
    private val searchHistory: SearchHistory
) : ViewModel() {

    private val _station = MutableLiveData<Station>()
    val station: LiveData<Station> = _station

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    private val _recents = MutableLiveData<List<Station>>()
    val recents: LiveData<List<Station>> = _recents

    private val _refreshingArrivals = MutableLiveData<Boolean>()
    val refreshingArrivals: LiveData<Boolean> = _refreshingArrivals

    private val _refreshingDepartures = MutableLiveData<Boolean>()
    val refreshingDepartures: LiveData<Boolean> = _refreshingDepartures

    init {
        station.observeForever { station ->
            state[KEY_STATION_SLUG] = station.slug.value
            simplePrefs.put(KEY_STATION_SLUG, station.slug.value)
        }
        val slug = state[KEY_STATION_SLUG] ?: simplePrefs.get(KEY_STATION_SLUG, DEFAULT_STATION_SLUG)
        _station.value = Bdz.stations.find { it.slug == Station.Slug(slug) }
        _recents.value = searchHistory.getList()
    }

    fun setStation(newStation: Station) {
        if (_station.value != newStation) {
            _station.value = newStation
            addToRecentSearches(newStation)
        }
    }

    fun refresh() {
        _station.value = _station.value
        _refreshingArrivals.value = true
        _refreshingDepartures.value = true
    }

    val arriving: LiveData<TimeTableState> = _station
        .switchMap { station ->
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(TimeTableState.Loading)
                try {
                    emit(TimeTableState.Success(loadArriving(station.slug)))
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Failed to load arriving timetable for ${station.slug}", e)
                    emit(TimeTableState.Error(e))
                } finally {
                    _refreshingArrivals.postValue(false)
                }
            }
        }

    val departing: LiveData<TimeTableState> = _station
        .switchMap { station ->
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(TimeTableState.Loading)
                try {
                    emit(TimeTableState.Success(loadDeparting(station.slug)))
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Failed to load departing timetable for ${station.slug}", e)
                    emit(TimeTableState.Error(e))
                } finally {
                    _refreshingDepartures.postValue(false)
                }
            }
        }

    private suspend fun loadArriving(station: Station.Slug): List<TrainStatus> {
        return withContext(Dispatchers.IO) {
            val timeTable = timeTablesService.getArrivalsTimeTable(station = station)
            timeTable.trainStatuses.filter { it.trainCode.isNotBlank() }
        }
    }

    private suspend fun loadDeparting(station: Station.Slug): List<TrainStatus> {
        return withContext(Dispatchers.IO) {
            val timeTable = timeTablesService.getDeparturesTimeTable(station = station)
            timeTable.trainStatuses.filter { it.trainCode.isNotBlank() }
        }
    }

    private fun addToRecentSearches(station: Station) {
        searchHistory.add(station.slug)
        _recents.value = searchHistory.getList()
    }

    fun setCurrentPage(page: Int) {
        _currentPage.postValue(page)
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
            return TimeTablesViewModel(handle, simplePrefs, searchHistory) as T
        }
    }

    companion object {
        const val LOG_TAG = "TimeTablesViewModel"

        internal const val KEY_STATION_SLUG = "station"
        internal const val DEFAULT_STATION_SLUG = "sofia"
    }
}