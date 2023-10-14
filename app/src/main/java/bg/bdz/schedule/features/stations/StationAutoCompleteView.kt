package bg.bdz.schedule.features.stations

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import bg.bdz.schedule.features.base.InstantAutoCompleteTextView

class StationAutoCompleteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InstantAutoCompleteTextView(context, attrs, defStyleAttr) {

    var onActionListener: (() -> Unit)? = null

    init {
        setOnEditorActionListener { _, actionId, event: KeyEvent? ->
            // Identifier of the action. This will be either the identifier you supplied,
            // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event?.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                onActionListener?.invoke()
                true
            } else {
                false
            }
        }
    }
}