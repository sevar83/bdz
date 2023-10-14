package bg.bdz.schedule.features.stations

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import bg.bdz.schedule.R
import bg.bdz.schedule.features.base.SimpleArrayAdapter
import bg.bdz.schedule.models.Station
import bg.bdz.schedule.utils.getThemeColor


/**
 * [SimpleArrayAdapter] that stores a small list of recently searched strings at the beginning of
 * its [mOriginalValues] array. Each new search is placed at the first index.
 */
class StationsAdapter(
    context: Context,
    @LayoutRes itemLayout: Int = android.R.layout.simple_list_item_1,
    originalItems: List<Station> = emptyList(),
    @DrawableRes private val recentsIconRes: Int = R.drawable.ic_history,
    private val maxRecentsCount: Int
) : SimpleArrayAdapter<Station, StationsAdapter.StationViewHolder>(
    context,
    itemLayout,
    ArrayList(originalItems)
) {
    private val recentsIcon: Drawable = requireNotNull(AppCompatResources.getDrawable(context, recentsIconRes))
    @ColorInt private val recentsIconColor: Int = context.getThemeColor(com.google.android.material.R.attr.colorSecondary)

    init {
        require(maxRecentsCount >= 0) { "Non-negative maxRecentsCount expected" }
    }

    /**
     * A list of recently searched string that are always prepended at the beginning of the list.
     * Only a limitted count equal to [maxRecentsCount] are shown.
     */
    private var recents: Set<Station> = emptySet()

    class StationViewHolder : ViewHolder() {
        lateinit var textView: TextView
    }

    override fun createViewHolder(itemView: View, position: Int): StationViewHolder {
        return StationViewHolder().apply {
            textView = itemView as TextView
        }
    }

    override fun bindViewHolder(viewHolder: StationViewHolder, item: Station, position: Int) {
        viewHolder.textView.text = item.name
        when {
            recents.find { it.name.equals(item.name, ignoreCase = true) } != null -> {
                // Recent search item
                viewHolder.textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, recentsIcon, null)
                viewHolder.textView.compoundDrawables[2].setTint(recentsIconColor)
                return
            }
            item.name.length == 1 -> {
                // Alphabetic section (e.g. "A")
                viewHolder.textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            else -> {
                // Station item
                viewHolder.textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }
    }

    fun findStation(name: String): Station? {
        return (0 until count)
            .asSequence()
            .map(::getItem)
            .filterNotNull()
            .filter { name.length > 1 }
            .find { current -> current.name.equals(name, ignoreCase = true) }
    }

    fun setRecentSearches(recents: List<Station>) {
        synchronized (mLock) {
            // Remove any existing recent search items from the beginning of list
            this.recents.forEachIndexed { index, _ -> removeAt(index) }

            // Add a limited count of the new searches list at the beginning
            val results = recents
                .toSet()
                .take(maxRecentsCount)
                .apply {
                    reversed().forEach { insert(it, 0) }
                }
                .toSet()

            this.recents = results
        }
    }
}