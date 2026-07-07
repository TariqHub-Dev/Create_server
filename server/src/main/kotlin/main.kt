package com.example

import io.ktor.server.engine.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.print.Book

fun main(args: Array<String>) {

    Database.connect("jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;",driver = "org.h2.Driver")

    transaction {
        SchemaUtils.create(BookTable)
    }

    io.ktor.server.cio.EngineMain.main(args)
}
