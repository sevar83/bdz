package bg.bdz.schedule.features

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import bg.bdz.schedule.R
import bg.bdz.schedule.utils.getThemeColor
import bg.bdz.schedule.utils.isNightMode
import bg.bdz.schedule.utils.setLightStatusBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val windowBackgroundColor = getThemeColor(android.R.attr.windowBackground)
        if (!isNightMode) {
            window.setLightStatusBar(windowBackgroundColor)
        } else {
            window.statusBarColor = windowBackgroundColor
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_timetable,
            R.id.navigation_schedule,
            R.id.navigation_notifications
        ))
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
