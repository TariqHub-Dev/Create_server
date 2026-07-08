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

    val dataSource = if (dbUrl != null) {
        // Konfigurasi untuk PostgreSQL (Supabase/Railway)
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            
            // Parsing DATABASE_URL
            val cleanUrl = dbUrl.replace("jdbc:", "")
            val uri = URI(cleanUrl)
            val userInfo = uri.userInfo?.split(":")
            
            jdbcUrl = "jdbc:postgresql://${uri.host}:${if (uri.port != -1) uri.port else 5432}${uri.path}?sslmode=require"
            username = userInfo?.getOrNull(0)
            password = userInfo?.getOrNull(1)
            
            // Optimasi Pool
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            
            // Tambahkan timeout yang lebih panjang untuk koneksi cloud
            connectionTimeout = 30000 
            validate()
        }
        HikariDataSource(config)
    } else {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;"
            maximumPoolSize = 3
            isAutoCommit = false
            validate()
        }
        HikariDataSource(config)
    }

    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(BookTable)
    }
}
