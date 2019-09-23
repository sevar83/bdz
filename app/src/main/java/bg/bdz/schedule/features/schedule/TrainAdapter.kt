package bg.bdz.schedule.features.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import bg.bdz.schedule.R
import bg.bdz.schedule.models.Train

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
class TrainAdapter : ListAdapter<Train, TrainViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_train, parent, false)
        return TrainViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : ItemCallback<Train>() {
            override fun areItemsTheSame(oldItem: Train, newItem: Train): Boolean {
                return oldItem.departTime == newItem.departTime
            }

            override fun areContentsTheSame(oldItem: Train, newItem: Train): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldItem == newItem
            }
        }
    }
}