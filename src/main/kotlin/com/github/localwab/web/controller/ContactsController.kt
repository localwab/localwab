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

import com.github.localwab.web.domain.GetContacts
import com.github.localwab.web.domain.Identity
import com.github.localwab.web.registry.ContactRegistry
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/v1/contacts")
class ContactsController(val contactRegistry: ContactRegistry) {
    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "Get status and ID for contacts",
        description = "Verify the already registered account",
        externalDocs = ExternalDocumentation(
            description = "Use the contacts node to manage WhatsApp users in your database",
            url = "https://developers.facebook.com/docs/whatsapp/api/contacts"
        ),
        tags = ["WhatsApp API"]
    )
    @PostMapping()
    suspend fun getContacts(@RequestBody body: GetContacts): ResponseEntity<WabResponse> {
        if (body.blocking != "wait") {
            logger.info { "Only blocking method 'wait' is supported. Ignore ${body.blocking}" }
        }
        if (body.force_check) {
            logger.info { "Forcing check is ignored. Local WAB has no cache." }
        }
        val found = contactRegistry.findAll(body.contacts)
        val result = ContactsResponse(found)
        return ResponseEntity(result, HttpStatus.OK)
    }


    @Operation(
        summary = "Get user identity",
        description = "Use this /v1/contacts edge to manage users' identities.",
        externalDocs = ExternalDocumentation(
            description = "A GET call to this endpoint retrieves the latest identity hash for a user.",
            url = "https://developers.facebook.com/docs/whatsapp/api/contacts/users-whatsapp-id/identity"
        ),
        tags = ["WhatsApp API"]
    )
    @GetMapping("/{id}/identity")
    //TODO identity endpoint is not done yet
    suspend fun getIdentity(@RequestParam id: String): ResponseEntity<WabResponse> {
        val result = IdentityResponse(Identity("UKD6Y/5SqDU=", 1600132094000L, true))
        return ResponseEntity(result, HttpStatus.OK)
    }

    @Operation(
        summary = "Acknowledge the latest user_identity_changed system notification",
        description = "Use this /v1/contacts edge to manage users' identities.",
        externalDocs = ExternalDocumentation(
            description = "Use PUT calls to this endpoint to acknowledge the latest user_identity_changed system notification.",
            url = "https://developers.facebook.com/docs/whatsapp/api/contacts/users-whatsapp-id/identity"
        ),
        tags = ["WhatsApp API"]
    )
    @PutMapping("/{id}/identity")
    //TODO identity endpoint is not done yet
    suspend fun putIdentity(
        @RequestParam id: String,
        @RequestBody body: String
    ): ResponseEntity<WabResponse> {
        throw UnsupportedOperationException("TODO: https://developers.facebook.com/docs/whatsapp/api/contacts/users-whatsapp-id/identity")
    }
}