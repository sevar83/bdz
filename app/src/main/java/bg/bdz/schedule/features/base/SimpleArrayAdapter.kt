package bg.bdz.schedule.features.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import bg.bdz.schedule.utils.ArrayAdapter

/**
 * Created by Svetlozar Kostadinov on 07.09.19.
 */
abstract class SimpleArrayAdapter<T, VH : SimpleArrayAdapter.ViewHolder>(
    context: Context,
    @LayoutRes itemLayout: Int,
    originalItems: List<T> = emptyList()
) : ArrayAdapter<T>(context, itemLayout, originalItems) {

    open class ViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: VH
        val itemView: View
        if (convertView == null) {
            itemView = super.getView(position, convertView, parent)
            viewHolder = createViewHolder(itemView, position)
            itemView.tag = viewHolder
        } else {
            itemView = convertView
            viewHolder = convertView.tag as VH
        }

        val item = getItem(position)
        if (item != null) {
            bindViewHolder(viewHolder, item, position)
        }

        return itemView
    }

    abstract fun createViewHolder(itemView: View, position: Int): VH
    abstract fun bindViewHolder(viewHolder: VH, item: T, position: Int)
}