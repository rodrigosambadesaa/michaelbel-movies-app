package org.michaelbel.movies.ui.preview.base

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

open class CollectionPreviewParameterProvider<T>(
    private val collection: Collection<T>
): PreviewParameterProvider<T> {

    override val values: Sequence<T>
        get() = collection.asSequence()
}