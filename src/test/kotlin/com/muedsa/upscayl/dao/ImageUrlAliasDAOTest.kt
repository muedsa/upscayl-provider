package com.muedsa.upscayl.dao

import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.koin.ktor.ext.get
import kotlin.test.Test

class ImageUrlAliasDAOTest {

    @Test
    fun getByUrl_test() = testApplication {
        application {
            val dao = get<ImageUrlAliasDAO>()
            testSuspend {
                dao.getByUrl("")
            }
        }
    }
}