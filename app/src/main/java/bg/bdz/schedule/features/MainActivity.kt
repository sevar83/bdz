package bg.bdz.schedule.features

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import bg.bdz.schedule.R
import bg.bdz.schedule.databinding.ActivityMainBinding
import bg.bdz.schedule.utils.getThemeColor
import bg.bdz.schedule.utils.isNightMode
import bg.bdz.schedule.utils.setLightStatusBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setupStatusBar()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController)
    }

    private fun setupStatusBar() {
        val windowBackgroundColor = getThemeColor(android.R.attr.windowBackground)
        if (!isNightMode) {
            window.setLightStatusBar(windowBackgroundColor)
        } else {
            window.statusBarColor = windowBackgroundColor
        }
    }
}
