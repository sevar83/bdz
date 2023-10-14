package bg.bdz.schedule.serialization

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

open class TextSplittingConverter(
    private val delimiter: Char,
    private val preprocessor: (input: String) -> String,
    private val itemMapper: (item: String) -> String,
) : ElementConverter<List<String>> {
    override fun convert(node: Element, selector: Selector): List<String> {
        return preprocessor(node.text())
            .split(delimiter)
            .map(itemMapper)
    }
}

class RouteSplittingConverter : TextSplittingConverter(
    delimiter = '-',
    preprocessor = ::removeNumberAtStart,
    itemMapper = String::trim
)

class TimesSplittingConverter : TextSplittingConverter(
    delimiter = '-',
    preprocessor = { it },
    itemMapper = String::trim
)

private fun removeNumberAtStart(input: String): String {
    return digitsRegex.replace(input.trim(), "").trim()
}

private val digitsRegex = Regex("[0-9]")