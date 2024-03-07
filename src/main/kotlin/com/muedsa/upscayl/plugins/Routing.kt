package com.muedsa.upscayl.plugins

import com.muedsa.upscayl.model.ExistUpscaylImageResp
import com.muedsa.upscayl.model.ImageUrlAlias
import com.muedsa.upscayl.model.ProvideUpscaylImage
import com.muedsa.upscayl.service.UpscaylImageService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting() {

    val service = get<UpscaylImageService>()

    routing {

        get("/upscayl") {
            try {
                val url = call.parameters["url"]
                check(!url.isNullOrEmpty()) { "Invalid URL" }
                check(url.length < 500) { "URL too long" }
                url.checkAsUrl()
                val proxyUrl = service.getUpscaylImage(url)
                call.respondRedirect(proxyUrl)
            } catch (t: Throwable) {
                call.respondText(t.message ?: "", status = HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-bearer") {
            get("/updateImageHash") {
                try {
                    val req = call.receive<ImageUrlAlias>()
                    val upscaylImage = service.updateUrlAlias(req)
                    call.respond(
                        ExistUpscaylImageResp(
                            hasImage = upscaylImage != null,
                            upscaylUrl = upscaylImage?.url
                        )
                    )
                } catch (t: Throwable) {
                    call.respondText(t.message ?: "", status = HttpStatusCode.BadRequest)
                }
            }

            post("/provide") {
                val provideUpscaylImage = call.receive<ProvideUpscaylImage>()
                service.saveUpscaylImage(provideUpscaylImage)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun String.checkAsUrl() {
    check(this.matches(Regex("^(http|https)://.*"))) { "Invalid URL" }
    try {
        Url(this)
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid URL")
    }
}