package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

fun Application.configureDatabases() {
    val dbUrl = System.getenv("DATABASE_URL")

    if (dbUrl != null) {
        Database.connect(
            url = "jdbc:$dbUrl",
            driver = "org.postgresql.Driver"
        )
    } else {
        Database.connect(
            url = "jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )
    }

    transaction {
        SchemaUtils.create(BookTable)
    }
}