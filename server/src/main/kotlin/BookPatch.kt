package com.example

import kotlinx.serialization.Serializable

@Serializable
data class BookPatch (
    val title: String? = null,
    val author: String? = null,
    val isBorrowed: Boolean? = null
)
