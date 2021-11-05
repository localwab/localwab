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

import com.github.localwab.web.domain.AccountVerification
import com.github.localwab.web.domain.CreateAccount
import com.github.localwab.web.domain.ErrorMessage
import com.github.localwab.web.registry.SettingsRegistry
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v1/account")
class AccountController(val settingsRegistry: SettingsRegistry) {
    private val logger = KotlinLogging.logger {}

    private val lengthIndexInCertificate = 22

    @Operation(
        summary = "Register single account",
        description = "Register the account",
        externalDocs = ExternalDocumentation(
            description = "The account node is for registering your WhatsApp Business API client.",
            url = "https://developers.facebook.com/docs/whatsapp/api/account"
        ),
        tags = ["WhatsApp API"]
    )
    @PostMapping()
    suspend fun createAccount(@RequestBody body: CreateAccount): ResponseEntity<WabResponse> {
        val certificate = body.cert
        logger.info { "Cert=$certificate" }
        val decodedBytes = Base64.getDecoder().decode(certificate)
        val length = decodedBytes[lengthIndexInCertificate].toInt()
        val vname = String(
            decodedBytes.sliceArray(
                IntRange(
                    lengthIndexInCertificate + 1,
                    lengthIndexInCertificate + length
                )
            )
        )
        logger.info { "decoded=$vname" }
        settingsRegistry.setAccount(body)
        val result = AccountResponse(listOf(mapOf("vname" to vname)))
        return ResponseEntity(result, HttpStatus.CREATED)
    }

    @Operation(
        summary = "Verify registered account",
        description = "Verify the already registered account",
        externalDocs = ExternalDocumentation(
            description = "Once you have received the registration code via your specified method, you can complete your account registration by sending an API call to /v1/account/verify.",
            url = "https://developers.facebook.com/docs/whatsapp/api/account/verify"
        ),
        tags = ["WhatsApp API"]
    )
    @PostMapping("/verify")
    suspend fun verifyAccount(@RequestBody verification: AccountVerification): ResponseEntity<WabResponse> {
        logger.info { "code=${verification.code}" }
        val verified = settingsRegistry.verifyAccount(verification.code)
        return if (verified) {
            val success = TextResponse("")
            ResponseEntity(success, HttpStatus.CREATED)
        } else {
            val error = ErrorResponse(
                listOf(
                    ErrorMessage(
                        1005,
                        "Error checking code (missing)",
                        "Access denied"
                    )
                )
            )
            ResponseEntity(error, HttpStatus.FORBIDDEN)
        }
    }
}