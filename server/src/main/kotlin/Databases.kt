package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val dbPath = "./librarydb"
    Database.connect("jdbc:h2:file:$dbPath;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

    transaction {
        SchemaUtils.create(BookTable)
    }
}
