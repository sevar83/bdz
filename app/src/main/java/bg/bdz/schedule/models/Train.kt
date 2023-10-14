package bg.bdz.schedule.models

import androidx.annotation.Keep
import bg.bdz.schedule.serialization.RouteSplittingConverter
import bg.bdz.schedule.serialization.TimesSplittingConverter
import pl.droidsonroids.jspoon.annotation.Selector

data class Train(
    @Selector("div.row div.col span.badge")
    var ordinal: Int = 0,

    @Selector("div.schedule-table > div.row :containsOwn( - )", attr="html", defValue = "", converter = RouteSplittingConverter::class)
    var stations: List<String> = emptyList(),

    @Selector("b.schedule-time", converter = TimesSplittingConverter::class)
    var times: List<String> = emptyList(),

    // TODO
    var transferCount: Int = 0,

    @Selector("div.schedule-table :containsOwn( ч.)", defValue = "0:00 ч.")
    var duration: String = "",

    @Selector("div.schedule-note")
    var note: String = "",

    @Selector("div.schedule-changes > div.row")
    var waypoints: List<WaypointStation> = emptyList(),

    @Selector("div.schedule-table span.train-number")
    var trainNumber: String = ""
)