package bg.bdz.schedule.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationEntry(
    val id: String,
    val slug: Station.Slug,
    @SerialName("name_en") val nameEn: String,
    @SerialName("name_bg") val nameBg: String,
)