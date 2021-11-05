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
package com.github.localwab.web.registry

import com.github.localwab.web.controller.ContactsResponse
import com.github.localwab.web.domain.Contact
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap


@Service
class ContactRegistry(@Value("\${mock.import.contacts-file-name}") val contactsFilename: String) {
    private val logger = KotlinLogging.logger {}

    init {
        importContacts()
    }

    private val contacts: MutableMap<String, Contact> = ConcurrentHashMap<String, Contact>()

    companion object {
        fun importFrom(json: String): Map<String, Contact> {
            val parsed = Json.decodeFromString(ContactsResponse.serializer(), json)
            return parsed.contacts.associateBy { contact -> contact.input }
        }
    }

    fun findAll(requested: List<String>): List<Contact> {
        val (found, notFound) = requested.partition { phone -> requested.contains(phone) }
        val registered: List<Contact> = found.map { phone -> contacts[phone]!! }
        val invalid: List<Contact> = notFound.map { phone -> Contact(phone, "invalid") }
        return registered + invalid
    }

    private fun importContacts() {
        val path = Paths.get(contactsFilename)
        if (Files.exists(path) && Files.isRegularFile(path)) {
            logger.info { "Detected JSON file with contacts at $contactsFilename" }
            val content = path.toFile().readText(Charsets.UTF_8)
            val toImport = importFrom(content)
            putAll(toImport)
            logger.info { "Imported ${toImport.size}" }
        } else {
            logger.info { "No file with contacts found to import at $contactsFilename" }
        }
    }

    fun getAll() = contacts

    fun putAll(toImport: Map<String, Contact>) {
        contacts.clear()
        contacts.putAll(toImport)
    }

    fun put(id: String, contact: Contact) {
        contacts[id] = contact
    }

    fun get(id: String) = contacts[id]
}