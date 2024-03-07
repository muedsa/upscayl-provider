package com.muedsa.upscayl.service

import com.muedsa.upscayl.dao.ImageUrlAliasDAO
import com.muedsa.upscayl.dao.UpscaylImageIndexDAO
import com.muedsa.upscayl.dao.UpscaylTaskLogDAO
import com.muedsa.upscayl.model.ImageUrlAlias
import com.muedsa.upscayl.model.NetworkImageUpscaylParams
import com.muedsa.upscayl.model.ProvideUpscaylImage
import com.muedsa.upscayl.model.UpscaylImageIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class UpscaylImageService(
    private val redisService: RedisService,
    private val imageUrlAliasDAO: ImageUrlAliasDAO,
    private val upscaylImageIndexDAO: UpscaylImageIndexDAO,
    private val upscaylTaskLogDAO: UpscaylTaskLogDAO,
    private val database: Database
) {

    suspend fun getUpscaylImage(url: String): String {
        val imageUrlAlias = imageUrlAliasDAO.getByUrl(url)
        if (imageUrlAlias != null) {
            val index = upscaylImageIndexDAO.getByHash(imageUrlAlias.hash)
            return index?.url ?: url
        } else {
            runInterruptible {
                val nx = redisService.setNx("UPSCAYL_TASK_LOCK:$url", url, 60 * 60 * 5)
                if (nx) {
                    val params = NetworkImageUpscaylParams(
                        guid = UUID.randomUUID().toString(),
                        url = url
                    )
                    redisService.publish("NETWORK_IMAGE_UPSCAYL", Json.Default.encodeToString(params))
                }
            }
            return url
        }
    }

    suspend fun updateUrlAlias(imageUrlAlias: ImageUrlAlias): UpscaylImageIndex? {
        return upscaylImageIndexDAO.getByHash(imageUrlAlias.hash)?.also {
            imageUrlAliasDAO.insertIgnore(imageUrlAlias.url, it.hash)
        }
    }

    suspend fun saveUpscaylImage(provideUpscaylImage: ProvideUpscaylImage) {
        newSuspendedTransaction(Dispatchers.IO, db = database) {
            upscaylImageIndexDAO.insertIgnore(provideUpscaylImage.sourceHash, provideUpscaylImage.upscaylUrl)
            imageUrlAliasDAO.insertIgnore(provideUpscaylImage.sourceUrl, provideUpscaylImage.sourceHash)
            if (provideUpscaylImage.taskResult != null) {
                upscaylTaskLogDAO.insertIgnore(provideUpscaylImage)
            }
        }
    }
}