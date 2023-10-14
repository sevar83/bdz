package bg.bdz.schedule.models

import pl.droidsonroids.jspoon.annotation.Selector

data class WaypointStation(
    // Does not work with "div:eq(0)" for some reason
    @Selector("div.truncate")
    var name: String = "",

    @Selector("div.col-3", index = 0)
    var arriveTime: String? = null,

    // Do not use :eq(2) as it may select the parent before the child, because parent div also has sibling index of 2 (it is second inside it's own parent)
    @Selector("div.col-3", index = 1)
    var departTime: String? = null,

    @Selector("span.train-number")
    var trainCode: String = "",
)