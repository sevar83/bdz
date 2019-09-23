package bg.bdz.schedule.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import bg.bdz.schedule.BdzApp
import bg.bdz.schedule.data.SimplePreferences
import bg.bdz.schedule.features.stations.SearchHistory

object Dependencies {

    fun provideApp(): BdzApp = BdzApp.INSTANCE

    fun provideAppContext(): Context = BdzApp.applicationContext

    fun provideSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun provideSimplePreferences(): SimplePreferences =
        SimplePreferences(provideSharedPreferences(provideAppContext()))

    fun provideSearchHistory(): SearchHistory =
        object : SearchHistory(provideSimplePreferences()) {}
}