/*
 * Copyright (c) 2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.localwab.web

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@SpringBootApplication
class WebApplication

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}

@Configuration
class Config {
    @Bean
    fun springShopOpenAPI(): OpenAPI? {
        val docs = ExternalDocumentation()
            .description("Local WAB docs")
            .url("http://springdoc.org")
        val whatsAppDoc = ExternalDocumentation()
            .description("WhatsApp Business API")
            .url("https://developers.facebook.com/docs/whatsapp")
        return OpenAPI()
            .info(
                Info().title("Local WAB API")
                    .description("WAB instance unconnected to WhatsApp")
                    .version("v1.0")
                    //.contact(Contact().name("Andrey Somov").url("https://github.com/asomov").email("public.somov@gmail.com"))
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
            .externalDocs(
                whatsAppDoc
            ).tags(
                listOf(
                    Tag().name("WhatsApp API").description("Part of the official API")
                        .externalDocs(whatsAppDoc),
                    Tag().name("Mock").description("Support API").externalDocs(docs)
                )
            )
    }
}
