package com.muedsa.upscayl.di

import io.ktor.events.EventHandler
import io.ktor.server.application.*
import org.koin.core.KoinApplication
import org.koin.ktor.plugin.KoinApplicationStopPreparing

object KoinShutdownDispatcher {

    private val listeners = mutableListOf<EventHandler<KoinApplication>>()

    fun register(handler: EventHandler<KoinApplication>) {
        listeners += handler
    }

    fun complete(applicationEnvironment: ApplicationEnvironment) {
        listeners.asReversed().forEach {
            applicationEnvironment.monitor.subscribe(KoinApplicationStopPreparing, it)
        }
    }
}