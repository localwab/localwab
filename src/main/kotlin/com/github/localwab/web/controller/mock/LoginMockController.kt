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
package com.github.localwab.web.controller.mock

import com.github.localwab.web.controller.SuccessLoginResponse
import com.github.localwab.web.controller.WabResponse
import com.github.localwab.web.domain.AuthToken
import com.github.localwab.web.domain.CreateAccount
import com.github.localwab.web.registry.SettingsRegistry
import com.github.localwab.web.registry.UserRegistry
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginMockController(
    val settingsRegistry: SettingsRegistry,
    val userRegistry: UserRegistry,
    @Value("\${mock.default-new-password") val defaultNewPassword: String
) {
    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "Login and register single account",
        description = "Login and register the account in one request",
        tags = ["Mock"],
        parameters = [Parameter(
            name = "newPassword",
            required = false,
            description = "Optional password, if missing the env variable DEFAULT_NEW_PASSWORD is used",
            example = "Pass123-qwerty"
        )]
    )
    @PostMapping("/mock/login")
    suspend fun login(
        @RequestParam(required = false) newPassword: String = defaultNewPassword,
        @RequestBody body: CreateAccount
    ): ResponseEntity<WabResponse> {
        val (token, expiration) = userRegistry.mockLogin(newPassword)
        val expirationDate = com.github.localwab.web.DateUtils.convert(expiration)
        val resp = SuccessLoginResponse(
            listOf(AuthToken(expirationDate, token))
        )
        logger.info { "Mock login is successful. New password=$newPassword, account=$body" }
        return ResponseEntity(resp, HttpStatus.OK)
    }
}