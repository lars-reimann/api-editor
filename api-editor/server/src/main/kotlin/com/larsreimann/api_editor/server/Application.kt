package com.larsreimann.api_editor.server

import de.unibonn.simpleml.SimpleMLStandaloneSetup
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.minimumSize
import io.ktor.request.path
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level

fun main() {
    SimpleMLStandaloneSetup.doSetup()

    embeddedServer(Netty, port = 4280, host = "localhost") {
        configureHTTP()
        configureMonitoring()
        configureRouting()
    }.start(wait = true)
}

fun Application.configureHTTP() {
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
}

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}