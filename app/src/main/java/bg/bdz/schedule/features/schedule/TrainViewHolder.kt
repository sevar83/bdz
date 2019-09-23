package bg.bdz.schedule.features.schedule

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import bg.bdz.schedule.R
import bg.bdz.schedule.models.Train
import kotlinx.android.synthetic.main.item_train.view.*

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
class TrainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(itemView.context)

    fun bindTo(train: Train) {
        itemView.departTimeTextView.text = train.departTime
        itemView.arriveTimeTextView.text = train.arriveTime

        // Convert "12:34" to "12 часа 34 мин"
        val hours = train.duration.split(":")[0].toInt()
        val hoursText = if (hours == 0) "" else
            itemView.context.resources.getQuantityString(R.plurals.hours, hours, hours)
        val minutes = train.duration.split(":")[1].toInt()
        val minutesText = if (minutes == 0) "" else
            itemView.context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        itemView.durationTextView.text = itemView.resources.getString(
            R.string.duration_hours_minutes, hoursText, minutesText)

        // Transfers

        itemView.transfersTextView.isVisible = train.transferCount > 0
        itemView.transfersTextView.text = itemView.context.resources
            .getQuantityString(R.plurals.d_transfers, train.transferCount, train.transferCount)

        // Segments

        val withTransfers = train.segments.size > 1
        itemView.segmentsLinearLayout.isVisible = withTransfers
        if (withTransfers) {
            repopulateSegmentViews(train)
        } else {
            itemView.requestLayout()
        }
    }

    private fun repopulateSegmentViews(train: Train) {
        itemView.segmentsLinearLayout.removeAllViews()
        val segmentsCount = train.segments.size
        train.segments.forEachIndexed { index, segment ->
            val segmentItemView =
                layoutInflater.inflate(R.layout.item_segment, itemView.segmentsLinearLayout, false)
            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            itemView.segmentsLinearLayout.addView(segmentItemView, layoutParams)

            // From

            val fromTextView: TextView = segmentItemView.findViewById(R.id.segmentFromTextView)
            fromTextView.text = segment.from + " \u2192 "
            var style = when (index) {
                0 -> Typeface.BOLD
                else -> Typeface.NORMAL
            }
            fromTextView.setTypeface(Typeface.DEFAULT, style)

            // To

            val toTextView: TextView = segmentItemView.findViewById(R.id.segmentToTextView)
            toTextView.text = segment.to
            style = when (index) {
                segmentsCount-1 -> Typeface.BOLD
                else -> Typeface.NORMAL
            }
            toTextView.setTypeface(Typeface.DEFAULT, style)

            // Train type

            val trainTypeTextView: TextView = segmentItemView.findViewById(R.id.segmentTrainType)
            val trainType: String = segment.trainCode.replace(NO_DIGITS, "")
            trainTypeTextView.isVisible = trainType.isNotBlank()
            trainTypeTextView.text = trainType

            // Departure time

            val departTextView: TextView = segmentItemView.findViewById(R.id.segmentDepartTime)
            departTextView.text = segment.departTime

            // Arrival time

            val arriveTextView: TextView = segmentItemView.findViewById(R.id.segmentArrivеTime)
            arriveTextView.text = segment.arriveTime
        }
    }

    companion object {
        private val NO_DIGITS = Regex("[0-9]*")
    }
}