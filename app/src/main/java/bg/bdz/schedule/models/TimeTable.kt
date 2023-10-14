package bg.bdz.schedule.models

import pl.droidsonroids.jspoon.annotation.Selector

data class TimeTable(
    @Selector("div.timetableItem > div.row")
    var trainStatuses: List<TrainStatus> = emptyList()
)