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
package com.github.localwab.web

import com.github.localwab.web.controller.ContactsResponse
import com.github.localwab.web.domain.Contact
import com.github.localwab.web.domain.GetContacts
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ModelTest {

    @Test
    fun `parse full get contacts`() {
        val input = """
            {
              "blocking":  "wait",
              "contacts": [
                "16315551000",
                "+1 631 555 1001",
                "6315551002",
                "+1 (631) 555-1004",
                "1-631-555-1005"
              ],
              "force_check": true
            }"""
        val contacts = Json.decodeFromString(GetContacts.serializer(), input)
        Assertions.assertEquals("wait", contacts.blocking)
        Assertions.assertTrue(contacts.force_check)
        val expectList = listOf<String>(
            "16315551000",
            "+1 631 555 1001",
            "6315551002",
            "+1 (631) 555-1004",
            "1-631-555-1005"
        )
        Assertions.assertEquals(expectList, contacts.contacts)
    }

    @Test
    fun `parse get contacts`() {
        val input = """
            {
              "contacts": [
                "+1 631 555 1001",
                "+1 (631) 555-1004"
              ]
            }"""
        val contacts = Json.decodeFromString(GetContacts.serializer(), input)
        Assertions.assertEquals("no_wait", contacts.blocking)
        Assertions.assertFalse(contacts.force_check)
        val expectList = listOf<String>(
            "+1 631 555 1001",
            "+1 (631) 555-1004"
        )
        Assertions.assertEquals(expectList, contacts.contacts)
    }

    @Test
    fun `serialize ContactsResponse`() {
        val response = ContactsResponse(
            listOf(
                Contact("+16315551000", "valid", "16315551000"),
                Contact("+16315551000", "processing", "16315551000"),
                Contact("6315551002", "invalid", "16315551000"),
                Contact("+163155588", "failed", "16315551000")
            )
        )
        val json = Json.encodeToString(ContactsResponse.serializer(), response)
        Assertions.assertEquals(
            """{"contacts":[{"input":"+16315551000","status":"valid","wa_id":"16315551000"},{"input":"+16315551000","status":"processing","wa_id":"16315551000"},{"input":"6315551002","status":"invalid","wa_id":"16315551000"},{"input":"+163155588","status":"failed","wa_id":"16315551000"}]}""",
            json
        )
        val parsed = Json.decodeFromString(ContactsResponse.serializer(), json)
        Assertions.assertEquals(response, parsed)
    }

}
