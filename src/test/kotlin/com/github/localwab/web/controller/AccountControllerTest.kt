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
class AccountControllerTest(@Autowired val client: WebTestClient) {
    private val ACCOUNT_URL = "/v1/account"
    private val CHOPPED_CERTIFICATE =
        "CmcKIwjGvfOpuoTSAxIGZW50OndhIgpBbmRyZXlDb21wUIDVr4gGGkCh4oI3YCoRpaVKgqIf4fu3fqkcwWdk"

    @Autowired
    val userRegistry: UserRegistry? = null

    @Test
    fun `non-verified account is created`() {
        val (token, _) = userRegistry!!.login("admin", "secret", "Pass123-qwerty")!!

        val body = """{
    "cc": "971",
    "phone_number": "555555555",
    "method": "sms",
    "cert": "$CHOPPED_CERTIFICATE",
    "pin": "123456"
}"""
        val result = client.post().uri(ACCOUNT_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .headers { headers -> headers.setBearerAuth("$token") }
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<AccountResponse>()
            .returnResult().responseBody!!
        Assertions.assertEquals(1, result.account.size)
        val map = result.account[0]
        Assertions.assertEquals(mapOf("vname" to "AndreyComp"), map)
    }
}