package com.muedsa.upscayl.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrlAlias(
    val url: String,
    val hash: String
)
