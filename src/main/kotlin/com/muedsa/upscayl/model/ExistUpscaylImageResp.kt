package com.muedsa.upscayl.model

import kotlinx.serialization.Serializable

@Serializable
data class ExistUpscaylImageResp(
    val hasImage: Boolean = false,
    val upscaylUrl: String? = null
)
