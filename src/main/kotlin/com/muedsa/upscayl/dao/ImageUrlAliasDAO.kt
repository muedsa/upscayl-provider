package com.muedsa.upscayl.dao

import com.muedsa.upscayl.dao.ImageUrlAliasDAO.ImageUrlAliasTable
import com.muedsa.upscayl.model.ImageUrlAlias
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class ImageUrlAliasDAO(private val database: Database) {
    object ImageUrlAliasTable : Table("image_url_alias") {
        val id = integer("id").autoIncrement()
        val url = varchar("url", length = 512).index(isUnique = true)
        val hash = varchar("hash", length = 64).index() // sha-256

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(ImageUrlAliasTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun getByUrl(url: String): ImageUrlAlias? {
        return dbQuery {
            ImageUrlAliasTable.selectAll().where { ImageUrlAliasTable.url eq url }
                .map {
                    ImageUrlAlias(
                        it[ImageUrlAliasTable.url],
                        it[ImageUrlAliasTable.hash]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun getByHash(hash: String): ImageUrlAlias? {
        return dbQuery {
            ImageUrlAliasTable.selectAll().where { ImageUrlAliasTable.hash eq hash }
                .map {
                    ImageUrlAlias(
                        it[ImageUrlAliasTable.url],
                        it[ImageUrlAliasTable.hash]
                    )
                }
                .singleOrNull()
        }
    }

    fun insertIgnore(url: String, hash: String): Int = ImageUrlAliasTable.insertIgnore {
        it[ImageUrlAliasTable.url] = url
        it[ImageUrlAliasTable.hash] = hash
    }.insertedCount
}