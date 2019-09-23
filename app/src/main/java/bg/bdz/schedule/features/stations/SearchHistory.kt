package bg.bdz.schedule.features.stations

import android.util.JsonReader
import bg.bdz.schedule.data.SimplePreferences
import org.json.JSONArray
import java.io.StringReader

/**
 * Created by Svetlozar Kostadinov on 19.09.19.
 */
open class SearchHistory(
    private val simplePreferences: SimplePreferences
) {
    private val cachedSearches: MutableList<String> by lazy {
        simplePreferences.get(KEY_RECENT_SEARCHES, EMPTY_JSON_ARRAY)
            .readToList()
            .toMutableList()
    }

    fun add(newSearch: String) {
        val existing = cachedSearches.find { it.equals(newSearch, ignoreCase = true) }
        if (existing != null) {
            // Remove an older search so that it can be placed at first position
            cachedSearches -= existing
        }

        // Place the new search at first position
        cachedSearches.add(0, newSearch)

        // Store list as a JSON array
        simplePreferences.put(KEY_RECENT_SEARCHES, cachedSearches.writeToJson())
    }

    fun getList(): List<String> {
        return cachedSearches
    }

    // Json read / write

    private fun List<String>.writeToJson(): String {
        return JSONArray(this).toString()
    }

    private fun String.readToList(): List<String> {
        val list = mutableListOf<String>()

        JsonReader(StringReader(this)).use { jsonReader ->
            jsonReader.beginArray()
            while (jsonReader.hasNext()) {
                list.add(jsonReader.nextString())
            }
            jsonReader.endArray()
        }

        return list
    }

    companion object {
        const val MAX_RECENT_SEARCHES = 5
        private const val EMPTY_JSON_ARRAY = "[]"
        private const val KEY_RECENT_SEARCHES = "recentSeaches"
    }
}