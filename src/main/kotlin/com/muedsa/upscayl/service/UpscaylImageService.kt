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

class UpscaylImageService(
    private val redisService: RedisService,
    private val imageUrlAliasDAO: ImageUrlAliasDAO,
    private val upscaylImageIndexDAO: UpscaylImageIndexDAO,
    private val upscaylTaskLogDAO: UpscaylTaskLogDAO,
    private val database: Database
) {

    suspend fun getUpscaylImage(url: String, model: String, traceId: String): String {
        val imageUrlAlias = imageUrlAliasDAO.getByUrl(url)
        if (imageUrlAlias != null) {
            val index = upscaylImageIndexDAO.getByHash(imageUrlAlias.hash)
            return index?.url ?: url
        } else {
            runInterruptible {
                val nx = redisService.setNx("UPSCAYL_TASK_LOCK:$url", url, 60 * 60 * 5)
                if (nx) {
                    val params = NetworkImageUpscaylParams(
                        guid = traceId,
                        url = url,
                        model = model
                    )
                    redisService.publish("NETWORK_IMAGE_UPSCAYL", Json.Default.encodeToString(params))
                }
            }
            return url
        }
    }

    suspend fun updateUrlAlias(imageUrlAlias: ImageUrlAlias): UpscaylImageIndex? {
        return newSuspendedTransaction(Dispatchers.IO, db = database) {
            val index = upscaylImageIndexDAO.getByHash(imageUrlAlias.hash)
            if (index != null) {
                imageUrlAliasDAO.insertIgnore(imageUrlAlias.url, index.hash)
            }
            return@newSuspendedTransaction index
        }
    }

    suspend fun saveUpscaylImage(provideUpscaylImage: ProvideUpscaylImage) {
        newSuspendedTransaction(Dispatchers.IO, db = database) {
            upscaylImageIndexDAO.insertIgnore(
                hash = provideUpscaylImage.sourceHash,
                url = provideUpscaylImage.upscaylUrl,
                scale = provideUpscaylImage.scale,
                model = provideUpscaylImage.model
            )
            imageUrlAliasDAO.insertIgnore(provideUpscaylImage.sourceUrl, provideUpscaylImage.sourceHash)
            if (provideUpscaylImage.taskResult != null) {
                upscaylTaskLogDAO.insertIgnore(provideUpscaylImage)
            }
        }
    }
}