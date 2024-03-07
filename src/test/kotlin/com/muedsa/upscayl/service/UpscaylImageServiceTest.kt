package com.muedsa.upscayl.service

import com.muedsa.upscayl.model.ProvideUpscaylImage
import com.muedsa.upscayl.module
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.koin.ktor.ext.get
import kotlin.test.Test

class UpscaylImageServiceTest {


    @Test
    fun db_test() = testApplication {
        application {
            val service = get<UpscaylImageService>()
            testSuspend {
                val provideUpscaylImage = ProvideUpscaylImage(
                    "https://samples-files.com/samples/Images/jpg/3840-2160-sample.jpg",
                    "hash-123",
                    "https://samples-files.com/samples/Images/jpg/3840-2160-sample.jpg",
                    "trace-id-123"
                )
                service.saveUpscaylImage(provideUpscaylImage)
            }
        }
    }
}