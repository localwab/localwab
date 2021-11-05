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
import org.springframework.web.server.ResponseStatusException

class UserRegistryTest {

    @Test
    fun `must be initially with admin password`() {
        val registry = UserRegistry()
        Assertions.assertTrue(registry.isAdminPasswordChangeRequired())
    }

    @Test
    fun `must detect valid password`() {
        val registry = UserRegistry()
        Assertions.assertFalse(registry.isPasswordWellFormatted(""))
        Assertions.assertFalse(registry.isPasswordWellFormatted("Pp1!qqq"), "Too short")
        Assertions.assertFalse(
            registry.isPasswordWellFormatted("Pp1!5678901234567890123456789012345678901234567890123456789012345"),
            "Too long"
        )
        Assertions.assertFalse(
            registry.isPasswordWellFormatted("pp1!56789012345678901234567890123456789012345678901234"),
            "Missing uppercase"
        )
        Assertions.assertFalse(registry.isPasswordWellFormatted("PP1!PPPP"), "Missing lowercase")
        Assertions.assertFalse(registry.isPasswordWellFormatted("Ppp{qqqq"), "Missing digit")

        Assertions.assertTrue(registry.isPasswordWellFormatted("Pp1&5678901234"))
        Assertions.assertTrue(registry.isPasswordWellFormatted("Ab1]567890123456789012345678901234567890123456789012345678901234"))
        Assertions.assertTrue(registry.isPasswordWellFormatted("Pass123-qwerty"))
    }

    @Test
    fun `do not accept empty auth header`() {
        val registry = UserRegistry
        Assertions.assertEquals(
            Pair("Bearer", "123"),
            registry.checkAuthIsWellFormatted("Bearer 123")
        )
        Assertions.assertEquals(
            Pair("Basic", "YWRtaW46c2VjcmV0"),
            registry.checkAuthIsWellFormatted("Basic YWRtaW46c2VjcmV0")
        )
        Assertions.assertEquals(
            Pair("Basic", "YWRtaW46c2VjcmV0"),
            registry.checkAuthIsWellFormatted(" Basic  YWRtaW46c2VjcmV0 ")
        )
        Assertions.assertEquals(
            Pair("Bearer", "foo:bar"),
            registry.checkAuthIsWellFormatted(" Bearer  foo:bar ")
        )

        Assertions.assertThrows(
            ResponseStatusException::class.java,
            { registry.checkAuthIsWellFormatted(null) })
        Assertions.assertThrows(
            ResponseStatusException::class.java,
            { registry.checkAuthIsWellFormatted("") })
        Assertions.assertThrows(
            ResponseStatusException::class.java,
            { registry.checkAuthIsWellFormatted(" ") })
        Assertions.assertThrows(
            ResponseStatusException::class.java,
            { registry.checkAuthIsWellFormatted("\t") })
        Assertions.assertThrows(
            ResponseStatusException::class.java,
            { registry.checkAuthIsWellFormatted("user:password") })
        Assertions.assertThrows(
            ResponseStatusException::class.java,
            { registry.checkAuthIsWellFormatted("foo") })
    }
}