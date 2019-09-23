package bg.bdz.schedule.models

/**
 * Created by Svetlozar Kostadinov on 9/3/2019.
 */
data class TimeTable(
    val trainStatuses: List<TrainStatus> = emptyList()
)