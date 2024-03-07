package com.muedsa.upscayl.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    val bearerToken = environment.config.property("ktor.security.bearer.token").getString()

    install(Authentication) {
        bearer("auth-bearer") {
            authenticate {tokenCredential ->
                if (tokenCredential.token == bearerToken) {
                    UserIdPrincipal("user")
                } else {
                   null
                }
            }
        }
    }
}
