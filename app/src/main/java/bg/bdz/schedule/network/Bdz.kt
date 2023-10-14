package bg.bdz.schedule.network

import android.content.Context
import bg.bdz.schedule.di.Dependencies
import bg.bdz.schedule.models.Station
import bg.bdz.schedule.models.StationEntry
import bg.bdz.schedule.serialization.TimeTableConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.create

object Bdz {

    private fun createOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(createLoggingInterceptor())
        .build()

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    internal fun createRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .client(createOkHttpClient())
        .baseUrl(baseUrl)
        .addConverterFactory(JspoonConverterFactory.create())
        .addConverterFactory(TimeTableConverterFactory())
        .build()

    val scheduleService by lazy<ScheduleService> {
        createRetrofit("https://razpisanie.bdz.bg").create()
    }

    val timeTablesService by lazy<TimeTablesService> {
        createRetrofit("https://live.bdz.bg").create()
    }

    val stations: List<Station> by lazy {
        loadStations(context = Dependencies.provideAppContext())
            .map {
                Station(
                    slug = it.slug,
                    name = it.nameBg
                )
            }
    }

    val stationsBySlugs: Map<Station.Slug, Station> by lazy { stations.associateBy(Station::slug) }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadStations(context: Context): List<StationEntry> {
        val assetStream = context.assets.open("stations.json")
        return Json.decodeFromStream<List<StationEntry>>(assetStream)
    }
}