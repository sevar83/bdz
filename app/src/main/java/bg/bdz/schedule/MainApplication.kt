package bg.bdz.schedule;

import android.app.Application
import android.content.Context

class BdzApp : Application() {

    init { INSTANCE = this }

    companion object {
        lateinit var INSTANCE: BdzApp
            private set

        val applicationContext: Context get() { return INSTANCE.applicationContext }
    }
}