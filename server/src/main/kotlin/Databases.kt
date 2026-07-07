package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    // Ambil URL dari sistem cloud, atau biarkan kosong jika belum ada
    val dbUrl = System.getenv("DATABASE_URL")

    if (dbUrl != null) {
        // 1. Jika URL tersedia (jalan di Railway), konek ke Supabase
        Database.connect(
            url = "jdbc:$dbUrl", 
            driver = "org.postgresql.Driver"
        )
    } else {
        // 2. Jika URL tidak ada (jalan di laptop/lokal), tetap pakai H2 untuk testing
        Database.connect(
            url = "jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;", 
            driver = "org.h2.Driver"
        )
    }

    transaction {
        SchemaUtils.create(BookTable)
    }
}
