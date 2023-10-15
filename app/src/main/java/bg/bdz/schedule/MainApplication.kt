package bg.bdz.schedule;

import android.app.Application
import android.content.Context
import bg.bdz.schedule.utils.AppLocale
import bg.bdz.schedule.utils.setLocale

class BdzApp : Application() {

    init { INSTANCE = this }

    override fun onCreate() {
        super.onCreate()
        setLocale(AppLocale.Bg)
    }

    companion object {
        lateinit var INSTANCE: BdzApp
            private set

        val applicationContext: Context get() { return INSTANCE.applicationContext }
    }
}