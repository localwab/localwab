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
class UserControllerTest(@Autowired val client: WebTestClient) {
    private val LOGIN_URL = "/v1/users/login"
    private val LOGOUT_URL = "/v1/users/logout"


    @Test
    fun `proper request should be accepted and password changed`() {
        val body = """{
                            "new_password": "Pass123-qwerty"
                    }"""
        val result = client.post().uri(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .headers { headers -> headers.setBasicAuth("admin", "secret") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<SuccessLoginResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals(1, result.users.size)
        val token = result.users[0]
        Assertions.assertTrue(token.expires_after.startsWith("202"), token.expires_after)
        Assertions.assertTrue(token.expires_after.endsWith("+00:00"), token.expires_after)
        Assertions.assertEquals(3, token.token.split('.').size)

        // logout
        client.post().uri(LOGOUT_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .headers { headers -> headers.setBearerAuth(token.token) }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<LogoutResponse>()
            .isEqualTo(LogoutResponse())
    }

    @Test
    fun `authenticateLogin with Basic authentication`() {
        val registry = UserRegistry()
        val controller = UserController(registry)
        Assertions.assertEquals(
            Pair("admin", "secret"),
            controller.parseBasicAuth("Basic", "YWRtaW46c2VjcmV0")
        )

        Assertions.assertNull(
            controller.parseBasicAuth("Bearer", "YWRtaW46c2VjcmV0"),
            "wrong prefix"
        )
    }
}