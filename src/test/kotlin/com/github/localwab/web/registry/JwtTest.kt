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
import java.util.*


class JwtTest {
    private val key = UserRegistry().SIGNING_KEY

    @Test
    fun `decode JWT header`() {
        val issueDate = Date(1626852053000L)
        val expDate = Date(1626852053000L + 1000 * 60 * 60 * 24 * 7) // issue plus 7 days
        val util = JwtUtil(key) { issueDate }

        val uuid = UUID.fromString("17178982-ea1b-11eb-9a03-0242ac130003")
        val (token, expiration) = util.createToken("admin", uuid)
        Assertions.assertEquals(expDate, expiration)

        val split = token.split('.')
        val header = split[0]
        val payload = split[1]
        val signature = split[2]
        val decodedHeader = util.decode(header)
        Assertions.assertEquals("""{"typ":"JWT","alg":"HS256"}""", decodedHeader)
        val decodedPayload = util.decode(payload)
        Assertions.assertEquals(
            """{"user":"admin","wa:rand":"17178982ea1b11eb9a030242ac130003","iat":1626852053,"exp":1627456853}""",
            decodedPayload
        )
        Assertions.assertEquals("vhU5W10TseFcN7m1rnaSskTtwyzGC5mWmLmxSODzj94", signature)
    }

    @Test
    fun `createUuid`() {
        val util = JwtUtil(key) { Date(1626852053000L) }
        val uuid = util.parseUuid("17178982ea1b11eb9a030242ac130003")
        Assertions.assertEquals(UUID.fromString("17178982-ea1b-11eb-9a03-0242ac130003"), uuid)
    }
}