package bg.bdz.schedule.network

import bg.bdz.schedule.models.Station
import bg.bdz.schedule.models.TimeTable
import retrofit2.http.GET
import retrofit2.http.Path

interface TimeTablesService {

    @GET("/{language}/{station}/arrivals")
    suspend fun getArrivalsTimeTable(
        @Path("language") language: String = "bg",
        @Path("station") station: Station.Slug
    ): TimeTable

    @GET("/{language}/{station}/departures")
    suspend fun getDeparturesTimeTable(
        @Path("language") language: String = "bg",
        @Path("station") station: Station.Slug
    ): TimeTable
}