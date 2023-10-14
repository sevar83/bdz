package bg.bdz.schedule.features.timetable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import bg.bdz.schedule.databinding.ItemTrainStatusBinding
import bg.bdz.schedule.models.TrainStatus

class TrainStatusAdapter(
    private val isArriving: Boolean
) : ListAdapter<TrainStatus, TrainStatusViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainStatusViewHolder {
        val itemBinding = ItemTrainStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrainStatusViewHolder(itemBinding, isArriving)
    }

    override fun onBindViewHolder(holder: TrainStatusViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : ItemCallback<TrainStatus>() {
            override fun areItemsTheSame(oldItem: TrainStatus, newItem: TrainStatus): Boolean {
                return oldItem.trainCode == newItem.trainCode
            }

            override fun areContentsTheSame(oldItem: TrainStatus, newItem: TrainStatus): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldItem == newItem
            }
        }
    }
}