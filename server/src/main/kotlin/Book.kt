package com.example


import kotlinx.serialization.Serializable


@Serializable
data class Book (
    val id: String,
    val title: String,
    val author: String,
    val isBorrowed: Boolean = false
)