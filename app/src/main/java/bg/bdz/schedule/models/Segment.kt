package bg.bdz.schedule.models

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * Created by Svetlozar Kostadinov on 19.09.19.
 */
class Segment {
    @Selector("td:nth-child(1) > a:nth-child(1)") lateinit var from: String
    @Selector("td:nth-child(1) > a:nth-child(3)") lateinit var to: String
    @Selector("td:nth-child(2) > a") lateinit var trainCode: String
    //val composition: Any
    @Selector("td:nth-child(4)") lateinit var departTime: String
    @Selector("td:nth-child(5)") lateinit var arriveTime: String
}