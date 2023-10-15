package bg.bdz.schedule.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import java.util.Locale

sealed class AppLocale(val locale: Locale) {
    data object Bg: AppLocale(Locale("bg"))
    data object En: AppLocale(Locale("en"))
}

/**
 * Set application locale manually.
 *
 * Inspired with: https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
 *
 * Source: https://gist.github.com/Swisyn/08d7f1f2e04d84f7bd1bbfa2e7196099
 */
fun Application.setLocale(appLocale: AppLocale) {
    val locale = appLocale.locale
    setLocaleInternal(locale)
    registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks(locale))
    registerComponentCallbacks(ApplicationCallbacks(this, locale))
}

private fun Context.setLocaleInternal(locale: Locale) {
    Locale.setDefault(locale)

    val resources = this.resources
    val currentLocale = resources.configuration.locales.get(0)
    if (currentLocale != locale) {
        val config = resources.configuration.apply {
            setLocale(locale)
        }
        // createConfigurationContext doesn't fit to our purpose
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

private class ActivityLifecycleCallbacks(private val locale: Locale) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.setLocaleInternal(locale)
    }

    // <editor-fold desc="Unused callbacks">
    override fun onActivityStarted(activity: Activity) { /* do nothing */ }

    override fun onActivityResumed(activity: Activity) { /* do nothing */ }

    override fun onActivityPaused(activity: Activity) { /* do nothing */ }

    override fun onActivityStopped(activity: Activity) { /* do nothing */ }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { /* do nothing */ }

    override fun onActivityDestroyed(activity: Activity) { /* do nothing */ }
    // </editor-fold>
}

private class ApplicationCallbacks(private val context: Context, private val locale: Locale) : ComponentCallbacks {

    override fun onConfigurationChanged(newConfig: Configuration) {
        context.setLocaleInternal(locale)
    }

    override fun onLowMemory() { /* do nothing */ }
}