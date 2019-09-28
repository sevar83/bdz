package bg.bdz.schedule.features.timetable

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import bg.bdz.schedule.data.SimplePreferences
import bg.bdz.schedule.features.stations.SearchHistory
import bg.bdz.schedule.models.Station
import bg.bdz.schedule.models.TrainStatus
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

    private val _recentSearches = MutableLiveData<List<String>>()
    val recentSearches: LiveData<List<String>> = _recentSearches

    private val _refreshingArrivals = MutableLiveData<Boolean>()
    val refreshingArrivals: LiveData<Boolean> = _refreshingArrivals

    private val _refreshingDepartures = MutableLiveData<Boolean>()
    val refreshingDepartures: LiveData<Boolean> = _refreshingDepartures

    init {
        station.observeForever { station ->
            state.set(KEY_STATION, station.name)
            simplePrefs.put(KEY_STATION, station.name)
        }
        _station.value = Station(state.get(KEY_STATION) ?: simplePrefs.get(KEY_STATION, DEFAULT_STATION))
        _recentSearches.value = searchHistory.getList()
    }

    fun setStation(newStation: Station) {
        if (_station.value != newStation) {
            _station.value = newStation
            addToRecentSearches(newStation.name)
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
                    emit(TimeTableState.Success(loadArriving(station)))
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Failed to load arriving timetable for ${station.name}", e)
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
                    emit(TimeTableState.Success(loadDeparting(station)))
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Failed to load departing timetable for ${station.name}", e)
                    emit(TimeTableState.Error(e))
                } finally {
                    _refreshingDepartures.postValue(false)
                }
            }
        }

    private suspend fun loadArriving(station: Station): List<TrainStatus> {
        return withContext(Dispatchers.IO) {
            val timeTable = timeTablesService.getArrivalsTimeTable(station)
            timeTable.trainStatuses.filter { it.trainCode.isNotBlank() }
        }
    }

    private suspend fun loadDeparting(station: Station): List<TrainStatus> {
        return withContext(Dispatchers.IO) {
            val timeTable = timeTablesService.getDeparturesTimeTable(station)
            timeTable.trainStatuses.filter { it.trainCode.isNotBlank() }
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
            return TimeTablesViewModel(handle, simplePrefs, searchHistory) as T
        }
    }

    companion object {
        const val LOG_TAG = "TimeTablesViewModel"

        internal const val KEY_STATION = "station"
        internal const val DEFAULT_STATION = "СОФИЯ"
    }
}