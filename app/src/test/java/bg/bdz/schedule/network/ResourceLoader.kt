package bg.bdz.schedule.network

import java.io.BufferedReader
import java.io.InputStream

object ResourceLoader {

    fun loadString(name: String): String? {
        return ResourceLoader::class.java.classLoader?.let { loadString(it, name) }
    }

    fun loadString(loader: ClassLoader, name: String): String {
        return loader.getResourceAsStream(name)!!.use(::loadString)
    }

    fun loadString(inputStream: InputStream): String {
        return inputStream.bufferedReader().use(BufferedReader::readText)
    }
}