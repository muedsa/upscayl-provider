package com.muedsa.upscayl.model

import kotlinx.serialization.Serializable

@Serializable
data class UpscaylImageIndex(
    val hash: String,
    val url: String,
)