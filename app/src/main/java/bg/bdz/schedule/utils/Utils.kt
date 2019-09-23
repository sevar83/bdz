package bg.bdz.schedule.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.Window
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment



/**
 * Created by Svetlozar Kostadinov on 16.09.19.
 */

fun View.setOnSingleClickListener(block: () -> Unit) {
    setOnClickListener(OnSingleClickListener(block))
}

class OnSingleClickListener(private val block: () -> Unit) : View.OnClickListener {

    private var lastClickTime = 0L

    override fun onClick(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        block()
    }
}

fun <F : Fragment> F.withArgs(vararg pairs: Pair<String, Any?>): F {
    if (arguments == null) {
        arguments = Bundle()
    }
    requireNotNull(arguments).putAll(bundleOf(*pairs))

    return this
}

fun Collection<String>.containsIgnoreCase(soughtFor: String): Boolean {
    for (current in this) {
        if (current.equals(soughtFor, ignoreCase = true)) {
            return true
        }
    }
    return false
}

@ColorInt
fun Context.getThemeColor(themeAttributeId: Int): Int {
    val outValue = TypedValue()
    val wasResolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    return if (wasResolved) {
        ContextCompat.getColor(this, outValue.resourceId)
    } else {
        Color.RED
    }
}

fun Window.setLightStatusBar(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR   // add LIGHT_STATUS_BAR to flag
        decorView.systemUiVisibility = flags
        statusBarColor = color // optional
    }
}

val Activity.isNightMode get(): Boolean {
    val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return when (currentNightMode) {
        Configuration.UI_MODE_NIGHT_NO -> false    // Night mode is not active, we're using the light theme
        Configuration.UI_MODE_NIGHT_YES -> true    // Night mode is active, we're using dark theme
        else -> false
    }

}