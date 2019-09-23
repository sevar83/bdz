package bg.bdz.schedule;

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen

class BdzApp : Application() {

    init { INSTANCE = this }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
    }

    companion object {
        lateinit var INSTANCE: BdzApp
            private set

        val applicationContext: Context get() { return INSTANCE.applicationContext }
    }
}