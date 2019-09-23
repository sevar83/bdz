package bg.bdz.schedule.utils

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

open class SeparateOnScrollDownListener(private val separatorView: View) : RecyclerView.OnScrollListener() {

    private var scrollY: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (scrollY + dy <= 0) {
            separatorView.isInvisible = true
        } else if (scrollY == 0 && dy > 0) {
            separatorView.isVisible = true
        }
        scrollY += dy
    }
}
