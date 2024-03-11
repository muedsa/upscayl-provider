package com.muedsa.upscayl.model

import kotlinx.serialization.Serializable

@Serializable
data class ProvideUpscaylImage(
    val sourceUrl: String,
    val sourceHash: String,
    val upscaylUrl: String,
    val scale: Int,
    val model: String,
    val traceId: String = "",
    val taskResult: UpscaylTaskResult? = null
)