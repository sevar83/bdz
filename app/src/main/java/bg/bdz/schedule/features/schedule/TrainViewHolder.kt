package bg.bdz.schedule.features.schedule

import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import bg.bdz.schedule.R
import bg.bdz.schedule.databinding.ItemTrainBinding
import bg.bdz.schedule.databinding.ItemWaypointBinding
import bg.bdz.schedule.models.Train

class TrainViewHolder(private val binding: ItemTrainBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(train: Train) = with(binding) {
        departTimeTextView.text = train.times[0]
        arriveTimeTextView.text = train.times[1]
        // TODO: Add intermediate stations if any

        // Convert "12:34 ч." to "12 часа 34 мин"
        val nonDigitsRegex = Regex("[^0-9]")
        val timeChunks = train.duration.split(":")
        val hours = timeChunks[0].toInt()
        val hoursText = if (hours == 0) "" else
            itemView.context.resources.getQuantityString(R.plurals.hours, hours, hours)
        // E.g. "34 ч." to "34"
        val minutes = nonDigitsRegex.replace(timeChunks[1], "").toInt()
        val minutesText = if (minutes == 0) "" else
            itemView.context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        durationTextView.text = itemView.resources.getString(
            R.string.duration_hours_minutes, hoursText, minutesText)

        // Transfers

        transfersTextView.isVisible = train.transferCount > 0
        transfersTextView.text = itemView.context.resources
            .getQuantityString(R.plurals.d_transfers, train.transferCount, train.transferCount)

        // Waypoints

        val withTransfers = train.waypoints.size > 1
        waypointsLinearLayout.isVisible = withTransfers
        if (withTransfers) {
            repopulateWaypointViews(train)
        } else {
            itemView.requestLayout()
        }
    }

    private fun repopulateWaypointViews(train: Train) = with(binding) {
        val inflater = LayoutInflater.from(binding.root.context)

        waypointsLinearLayout.removeAllViews()
        val waypointsCount = train.waypoints.size
        train.waypoints.forEachIndexed { _, waypoint ->
            val waypointItemBinding = ItemWaypointBinding.inflate(inflater, waypointsLinearLayout, false)
            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            waypointsLinearLayout.addView(waypointItemBinding.root, layoutParams)

            with (waypointItemBinding) {
                // Name

                waypointNameTextView.text = waypoint.name

                // Train type

                val trainType: String = waypoint.trainCode.replace(NO_DIGITS, "")
                waypointTrainType.isVisible = trainType.isNotBlank()
                waypointTrainType.text = trainType

                // Arrival time

                waypointArriveTime.text = waypoint.arriveTime

                // Departure time

                waypointDepartTime.text = waypoint.departTime
            }
        }
    }

    companion object {
        private val NO_DIGITS = Regex("[0-9]*")
    }
}