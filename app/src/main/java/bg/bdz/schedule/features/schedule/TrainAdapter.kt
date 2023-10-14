package bg.bdz.schedule.features.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import bg.bdz.schedule.databinding.ItemTrainBinding
import bg.bdz.schedule.models.Train

class TrainAdapter : ListAdapter<Train, TrainViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val itemBinding = ItemTrainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrainViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : ItemCallback<Train>() {
            override fun areItemsTheSame(oldItem: Train, newItem: Train): Boolean {
                return oldItem.times == newItem.times
            }

            override fun areContentsTheSame(oldItem: Train, newItem: Train): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldItem == newItem
            }
        }
    }
}