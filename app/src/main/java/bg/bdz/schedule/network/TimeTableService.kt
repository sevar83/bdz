package bg.bdz.schedule.network

import bg.bdz.schedule.models.Station
import bg.bdz.schedule.models.TimeTable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Svetlozar Kostadinov on 8/28/2019.
 */
interface TimeTablesService {

    @GET("timeTableCache.php")
    suspend fun getArrivalsTimeTable(
        @Query(value="s") station: Station
    ): TimeTable

    @GET("timeTableCache.php?d=1")
    suspend fun getDeparturesTimeTable(
        @Query(value="s") station: Station
    ): TimeTable
}