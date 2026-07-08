package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

fun Application.configureDatabases() {
    val dbUrl = System.getenv("DATABASE_URL")?.trim()

    val dataSource = if (!dbUrl.isNullOrBlank()) {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            
            try {
                // Parsing DATABASE_URL (format: postgres://user:pass@host:port/db)
                val uri = URI(dbUrl)
                val userInfo = uri.userInfo?.split(":")
                
                // Supabase membutuhkan sslmode=require
                jdbcUrl = "jdbc:postgresql://${uri.host}:${if (uri.port != -1) uri.port else 5432}${uri.path}?sslmode=require"
                username = userInfo?.getOrNull(0)
                password = userInfo?.getOrNull(1)
                
                println("DATABASE_URL terdeteksi. Menghubungkan ke host: ${uri.host}")
            } catch (e: Exception) {
                // Fallback jika format sudah berupa JDBC
                jdbcUrl = if (dbUrl.startsWith("jdbc:")) dbUrl else "jdbc:$dbUrl"
                println("Menggunakan fallback JDBC URL")
            }
            
            maximumPoolSize = 3
            connectionTimeout = 30000
            leakDetectionThreshold = 2000
        }
        HikariDataSource(config)
    } else {
        println("DATABASE_URL tidak ditemukan. Menggunakan H2 lokal.")
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;"
            maximumPoolSize = 3
        }
        HikariDataSource(config)
    }

    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(BookTable)
    }
}
