package com.muedsa.upscayl

import com.muedsa.upscayl.di.KoinShutdownDispatcher
import com.muedsa.upscayl.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    KoinShutdownDispatcher.complete(environment)
}
