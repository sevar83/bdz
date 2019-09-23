package bg.bdz.schedule.network

import bg.bdz.schedule.serialization.TimeTableConverterFactory
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.create

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
object Bdz {

    private fun createRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(JspoonConverterFactory.create())
        .addConverterFactory(TimeTableConverterFactory())
        .build()
    
    val scheduleService by lazy<ScheduleService> {
        createRetrofit("http://razpisanie.bdz.bg/").create()
    }

    val timeTablesService by lazy<TimeTablesService> {
        createRetrofit("http://live.bdz.bg/").create()
    }
}