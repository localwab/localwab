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

import com.github.localwab.web.registry.UserRegistry
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping()
class HealthController(val userRegistry: UserRegistry) {
    private val logger = KotlinLogging.logger {}

    private val statuses =
        listOf("connected", "connecting", "disconnected", "uninitialized", "unregistered")
    private var status = "connected"

    @Operation(
        summary = "Give hard coded status",
        description = "The health node supports Bearer token and API keys authentication.",
        externalDocs = ExternalDocumentation(
            description = "Use the health node to check the status of your WhatsApp Business API client.",
            url = "https://developers.facebook.com/docs/whatsapp/api/health"
        ),
        tags = ["WhatsApp API"]
    )
    @GetMapping("/v1/health")
    suspend fun getGatewayStatus(): ResponseEntity<WabResponse> {
        val jsonElement = buildJsonObject {
            putJsonObject("health") {
                put("gateway_status", status)
            }
        }
        val jsonText = jsonElement.toString()
        val result = TextResponse(jsonText)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @Operation(
        summary = "Put the status to be reported in WhatsApp API",
        description = "Provide the status value for testing",
        tags = ["Mock"]
    )
    @PutMapping("/mock/health")
    suspend fun putGatewayStatus(
        @RequestParam("status") providedStatus: String,
    ): ResponseEntity<WabResponse> {
        if (statuses.contains(providedStatus)) {
            status = providedStatus
            val jsonElement = buildJsonObject {
                putJsonObject("health") {
                    put("gateway_status", status)
                }
            }
            val jsonText = jsonElement.toString()
            val result = TextResponse(jsonText)
            return ResponseEntity(result, HttpStatus.OK)
        } else {
            val info = "Unexpected status: $providedStatus, must be $statuses"
            logger.info { info }
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, info)
        }
    }
}