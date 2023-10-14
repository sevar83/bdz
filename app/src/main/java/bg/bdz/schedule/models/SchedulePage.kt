package bg.bdz.schedule.models

import androidx.annotation.Keep
import pl.droidsonroids.jspoon.annotation.Selector

data class SchedulePage(
    @Selector("#content > div.container div.card-schedule")
    var trains: List<Train> = emptyList()
)