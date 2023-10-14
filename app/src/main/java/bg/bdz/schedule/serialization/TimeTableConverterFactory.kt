package bg.bdz.schedule.serialization

import bg.bdz.schedule.models.TimeTable
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class TimeTableConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type != TimeTable::class.java) {
            return null // Let retrofit choose another converter
        }

        return TimeTableResponseBodyConverter
    }
}