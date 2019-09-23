package bg.bdz.schedule.features.timetable

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import bg.bdz.schedule.R
import bg.bdz.schedule.models.TrainStatus
import bg.bdz.schedule.utils.getThemeColor
import kotlinx.android.synthetic.main.item_train_status.view.*

class TrainStatusViewHolder(
    itemView: View,
    private val isArriving: Boolean
) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(trainStatus: TrainStatus) {
        itemView.trainCodeTextView.text = trainStatus.trainCode

        // Attempt to extract "16:30" from "08.sep 16:30"
        val dateAndTime = trainStatus.dateTime.split(' ', limit = 2)
        if (dateAndTime.isNotEmpty()) {
            itemView.dateTimeTextView.text = dateAndTime[dateAndTime.size - 1]
        } else {
            // Fallback
            itemView.dateTimeTextView.text = trainStatus.dateTime
        }

        @StringRes val fromOrTo = if (isArriving) R.string.from_s else R.string.to_s
        itemView.stationTextView.text = itemView.context.getString(fromOrTo, trainStatus.station)
        itemView.trackTextView.text = trainStatus.track.toString()

        var comments = trainStatus.comments
        comments = comments?.replace("  Очаква", "\nОчаква")
        comments = comments?.replace("в  ", "в ")
        comments = comments?.replace("навреме", "Навреме")
        itemView.commentsTextView.text = comments
        itemView.commentsTextView.isVisible = comments?.isNotBlank() ?: false

        val isLate = trainStatus.comments?.contains("късн") ?: false
        @ColorInt val commentsColor = when (isLate) {
            true -> itemView.context.getThemeColor(R.attr.colorError)
            false -> itemView.context.getThemeColor(android.R.attr.textColorSecondary)
        }
        itemView.commentsTextView.setTextColor(commentsColor)
    }
}