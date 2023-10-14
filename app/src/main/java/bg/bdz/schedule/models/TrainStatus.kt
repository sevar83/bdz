package bg.bdz.schedule.models

import pl.droidsonroids.jspoon.annotation.Selector

data class TrainStatus(
    @Selector("div.col-3")
    var dateTime: String = "",

    @Selector("strong")
    var station: String = "",

    @Selector("span[data-toggle=tooltip]")
    var trainCode: String = "",

    @Selector("div[data-station-id]")
    var track: String? = null,

    @Selector("div.col-lg-3")
    var comments: String? = null,
)