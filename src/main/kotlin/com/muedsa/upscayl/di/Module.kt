package com.muedsa.upscayl.di

import com.muedsa.upscayl.configuration.*
import com.muedsa.upscayl.dao.*
import com.muedsa.upscayl.service.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun configModule(config: ApplicationConfig) = module(createdAtStart = true) {
    single<ApplicationConfig> { config }
    singleOf(::RedisConfig)
    singleOf(::DatabaseConfig)
}

val storageModule = module {
    singleOf(::RedisService)
    single {
        val config = get<DatabaseConfig>()
        Database.connect(HikariDataSource(HikariConfig().apply {
            driverClassName = config.driver
            jdbcUrl = config.jdbcUrl
            username = config.username
            password = config.password
            maximumPoolSize = 3
            validate()
        })).also {
            KoinShutdownDispatcher.register {
                it.close()
            }
        }
    }
}

val daoModule = module {
    singleOf(::ImageUrlAliasDAO)
    singleOf(::UpscaylImageIndexDAO)
    singleOf(::UpscaylTaskLogDAO)
}

val appModule = module {
    singleOf(::UpscaylImageService)
}