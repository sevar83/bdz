package bg.bdz.schedule.features.schedule

import bg.bdz.schedule.models.Train

sealed class ScheduleState {
    data object Loading : ScheduleState()
    data class Error(val exception: Exception) : ScheduleState()
    data class Success(val trains: List<Train>) : ScheduleState()
}