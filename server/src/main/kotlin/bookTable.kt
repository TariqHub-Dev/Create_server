package com.example

import org.jetbrains.exposed.sql.Table

object BookTable : Table("books") {
    val id = varchar("id", 50)
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val isBorrowed = bool("is_borrowed")

    override val primaryKey = PrimaryKey(id)
}