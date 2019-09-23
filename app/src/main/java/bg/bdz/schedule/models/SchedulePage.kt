package bg.bdz.schedule.models

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
class SchedulePage {
    @Selector("#myAccordion > div") var trains: List<Train> = emptyList()
}