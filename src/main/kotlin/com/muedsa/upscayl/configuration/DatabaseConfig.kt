package com.muedsa.upscayl.configuration

import io.ktor.server.config.*

class DatabaseConfig(
    private val config: ApplicationConfig
) {
    private val configSet: ApplicationConfig by lazy {
        config.config(DATABASE_CONFIG_KEY_PREFIX)
    }

    val driver: String by lazy {
        configSet.property(DRIVER_KEY).getString()
    }

    val jdbcUrl: String by lazy {
        configSet.property(JDBC_URL_KEY).getString()
    }

    val username: String by lazy {
        configSet.property(USERNAME_KEY).getString()
    }

    val password: String by lazy {
        configSet.property(PASSWORD_KEY).getString()
    }

    companion object {
        private const val DATABASE_CONFIG_KEY_PREFIX = "ktor.database"
        private const val DRIVER_KEY = "driver"
        private const val JDBC_URL_KEY = "jdbcUrl"
        private const val USERNAME_KEY = "username"
        private const val PASSWORD_KEY = "password"
    }
}