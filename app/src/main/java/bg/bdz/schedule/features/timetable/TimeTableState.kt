package bg.bdz.schedule.features.timetable

import bg.bdz.schedule.models.TrainStatus

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
sealed class TimeTableState {
    object Loading : TimeTableState()
    data class Error(val exception: Exception) : TimeTableState()
    data class Success(val trains: List<TrainStatus>) : TimeTableState()
}