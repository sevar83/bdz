package bg.bdz.schedule.serialization

import bg.bdz.schedule.models.TimeTable
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by Svetlozar Kostadinov on 9/3/2019.
 */
class TimeTableConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type != TimeTable::class.java) {
            return null // Let retrofit choose another converter
        }

        return TimeTableResponseBodyConverter
    }
}