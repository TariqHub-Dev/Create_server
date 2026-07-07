package com.example

import io.ktor.server.engine.*
import io.ktor.server.cio.*

fun main(args: Array<String>) {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(CIO, port = port) {
        module()
    }.start(wait = true)
}