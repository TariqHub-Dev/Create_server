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
        // Konfigurasi untuk PostgreSQL (Supabase/Railway)
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            
            try {
                // Bersihkan URL dari prefix jdbc: jika ada
                val cleanUrl = dbUrl.replace("jdbc:", "")
                val uri = URI(cleanUrl)
                
                val userInfo = uri.userInfo?.split(":")
                username = userInfo?.getOrNull(0)
                password = userInfo?.getOrNull(1)
                
                val host = uri.host
                val port = if (uri.port != -1) uri.port else 5432
                val dbName = if (uri.path.isNullOrBlank() || uri.path == "/") "/postgres" else uri.path
                
                // Tambahkan parameter penting untuk stabilitas di Cloud
                // prepareThreshold=0 penting jika menggunakan connection pooler (PgBouncer)
                jdbcUrl = "jdbc:postgresql://$host:$port$dbName?sslmode=require&prepareThreshold=0&tcpKeepAlive=true"
                
            } catch (e: Exception) {
                // Fallback jika parsing URI gagal, gunakan URL apa adanya
                jdbcUrl = if (dbUrl.startsWith("jdbc:")) dbUrl else "jdbc:$dbUrl"
            }
            
            // Optimasi Pool untuk Railway (Free tier biasanya punya limit koneksi rendah)
            maximumPoolSize = 3
            minimumIdle = 1
            idleTimeout = 10000
            connectionTimeout = 30000 
            maxLifetime = 1800000
            
            // Properti tambahan untuk performa
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }
        HikariDataSource(config)
    } else {
        // Fallback ke H2 untuk Lokal
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
