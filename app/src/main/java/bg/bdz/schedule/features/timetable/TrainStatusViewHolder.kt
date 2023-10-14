package bg.bdz.schedule.features.timetable

import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import bg.bdz.schedule.R
import bg.bdz.schedule.databinding.ItemTrainStatusBinding
import bg.bdz.schedule.models.TrainStatus
import bg.bdz.schedule.utils.getThemeColor

class TrainStatusViewHolder(
    private val binding: ItemTrainStatusBinding,
    private val isArriving: Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(trainStatus: TrainStatus) = with(binding) {
        trainCodeTextView.text = trainStatus.trainCode

        // Attempt to extract "16:30" from "08.sep 16:30"
        val dateAndTime = trainStatus.dateTime.split(' ', limit = 2)
        if (dateAndTime.isNotEmpty()) {
            dateTimeTextView.text = dateAndTime[dateAndTime.size - 1]
        } else {
            // Fallback
            dateTimeTextView.text = trainStatus.dateTime
        }

        @StringRes val fromOrTo = if (isArriving) R.string.from_s else R.string.to_s
        stationTextView.text = itemView.context.getString(fromOrTo, trainStatus.station)
        trackTextView.text = trainStatus.track.toString()

        var comments = trainStatus.comments
        comments = comments?.replace("  Очаква", "\nОчаква")
        comments = comments?.replace("в  ", "в ")
        comments = comments?.replace("навреме", "Навреме")
        commentsTextView.text = comments
        commentsTextView.isVisible = comments?.isNotBlank() ?: false

        val isLate = trainStatus.comments?.contains("късн") ?: false
        @ColorInt val commentsColor = when (isLate) {
            true -> itemView.context.getThemeColor(com.google.android.material.R.attr.colorError)
            false -> itemView.context.getThemeColor(android.R.attr.textColorSecondary)
        }
        commentsTextView.setTextColor(commentsColor)
    }
}