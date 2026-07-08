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
                val cleanUrl = dbUrl.replace("jdbc:", "")
                val uri = URI(cleanUrl)
                val userInfo = uri.userInfo?.split(":")
                
                // PERBAIKAN: Gunakan port 5432 untuk akses langsung (Direct Connection)
                // Port 6543 biasanya adalah Transaction Pooler yang bersifat Read-Only untuk perintah DDL (CREATE TABLE)
                val host = uri.host
                val port = 5432 
                val dbName = if (uri.path.isNullOrBlank() || uri.path == "/") "/postgres" else uri.path
                
                jdbcUrl = "jdbc:postgresql://$host:$port$dbName?sslmode=require"
                username = userInfo?.getOrNull(0)
                password = userInfo?.getOrNull(1)
                
            } catch (e: Exception) {
                jdbcUrl = if (dbUrl.startsWith("jdbc:")) dbUrl else "jdbc:$dbUrl"
            }
            
            maximumPoolSize = 3
            connectionTimeout = 30000
            // Pastikan tidak dipaksa menjadi read-only
            isReadOnly = false
        }
        HikariDataSource(config)
    } else {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:file:./librarydb;DB_CLOSE_DELAY=-1;"
            maximumPoolSize = 3
        }
        HikariDataSource(config)
    }

    Database.connect(dataSource)

    // Jalankan pembuatan tabel di dalam transaksi yang bisa menulis (RW)
    transaction {
        SchemaUtils.create(BookTable)
    }
}
