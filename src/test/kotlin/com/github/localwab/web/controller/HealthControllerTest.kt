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
import org.junit.jupiter.api.Test
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
class HealthControllerTest(@Autowired val client: WebTestClient) {
    private val HEALPH_URL = "/v1/health"

    @Autowired
    val userRegistry: UserRegistry? = null

    @Test
    fun `check health with token`() {
        val (token, _) = userRegistry!!.login("admin", "secret", "Pass123-qwerty")!!

        val result = client.get().uri(HEALPH_URL)
            .headers { headers -> headers.setBearerAuth(token) }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<TextResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals("""{"health":{"gateway_status":"connected"}}""", result.body)
    }

    @Test
    fun `check health with wrong hard coded token`() {
        val result = client.get().uri(HEALPH_URL)
            .headers { headers -> headers.set("Authorization", "Apikey 123") }
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<String>()
            .returnResult().responseBody!!
        Assertions.assertTrue(result.contains(""""path":"/v1/health""""))
        Assertions.assertTrue(result.contains(""""status":401"""))
    }

    @Test
    fun `check health with correct hard coded token`() {
        val result = client.get().uri(HEALPH_URL)
            .headers { headers ->
                headers.set(
                    "Authorization",
                    "Apikey this-is-test-token-for-health"
                )
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<TextResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals("""{"health":{"gateway_status":"connected"}}""", result.body)
    }
}