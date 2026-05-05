package org.michaelbel.movies.persistence.database.ktx

import org.michaelbel.movies.persistence.database.entity.SuggestionDb
import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo

val SuggestionPojo.suggestionDb: SuggestionDb
    get() = SuggestionDb(
        title = title
    )
