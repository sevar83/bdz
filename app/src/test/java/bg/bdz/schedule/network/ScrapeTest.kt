package bg.bdz.schedule.network

import bg.bdz.schedule.models.Station
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection
import kotlin.time.Duration

class ScrapeTest {
    private var mockWebServer = MockWebServer()
    private lateinit var scheduleService: ScheduleService
    private lateinit var timeTablesService: TimeTablesService

    @Before
    fun setup() {
        mockWebServer.start()
        val url = mockWebServer.url("/").toString()
        val retrofit = Bdz.createRetrofit(url)
        scheduleService = retrofit.create(ScheduleService::class.java)
        timeTablesService = retrofit.create(TimeTablesService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testScheduleScraping() = runTest {
        // Given
        val sofiaVarnaHtml = ResourceLoader.loadString("sofia-varna.html")!!
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(sofiaVarnaHtml)
        mockWebServer.enqueue(response)

        // When
        val schedulePage = scheduleService.getSchedulePage(
            fromStation = Station.Slug("dummy"),
            toStation = Station.Slug("dummy"),
        )

        // Then

        val trains = schedulePage.trains

        // Size

        assertEquals(12, trains.size)

        // Train.ordinal

        trains.forEachIndexed { index, train ->
            assertEquals(index + 1, train.ordinal)
        }

        // Train.stations

        assertEquals(listOf("София", "Варна"), trains[0].stations)
        assertEquals(listOf("София", "Варна"), trains[1].stations)
        assertEquals(listOf("София", "Карнобат", "Варна"), trains[2].stations)
        assertEquals(listOf("София", "Варна"), trains[3].stations)
        assertEquals(listOf("София", "Варна"), trains[4].stations)
        assertEquals(listOf("София", "Варна"), trains[5].stations)
        assertEquals(listOf("София", "Варна"), trains[6].stations)
        assertEquals(listOf("София", "Варна"), trains[7].stations)
        assertEquals(listOf("София", "Варна"), trains[8].stations)
        assertEquals(listOf("София", "Разград", "Варна"), trains[9].stations)
        assertEquals(listOf("София", "Карнобат", "Варна"), trains[10].stations)
        assertEquals(listOf("София", "Мездра", "Варна"), trains[11].stations)

        // Train.times
        assertEquals(listOf("07:00", "14:27"), trains[0].times)
        assertEquals(listOf("10:10", "17:35"), trains[1].times)
        // ...
        assertEquals(listOf("21:50", "11:50"), trains[11].times)

        // Train.duration

        assertEquals("07:27 ч.", trains[0].duration)
        assertEquals("07:25 ч.", trains[1].duration)
        // ...
        assertEquals("14:00 ч.", trains[11].duration)

        // Train.trainNumber

        assertEquals("БВ 2601", trains[0].trainNumber)
        assertEquals("БВ 2611", trains[1].trainNumber)
        // ...
        assertEquals("КПВ 20217", trains[11].trainNumber)

        // Train.waypoints

        val trainWithIntermediateStations = trains[2]
        val waypoints = trainWithIntermediateStations.waypoints
        assertEquals(3, waypoints.size)

        // waypoint.name
        assertEquals("София", waypoints[0].name)
        assertEquals("Карнобат", waypoints[1].name)
        assertEquals("Варна", waypoints[2].name)

        // waypoint.arriveTime & departTime
        assertEquals("↦", waypoints[0].arriveTime)
        assertEquals("07:35", waypoints[0].departTime)
        assertEquals("13:09", waypoints[1].arriveTime)
        assertEquals("15:00", waypoints[1].departTime)
        assertEquals("18:00", waypoints[2].arriveTime)
        assertEquals("↤", waypoints[2].departTime)

        // waypoint.trainCode
        assertEquals("БВ 3621", waypoints[0].trainCode)
        assertEquals("ПВ 30155", waypoints[1].trainCode)
        assertEquals("ПВ 30155", waypoints[2].trainCode)

        // Train.note

        assertEquals("Директен влак.", trains[0].note)
        assertEquals("Директен влак.", trains[1].note)
        assertEquals("Пътуване с прекачване.", trains[2].note)
    }

    @Test
    fun testTimetableScraping() = runTest(timeout = Duration.INFINITE) {
        // Given
        val sofiaTimetableHtml = ResourceLoader.loadString("timetable-sofia.html")!!
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(sofiaTimetableHtml)
        mockWebServer.enqueue(response)

        // When
        val timetablePage = timeTablesService.getArrivalsTimeTable(
            station = Station.Slug("sofia")
        )

        // Then
        assertEquals(1, timetablePage.trainStatuses.size)
    }
}