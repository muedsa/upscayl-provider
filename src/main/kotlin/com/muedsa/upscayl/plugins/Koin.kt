package com.muedsa.upscayl.plugins

import com.muedsa.upscayl.di.appModule
import com.muedsa.upscayl.di.configModule
import com.muedsa.upscayl.di.daoModule
import com.muedsa.upscayl.di.storageModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(configModule(environment.config), storageModule, daoModule, appModule)
    }
}