package bg.bdz.schedule.models

/**
 * Created by Svetlozar Kostadinov on 8/29/2019.
 */
data class TrainStatus(
    val trainCode: String,
    val dateTime: String,
    val station: String,
    val track: String?,
    val comments: String?
)