package bg.bdz.schedule.network

import android.annotation.SuppressLint
import bg.bdz.schedule.models.SchedulePage
import bg.bdz.schedule.models.Station
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.format.DateTimeFormatter

interface ScheduleService {

    @GET("/{language}/{fromStation}/{toStation}/{date}")
    suspend fun getSchedulePage(
        @Path(value = "language") language: String = "bg",                  // e.g. "bg" or "en"
        @Path(value = "fromStation") fromStation: Station.Slug,             // e.g. "sofia"
        @Path(value = "toStation") toStation: Station.Slug,                 // e.g. "varna"
        @Path(value = "date") date: String = "",                            // e.g. "01.12.2023"
        @Query("hour") hour: String? = null,                                // e.g. "14:00",
        @Query("type") type: String? = null,                                // e.g. "departure", "arrival"
        @Query("via") via: String? = null,                                  // e.g. "mezdra"
    ): SchedulePage

    companion object {
        @SuppressLint("SimpleDateFormat")
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }
}