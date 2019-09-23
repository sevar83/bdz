package bg.bdz.schedule.network

import android.annotation.SuppressLint
import bg.bdz.schedule.models.SchedulePage
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
interface ScheduleService {

    @FormUrlEncoded
    @POST("SearchServlet")
    suspend fun getSchedulePage(
        @Query(value="action") action: String = "listOptions",
        @Query(value="lang") language: String = "bg",  /* bg, en */
        @Field("from_station") fromStation: String,
        @Field("to_station") toStation: String,
        @Field("via_station") viaStation: String? = null,
        @Field("date") date: String = DATE_FORMATTER.format(LocalDate.now()),
        @Field("dep_arr") departsOrArrives: Int = 1,    /* 1=departs or 2=arrives */
        @Field("time_from") timeFrom: String = "00:00",
        @Field("time_to") timeTo: String = "24:00",
        @Field("all_cats") allCats: String = "checked",
        @Field("cardld") cardld: Int = 30,
        @Field("class") klass: Int = 0,
        @Field("sort_by") sortBy: Int = 0,
        @Field("x") x: Int = 24,
        @Field("y") y: Int = 12
    ): SchedulePage

    companion object {
        @SuppressLint("SimpleDateFormat")
        val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}