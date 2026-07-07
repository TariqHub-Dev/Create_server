package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.module() {
    Database.connect("jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    transaction {
        SchemaUtils.create(BookTable)
    }
    configureRouting()
}