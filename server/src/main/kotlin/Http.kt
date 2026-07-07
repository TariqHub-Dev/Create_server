package com.example

import io.ktor.server.application.*
import com.asyncapi.kotlinasyncapi.ktor.AsyncApiPlugin
import com.asyncapi.kotlinasyncapi.context.service.AsyncApiExtension

fun Application.configureHttp() {
    install(AsyncApiPlugin) {
        extension = AsyncApiExtension.builder {
            info {
                title("Sample API")
                version("1.0.0")
            }
        }
    }
}
