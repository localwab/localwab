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

import com.github.localwab.web.domain.ErrorMessage
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
class UserControllerFailureTest(@Autowired val client: WebTestClient) {
    private val URL = "/v1/users/login"

    @Test
    fun `GET method should be rejected`() {
        client.get().uri(URL).exchange()
            .expectStatus().is4xxClientError
    }

    @Test
    fun `fail without auth header`() {
        val body = """{
                            "new_password": "Pass123-qwerty"
                    }"""
        client.post().uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().exists("WWW-Authenticate")
    }

    @Test
    fun `fail without body with 401 and code 1024`() {
        val error =
            ErrorMessage(1024, "Password must be changed", "Password change required")

        client.post().uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .headers { headers -> headers.setBasicAuth("admin", "secret") }
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().values("WWW-Authenticate") { list ->
                Assertions.assertEquals(
                    """Basic realm="Secured"""",
                    list[0]
                )
            }
            .expectBody<ErrorResponse>()
            .isEqualTo(ErrorResponse(listOf(error)))
    }

    @Test
    fun `fail with invalid JSON in body with 400 and code 1025`() {
        val error =
            ErrorMessage(
                1025,
                "Unable to decode json from the request: Syntax error",
                "Request is not valid"
            )

        val body = "Foo"
        client.post().uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .headers { headers -> headers.setBasicAuth("admin", "secret") }
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().exists("WWW-Authenticate")
            .expectBody<ErrorResponse>()
            .isEqualTo(ErrorResponse(listOf(error)))
    }

    /**
     * TODO This deviates from WAB. It gives 401 and code 1024
     */
    @Test
    fun `fail with missing property new_password in JSON in body with 401 and code 1024`() {
        val error =
            ErrorMessage(
                1025,
                "Unable to decode json from the request: Syntax error",
                "Request is not valid"
            )

        val body = """{
                            "foo": "bar"
                    }"""
        client.post().uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .headers { headers -> headers.setBasicAuth("admin", "secret") }
            .exchange()
            .expectStatus().isBadRequest //TODO WAB has 401 here
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().exists("WWW-Authenticate")
            .expectBody<ErrorResponse>()
            .isEqualTo(ErrorResponse(listOf(error)))
    }

    @Test
    fun `fail with invalid new_password in JSON in body with 400 and code 1009`() {
        val error =
            ErrorMessage(
                1009,
                """Password doesn't meet complexity requirements: length between 8 and 64 characters, at least 1 each of upper-case character, lower-case character, digit, special character (!\"#${'$'}%&'()*+,-./:;<=>?@[\\]^_`{|}~) are required""",
                "Parameter value is not valid"
            )

        val body = """{
                            "new_password": "bar"
                    }"""
        client.post().uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .headers { headers -> headers.setBasicAuth("admin", "secret") }
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().exists("WWW-Authenticate")
            .expectBody<ErrorResponse>()
            .isEqualTo(ErrorResponse(listOf(error)))
    }
}