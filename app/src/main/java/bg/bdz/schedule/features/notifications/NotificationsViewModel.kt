package bg.bdz.schedule.features.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Get notifications about schedule changes and delays in your daily commute"
    }
    val text: LiveData<String> = _text
}