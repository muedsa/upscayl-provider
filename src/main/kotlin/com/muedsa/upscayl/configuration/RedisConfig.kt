package com.muedsa.upscayl.configuration


import io.ktor.server.config.*

class RedisConfig(
    private val config: ApplicationConfig
) {

    private val redisConfig: ApplicationConfig by lazy {
        config.config(REDIS_CONFIG_KEY_PREFIX)
    }

    val host: String by lazy {
        redisConfig.property(REDIS_HOST_KEY).getString()
    }

    val port: Int by lazy {
        redisConfig.property(REDIS_PORT_KEY).getString().toInt()
    }

    val password: String by lazy {
        redisConfig.property(REDIS_PASSWORD_KEY).getString()
    }

    companion object {
        private const val REDIS_CONFIG_KEY_PREFIX = "ktor.redis"
        private const val REDIS_HOST_KEY = "host"
        private const val REDIS_PORT_KEY = "port"
        private const val REDIS_PASSWORD_KEY = "password"
    }
}