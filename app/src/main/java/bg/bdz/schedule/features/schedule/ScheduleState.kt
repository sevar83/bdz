package bg.bdz.schedule.features.schedule

import bg.bdz.schedule.models.Train

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
sealed class ScheduleState {
    object Loading : ScheduleState()
    data class Error(val exception: Exception) : ScheduleState()
    data class Success(val trains: List<Train>) : ScheduleState()
}