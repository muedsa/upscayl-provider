package com.muedsa.upscayl.model

import kotlinx.serialization.Serializable

@Serializable
data class UpscaylTaskResult(
    val exitCode: Int,
    val message: String,
    val startTime: Long,
    val endTime: Long
) {
    val success: Boolean by lazy {
        exitCode == 0 && message.contains("Upscayl Successful")
    }
}
