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

import com.github.localwab.web.domain.Contact
import com.github.localwab.web.registry.ContactRegistry
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mock/contacts")
class ContactsMockController(val contactRegistry: ContactRegistry) {

    @Operation(
        summary = "Show available contacts",
        description = "Show contacts either configured at startup or injected via PUT",
        tags = ["Mock"]
    )
    @GetMapping
    suspend fun getContacts(): Map<String, Contact> = contactRegistry.getAll()

    @Operation(
        summary = "Put all contacts",
        description = "Put all contacts replacing the previous",
        tags = ["Mock"]
    )
    @PutMapping()
    suspend fun putContacts(@RequestBody body: Map<String, Contact>) = contactRegistry.putAll(body)

    @Operation(
        summary = "Put a contact",
        description = "Put a contact to be able to send messages",
        tags = ["Mock"]
    )
    @PutMapping("/{id}")
    suspend fun putContact(
        @RequestBody body: Contact,
        @RequestParam id: String
    ) = contactRegistry.put(id, body)

    @Operation(
        summary = "Get a contact",
        description = "Get a contact to see if we can send messages",
        tags = ["Mock"]
    )
    @GetMapping("/{id}")
    suspend fun getContact(@RequestParam id: String) = contactRegistry.get(id)
}