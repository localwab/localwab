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

import com.github.localwab.web.domain.WabMessage
import com.github.localwab.web.registry.MessageRegistry
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController()
class MessagesController(val messageRegistry: MessageRegistry) {
    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "Send messages",
        description = "Send text messages, media/documents, and message templates to your customers.",
        externalDocs = ExternalDocumentation(
            description = "Use the messages node to send text messages, media/documents, and message templates to your customers.",
            url = "https://developers.facebook.com/docs/whatsapp/api/messages"
        ),
        tags = ["WhatsApp API"]
    )
    @PostMapping("/v1/messages")
    suspend fun sendMessage(@RequestBody body: WabMessage): ResponseEntity<WabResponse> {
        logger.info { "Got message to be sent: $body" }
        val ids = messageRegistry.send(body)
        val result = MessagesResponse(ids)
        return ResponseEntity(result, HttpStatus.OK)
    }
}