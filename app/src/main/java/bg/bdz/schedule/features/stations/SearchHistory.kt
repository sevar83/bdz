package bg.bdz.schedule.features.stations

import android.util.JsonReader
import bg.bdz.schedule.data.SimplePreferences
import bg.bdz.schedule.models.Station
import bg.bdz.schedule.network.Bdz
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.StringReader

open class SearchHistory(
    private val simplePreferences: SimplePreferences
) {
    private val recentStations: MutableSet<Station.Slug> by lazy {
        simplePreferences.get(KEY_RECENT_SEARCHES, EMPTY_JSON_ARRAY)
            .readToList()
            .toMutableSet()
    }

    private val json = Json

    fun add(recent: Station.Slug) {
        val existing = recentStations.find { it == recent }
        if (existing != null) {
            // Remove an older search so that it can be placed at first position
            recentStations -= existing
        }

        recentStations.add(recent)

        // Store list as a JSON array
        simplePreferences.put(KEY_RECENT_SEARCHES, recentStations.writeToJson())
    }

    fun getList(): List<Station> = recentStations.reversed().mapNotNull(Bdz.stationsBySlugs::get)

    // Json read / write

    private fun Set<Station.Slug>.writeToJson(): String = json.encodeToString(this)

    private fun String.readToList(): Set<Station.Slug> {
        val set = mutableSetOf<Station.Slug>()

        JsonReader(StringReader(this)).use { jsonReader ->
            jsonReader.beginArray()
            while (jsonReader.hasNext()) {
                val slug = Station.Slug(jsonReader.nextString())
                set.add(slug)
            }
            jsonReader.endArray()
        }

        return set
    }

    companion object {
        const val MAX_RECENTS = 5
        private const val EMPTY_JSON_ARRAY = "[]"
        private const val KEY_RECENT_SEARCHES = "recentSeaches"
    }
}