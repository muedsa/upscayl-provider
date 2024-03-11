package com.muedsa.upscayl.dao

import com.muedsa.upscayl.model.UpscaylImageIndex
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class UpscaylImageIndexDAO(private val database: Database) {
    object UpscaylImageIndexTable : Table("upscayl_image_index") {
        val id = integer("id").autoIncrement()
        val hash = varchar("hash", length = 64).index(isUnique = true) // sha-256
        val url = varchar("url", length = 512).index()
        val scale = integer("scale")
        val model = varchar("model", length = 64)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UpscaylImageIndexTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun getByHash(hash: String): UpscaylImageIndex? {
        return dbQuery {
            UpscaylImageIndexTable.selectAll().where { UpscaylImageIndexTable.hash eq hash }
                .map {
                    UpscaylImageIndex(
                        it[UpscaylImageIndexTable.hash],
                        it[UpscaylImageIndexTable.url]
                    )
                }
                .singleOrNull()
        }
    }

    fun insertIgnore(hash: String, url: String, scale: Int, model: String): Int = UpscaylImageIndexTable.insertIgnore {
        it[UpscaylImageIndexTable.hash] = hash
        it[UpscaylImageIndexTable.url] = url
        it[UpscaylImageIndexTable.scale] = scale
        it[UpscaylImageIndexTable.model] = model
    }.insertedCount

}