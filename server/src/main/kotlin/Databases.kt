package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )

    transaction(database) {
        SchemaUtils.create(BookTable)
    }
}
