package org.michaelbel.movies.gallery.ktx

const val INFINITE_PAGER_PAGE_COUNT = Int.MAX_VALUE

fun calculateInitialPagerPage(imageCount: Int): Int {
    if (imageCount <= 1) return 0
    val middlePage = INFINITE_PAGER_PAGE_COUNT / 2
    return middlePage - middlePage % imageCount
}

fun pageToImageIndex(page: Int, imageCount: Int): Int {
    if (imageCount == 0) return 0
    return page % imageCount
}
