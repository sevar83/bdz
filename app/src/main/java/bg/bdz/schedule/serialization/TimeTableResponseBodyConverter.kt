package bg.bdz.schedule.serialization

import android.util.JsonReader
import android.util.JsonToken
import bg.bdz.schedule.models.TimeTable
import bg.bdz.schedule.models.TrainStatus
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Converter
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


internal object TimeTableResponseBodyConverter : Converter<ResponseBody, TimeTable> {

    private val WORD_BLACK_LIST = listOf(
        "<span>", "<span >", "</span>",
        "<b>", "</b>",
        "<br>"
    )

    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): TimeTable? {
        var charset: Charset? = null
        val mediaType = responseBody.contentType()
        if (mediaType != null)
            charset = mediaType.charset()

        val inputStream = responseBody.byteStream()
        val reader = JsonReader(InputStreamReader(inputStream, charset))

        return reader.use {
            inputStream.use {
                reader.readTimeTable()
            }
        }
    }

    @Throws(IOException::class)
    fun JsonReader.readTimeTable(): TimeTable {
        var trains: List<TrainStatus>? = null

        beginObject()

        while (hasNext()) {
            when (nextName()) {
                "data" -> trains = readData()
                else -> skipValue()
            }
        }

        endObject()

        if (trains == null) {
            throw JSONException("Missing data field")
        }

        return TimeTable(trains)
    }

    @Throws(IOException::class)
    fun JsonReader.readData(): List<TrainStatus> {
        var trains: List<TrainStatus>? = null
        beginObject()
        while (hasNext()) {
            when (nextName()) {
                "trains" -> trains = readTrainArray()
                else -> skipValue()
            }
        }
        endObject()

        if (trains == null) {
            throw JSONException("Missing trains field")
        }

        return trains
    }

    @Throws(IOException::class)
    fun JsonReader.readTrainArray(): List<TrainStatus> {
        val trains = ArrayList<TrainStatus>()
        beginArray()
        while (hasNext()) {
            trains.add(readTrainStatus())
        }
        endArray()

        return trains
    }

    @Throws(IOException::class)
    fun JsonReader.readTrainStatus(): TrainStatus {
        var trainCode: String? = null
        var dateTime: String? = null
        var station: String? = null
        var track: String? = null
        var comments: String? = null

        beginObject()

        while (hasNext()) {
            when (nextName()) {
                "trainTypeNumber" -> trainCode = readString()
                "trainTime" -> dateTime = readString()
                "trainFromTo" -> station = readString()
                "track" -> track = readString()
                "trainDelay" -> comments = readString()
                else -> skipValue()
            }
        }
        endObject()

        if (trainCode == null) {
            throw JSONException("Missing trainTypeNumber field")
        }

        if (dateTime == null) {
            throw JSONException("Missing trainTime field")
        }

        if (station == null) {
            throw JSONException("Missing trainFromTo field")
        }

        return TrainStatus(
            trainCode = trainCode,
            dateTime = dateTime,
            station = station,
            track = track,
            comments = comments
        )
    }

    @Throws(IOException::class)
    fun JsonReader.readString(): String? {
        val token = peek()
        return if (token == JsonToken.NULL) {
            nextNull()
            null
        } else {
            var string: String? = nextString()
            WORD_BLACK_LIST.forEach { wordToRemove ->
                val replacement = if (wordToRemove == "<br>") {
                    " "
                } else {
                    ""
                }
                string = string?.replace(wordToRemove, replacement)
            }
            string
        }
    }
}
