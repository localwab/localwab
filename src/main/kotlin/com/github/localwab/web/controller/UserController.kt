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

import com.github.localwab.web.domain.AuthToken
import com.github.localwab.web.domain.ErrorMessage
import com.github.localwab.web.domain.ProvidePassword
import com.github.localwab.web.registry.UserRegistry
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/v1/users")
class UserController(val userRegistry: UserRegistry) {
    private val logger = KotlinLogging.logger {}

    /**
     * check that the provided HTTP body contains the new password
     */
    fun parseNewPassword(body: String?): Pair<String, ResponseEntity<WabResponse>?> {
        if (body == null) {
            val error = ErrorMessage(1024, "Password must be changed", "Password change required")
            return Pair(
                "",
                ResponseEntity(
                    ErrorResponse(listOf(error)),
                    HttpStatus.UNAUTHORIZED
                )
            )
        }
        return try {
            val parsed = Json.decodeFromString<ProvidePassword>(ProvidePassword.serializer(), body)
            Pair(parsed.new_password, null)
        } catch (e: Exception) {
            val error =
                ErrorMessage(
                    1025,
                    "Unable to decode json from the request: Syntax error",
                    "Request is not valid"
                )
            Pair(
                "",
                ResponseEntity(
                    ErrorResponse(listOf(error)),
                    HttpStatus.BAD_REQUEST
                )
            )
        }
    }

    /**
     * Check valid authentication
     * Return true if the credentials are correct
     */
    fun parseBasicAuth(prefix: String, base64: String): Pair<String, String>? {
        if ("Basic" != prefix) {
            logger.info { "Auth is invalid (Basic auth is expected): $prefix" }
            return null
        }
        val split = String(Base64.getDecoder().decode(base64)).split(':')
        val username = split[0]
        val password = split[1]
        return Pair(username, password)
    }

    @Operation(
        summary = "To authenticate yourself with WhatsApp Business API client",
        description = "Login and change the password on the first login",
        externalDocs = ExternalDocumentation(
            description = "To log in, send your username and password over basic authentication and receive a bearer token in response.",
            url = "https://developers.facebook.com/docs/whatsapp/api/users/login"
        ),
        tags = ["WhatsApp API"]
    )
    @PostMapping("/login")
    suspend fun login(
        @RequestBody(required = false) body: String?,
        @RequestHeader("Authorization") authorization: String?,
    ): ResponseEntity<WabResponse> {
        val newPassword = if (userRegistry.isAdminPasswordChangeRequired()) {
            val (providedPassword, noNewPasswordError) = parseNewPassword(body)
            if (noNewPasswordError != null) {
                logger.info { "New password is not provided: $body" }
                return noNewPasswordError
            }
            val valid = userRegistry.isPasswordWellFormatted(providedPassword)
            if (!valid) {
                logger.info { "Password is invalid: $providedPassword" }
                val error =
                    ErrorMessage(
                        1009,
                        """Password doesn't meet complexity requirements: length between 8 and 64 characters, at least 1 each of upper-case character, lower-case character, digit, special character (!\"#${'$'}%&'()*+,-./:;<=>?@[\\]^_`{|}~) are required""",
                        "Parameter value is not valid"
                    )
                return ResponseEntity(
                    ErrorResponse(listOf(error)),
                    HttpStatus.BAD_REQUEST
                )
            }
            providedPassword
        } else {
            null // no password change
        }
        val (prefix, base64) = UserRegistry.checkAuthIsWellFormatted(authorization)
        val pair = parseBasicAuth(prefix, base64)
        val (username, password) = pair ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val logged = userRegistry.login(username, password, newPassword)
        if (logged == null) {
            logger.info { "Provided password is invalid: $password" }
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        } else {
            val (token, expiration) = logged
            val expirationDate = com.github.localwab.web.DateUtils.convert(expiration)
            val resp = SuccessLoginResponse(
                listOf(AuthToken(expirationDate, token))
            )
            return ResponseEntity(resp, HttpStatus.OK)
        }
    }

    @Operation(
        summary = "Revoke the authentication token.",
        description = "Login and change the password on the first login",
        externalDocs = ExternalDocumentation(
            description = "Logging out revokes the authentication token.",
            url = "https://developers.facebook.com/docs/whatsapp/api/users/logout"
        ),
        tags = ["WhatsApp API"]
    )
    @PostMapping("/logout")
    suspend fun logout(@RequestHeader("Authorization") authorization: String?): ResponseEntity<WabResponse> {
        val (prefix, token) = UserRegistry.checkAuthIsWellFormatted(authorization)
        if ("Bearer" != prefix) {
            logger.info { "Auth is invalid (Bearer auth is expected with token): $prefix" }
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
        userRegistry.logoutToken(token)
        return ResponseEntity(LogoutResponse(), HttpStatus.OK)
    }
}