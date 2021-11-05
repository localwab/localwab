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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ContactRegistryTest {

    @Test
    fun `must be initially empty`() {
        val registry = ContactRegistry("/foo.json")
        Assertions.assertTrue(registry.findAll(listOf()).isEmpty())
    }

    @Test
    fun `parse contacts file`() {
        val jsonStream =
            Thread.currentThread().contextClassLoader.getResourceAsStream("contacts-1.json")
        val json = jsonStream.readBytes().toString(Charsets.UTF_8)
        val registry = ContactRegistry.importFrom(json)
        Assertions.assertEquals(3, registry.size)
    }
}