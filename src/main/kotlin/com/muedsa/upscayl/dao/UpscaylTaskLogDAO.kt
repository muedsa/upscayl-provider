package com.muedsa.upscayl.dao

import com.muedsa.upscayl.model.ProvideUpscaylImage
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class UpscaylTaskLogDAO(private val database: Database) {

    object UpscaylTaskLogTable : Table("upscayl_task_log") {
        val id = integer("id").autoIncrement()
        val sourceUrl = varchar("source_url", length = 512)
        val upscaylUrl = varchar("upscayl_url", length = 512)
        val traceId = varchar("trace_id", length = 128).nullable()
        val exitCode = integer("exit_code")
        val message = text("message")
        val startTime = long("start_time").nullable()
        val endTime = long("end_time").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UpscaylTaskLogTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    fun insertIgnore(provideUpscaylImage: ProvideUpscaylImage): Int =  UpscaylTaskLogTable.insertIgnore {
        it[sourceUrl] = provideUpscaylImage.sourceUrl
        it[upscaylUrl] = provideUpscaylImage.upscaylUrl
        it[traceId] = provideUpscaylImage.traceId
        it[exitCode] = provideUpscaylImage.taskResult?.exitCode ?: -1
        it[message] = provideUpscaylImage.taskResult?.message ?: ""
        it[startTime] = provideUpscaylImage.taskResult?.startTime
        it[endTime] = provideUpscaylImage.taskResult?.endTime
    }.insertedCount

}