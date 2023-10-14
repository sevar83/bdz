package bg.bdz.schedule.data

import android.content.SharedPreferences

class SimplePreferences(
    val preferences: SharedPreferences
) {

    fun get(key: String, default: String): String = preferences.getString(key, default)!!
    fun get(key: String, default: Set<String>): Set<String> = preferences.getStringSet(key, default)!!
    fun get(key: String, default: Int): Int = preferences.getInt(key, default)
    fun get(key: String, default: Float): Float = preferences.getFloat(key, default)
    fun get(key: String, default: Long): Long = preferences.getLong(key, default)
    fun get(key: String, default: Boolean): Boolean = preferences.getBoolean(key, default)

    fun put(key: String, value: String) = preferences.editor { putString(key, value) }
    fun put(key: String, value: Set<String>) = preferences.editor { putStringSet(key, value) }
    fun put(key: String, value: Int) = preferences.editor { putInt(key, value) }
    fun put(key: String, value: Float) = preferences.editor { putFloat(key, value) }
    fun put(key: String, value: Long) = preferences.editor { putLong(key, value) }
    fun put(key: String, value: Boolean) = preferences.editor { putBoolean(key, value) }

    private inline fun SharedPreferences.editor(editor: SharedPreferences.Editor.() -> Unit) {
        edit().apply { editor(this) }.apply()
    }

    inline fun editMultiple(doStuff: () -> Unit) {
        val prefs = preferences
        prefs.edit().apply {
            doStuff()
        }.apply()
    }

    fun hasPreference(key: String) = preferences.contains(key)
}