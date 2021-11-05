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
package com.github.localwab.web.controller

import com.github.localwab.web.registry.ContactRegistry
import com.github.localwab.web.registry.MessageRegistry
import com.github.localwab.web.registry.SettingsRegistry
import com.github.localwab.web.registry.UserRegistry
import com.github.localwab.web.registry.WebhookRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest
@Import(
    UserRegistry::class,
    ContactRegistry::class,
    SettingsRegistry::class,
    MessageRegistry::class,
    WebhookRegistry::class,
)
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SettingsControllerTest(@Autowired val client: WebTestClient) {
    private val SETTINGS_URL = "/v1/settings/application"
    private val ABOUT_URL = "/v1/settings/profile/about"
    private val BUSINESS_URL = "/v1/settings/business/profile"

    @Autowired
    val userRegistry: UserRegistry? = null

    private var token: String? = null

    @BeforeAll
    fun setUp() {
        val (token, _) = userRegistry!!.login("admin", "secret", "Pass123-qwerty")!!
        this.token = token
    }

    @Test
    fun `get general settings`() {
        val emptyResult = client.get().uri(SETTINGS_URL)
            .headers { headers -> headers.setBearerAuth("$token") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ApplicationSettingsResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals("", emptyResult.settings.wa_id, "Must be empty initially.")
        Assertions.assertEquals("", emptyResult.settings.webhooks.url, "Must be empty initially.")
    }

    @Test
    fun `get profile data`() {
        val emptyResult = client.get().uri(ABOUT_URL)
            .headers { headers -> headers.setBearerAuth("$token") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<TextResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals(
            """{"settings":{"profile":{"about":{"text":""}}}}""",
            emptyResult.body,
            "Must be empty initially."
        )

        // set text
        val body = """{
                "text": "My Account 777"
            }"""
        val result = client.patch().uri(ABOUT_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .headers { headers -> headers.setBearerAuth("$token") }
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<TextResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals("My Account 777", result.body)

        // check
        val changedResult = client.get().uri(ABOUT_URL)
            .headers { headers -> headers.setBearerAuth("$token") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<TextResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals(
            """{"settings":{"profile":{"about":{"text":"My Account 777"}}}}""",
            changedResult.body
        )
    }

    @Test
    fun `get business profile data`() {
        val emptyResult = client.get().uri(BUSINESS_URL)
            .headers { headers -> headers.setBearerAuth("$token") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<TextResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals(
            """{"settings":{"business":{"profile":{"address":"Address","description":"Description","email":"me@here.com","vertical":"Telecom","websites":["http://www.here.work"]}}}}""",
            emptyResult.body
        )
    }
}