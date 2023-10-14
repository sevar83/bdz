package bg.bdz.schedule.models

import kotlinx.serialization.Serializable

data class Station(
    val name: String,
    val slug: Slug,
) {
    /**
     * The http-friendly name of the station. Used as a unique identifier.
     */
    @Serializable
    @JvmInline
    value class Slug(val value: String) {
        override fun toString(): String = value
    }

    override fun toString(): String = name
}