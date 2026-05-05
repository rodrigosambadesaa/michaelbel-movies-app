package androidx.paging

class PagingData<T> private constructor(
    val items: List<T>
) {

    companion object {
        fun <T> from(items: List<T>): PagingData<T> {
            return PagingData(items)
        }
    }
}
