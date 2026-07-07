package com.example

import io.ktor.server.engine.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    transaction { 
        SchemaUtils.create(BookTable) 
    }

    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(CIO, port = port) {
        configureHttp()
        configureSerialization()
        configureResources()
        configureRouting()
    }.start(wait = true)
}
