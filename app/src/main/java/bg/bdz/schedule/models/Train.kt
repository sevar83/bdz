package bg.bdz.schedule.models

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
class Train {
    @Selector("#myid > td:nth-child(1)") var ordinal: Int = 0
    @Selector("#myid > td:nth-child(2)") lateinit var departTime: String
    @Selector("#myid > td:nth-child(3)") lateinit var arriveTime: String
    @Selector("#myid > td:nth-child(4)") lateinit var category: String
    @Selector("#myid > td:nth-child(5)") var transferCount: Int = 0
    @Selector("#myid > td:nth-child(6)") lateinit var duration: String
    @Selector("#myid > td:nth-child(7)") lateinit var comments: String

    @Selector("div.cont tr[align=left]") lateinit var segments: List<Segment>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Train

        if (ordinal != other.ordinal) return false
        if (departTime != other.departTime) return false
        if (arriveTime != other.arriveTime) return false
        if (category != other.category) return false
        if (transferCount != other.transferCount) return false
        if (duration != other.duration) return false
        if (comments != other.comments) return false
        if (segments != other.segments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ordinal
        result = 31 * result + departTime.hashCode()
        result = 31 * result + arriveTime.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + transferCount
        result = 31 * result + duration.hashCode()
        result = 31 * result + comments.hashCode()
        result = 31 * result + segments.hashCode()
        return result
    }

}