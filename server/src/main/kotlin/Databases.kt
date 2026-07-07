package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

fun Application.configureDatabases() {
    val dbUrl = System.getenv("DATABASE_URL")

    if (dbUrl != null) {
        // Railway/Heroku DATABASE_URL biasanya berformat postgres://user:pass@host:port/db
        try {
            val uri = URI(dbUrl)
            val userInfo = uri.userInfo.split(":")
            val username = userInfo[0]
            val password = userInfo[1]
            val host = uri.host
            val port = if (uri.port != -1) uri.port else 5432
            val path = uri.path
            val jdbcUrl = "jdbc:postgresql://$host:$port$path?sslmode=require"

            Database.connect(
                url = jdbcUrl,
                driver = "org.postgresql.Driver",
                user = username,
                password = password
            )
        } catch (e: Exception) {
            // Fallback jika parsing gagal
            Database.connect(
                url = "jdbc:postgresql://db.cjwlgoozwejzvwmmiosa.supabase.co:5432/postgres",
                driver = "org.postgresql.Driver"
            )
        }
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
