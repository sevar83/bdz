package bg.bdz.schedule.utils

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.shopify.livedataktx.*

fun <A, B> combineLatest(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A?, B?>> {

    fun Pair<A?, B?>?.copyWithFirst(first: A?): Pair<A?, B?> {
        if (this@copyWithFirst == null) return Pair<A?, B?>(first, null)
        return this@copyWithFirst.copy(first = first)
    }

    fun Pair<A?, B?>?.copyWithSecond(second: B?): Pair<A?, B?> {
        if (this@copyWithSecond == null) return Pair<A?, B?>(null, second)
        return this@copyWithSecond.copy(second = second)
    }

    return MediatorLiveData<Pair<A?, B?>>().apply {
        addSource(a) { value = value.copyWithFirst(it) }
        addSource(b) { value = value.copyWithSecond(it) }
    }
}

fun <A, B, C> combineLatest(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>): LiveData<Triple<A?, B?, C?>> {

    fun Triple<A?, B?, C?>?.copyWithFirst(first: A?): Triple<A?, B?, C?> {
        if (this@copyWithFirst == null) return Triple<A?, B?, C?>(first, null, null)
        return this@copyWithFirst.copy(first = first)
    }

    fun Triple<A?, B?, C?>?.copyWithSecond(second: B?): Triple<A?, B?, C?> {
        if (this@copyWithSecond == null) return Triple<A?, B?, C?>(null, second, null)
        return this@copyWithSecond.copy(second = second)
    }

    fun Triple<A?, B?, C?>?.copyWithThird(third: C?): Triple<A?, B?, C?> {
        if (this@copyWithThird == null) return Triple<A?, B?, C?>(null, null, third)
        return this@copyWithThird.copy(third = third)
    }

    return MediatorLiveData<Triple<A?, B?, C?>>().apply {
        addSource(a) { value = value.copyWithFirst(it) }
        addSource(b) { value = value.copyWithSecond(it) }
        addSource(c) { value = value.copyWithThird(it) }
    }
}

private class FilterOperator<T>(val predicate: (T) -> Boolean) : Operator<T, T> {

    override fun run(output: MediatorLiveDataKtx<T>, value: T) {
        if (predicate.invoke(value)) {
            output.value = value
        }
    }
}

fun <T> LiveDataKtx<T>.filter(predicate: (T) -> Boolean): LiveDataKtx<T> =
    Extension.create(this, FilterOperator(predicate))

fun <T> MutableLiveDataKtx<T>.filter(predicate: (T) -> Boolean): MutableLiveDataKtx<T> =
    Extension.create(this, FilterOperator(predicate))

fun <T> MediatorLiveDataKtx<T>.filter(predicate: (T) -> Boolean): MediatorLiveDataKtx<T> =
    Extension.create(this, FilterOperator(predicate))

fun TextView.updateText(text: CharSequence?) {
    if (this.text?.toString() != text.toString()) {
        setText(text)
    }
}