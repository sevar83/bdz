package bg.bdz.schedule.features.timetable

import bg.bdz.schedule.models.TrainStatus

sealed class TimeTableState {
    data object Loading : TimeTableState()
    data class Error(val exception: Exception) : TimeTableState()
    data class Success(val trains: List<TrainStatus>) : TimeTableState()
}