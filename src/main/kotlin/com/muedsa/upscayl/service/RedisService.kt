package com.muedsa.upscayl.service

import com.muedsa.upscayl.configuration.RedisConfig
import com.muedsa.upscayl.di.KoinShutdownDispatcher
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.pubsub.RedisPubSubAdapter
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import org.slf4j.LoggerFactory

class RedisService(
    redisConfig: RedisConfig
) {
    private val redisUri: RedisURI = RedisURI.Builder.redis(redisConfig.host, redisConfig.port)
        .withPassword(redisConfig.password.toCharArray())
        .build()
    private val redisClient: RedisClient = RedisClient.create(redisUri)
    private val connection: StatefulRedisConnection<String, String> = redisClient.connect()
    private val commands = connection.sync()

    fun setNx(key: String, value: String, expire: Long): Boolean {
        return commands.setnx(key, value) && commands.expire(key, expire)
    }

    fun del(key: String): Boolean {
        return commands.del(key) > 0
    }

    fun get(key: String): String? {
        return commands.get(key)
    }

    fun set(key: String, value: String) {
        commands.set(key, value)
    }

    fun set(key: String, value: String, expire: Long) {
        commands.set(key, value)
        commands.expire(key, expire)
    }

    fun exist(key: String): Boolean {
        return commands.exists(key) > 0
    }

    fun publish(channel: String, message: String) {
        commands.publish(channel, message)
    }
}