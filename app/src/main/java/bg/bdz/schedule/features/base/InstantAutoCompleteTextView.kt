package bg.bdz.schedule.features.base

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import bg.bdz.schedule.utils.KeyboardUtils

/**
 * Created by PasiMatalamaki on 7.9.2015.
 */
open class InstantAutoCompleteTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatAutoCompleteTextView(context, attrs, defStyleAttr) {

    private var lastValidText: String? = null

    var isDropDownAlwaysVisible: Boolean = false
        set(visible) {
            field = visible
            if (visible) {
                showDropDownIfFocused()
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        showDropDownIfFocused()
    }

    override fun enoughToFilter(): Boolean {
        return isDropDownAlwaysVisible /*|| super.enoughToFilter()*/
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        showDropDownIfFocused()

        if (!focused) {
            validate()
        }
    }

    fun approveAsValid() {
        lastValidText = text.toString()
    }

    private fun validate() {
        if (lastValidText != text.toString()) {
            setText(lastValidText)
        }
    }

    private fun showDropDownIfFocused() {
        if (enoughToFilter() && isFocused && windowVisibility == View.VISIBLE) {
            performFiltering("", 0)
            showDropDown()
        }
    }

    fun dismissKeybardAndClearFocus(activity: Activity) {
        if (lastValidText == text.toString()) {
            KeyboardUtils.hideSoftKeyboard(activity)
            clearFocus()
        }
        (filter as? OnDismissListener)?.onDismiss()
    }

    fun refreshFiltering() {
        text?.toString()?.let {
            performFiltering(it, 0)
        }
    }
}