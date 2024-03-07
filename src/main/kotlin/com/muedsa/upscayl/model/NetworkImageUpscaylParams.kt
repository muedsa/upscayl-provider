package com.muedsa.upscayl.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkImageUpscaylParams(
    val guid: String,
    val url: String,
    val scale: Int = 4,
    val model: String = "realesrgan-x4plus",
    val format: String = "png"
)