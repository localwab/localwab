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

import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import java.util.*

class JwtUtil(SIGNING_KEY: String, private val dateProvider: DateProvider) {
    private val logger = KotlinLogging.logger {}
    private val key = Keys.hmacShaKeyFor(SIGNING_KEY.toByteArray())

    fun createToken(user: String, rand: UUID): Pair<String, Date> {
        val now = dateProvider.getDate()
        val expiration = Date(now.time + 604800000L)
        val random = rand.toString().replace("-", "")
        val claims: Map<String, String> =
            mapOf("user" to user, "wa:rand" to random)
        return Pair(
            Jwts.builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE).setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key).compact(), expiration
        )
    }

    fun validateToken(token: String, providedUsername: String): UUID? {
        val body = Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).body
        val expiration = body.expiration
        if (dateProvider.getDate().after(expiration)) {
            logger.info { "Token expired: $token" }
            return null
        }
        val username = body["user"]
        if (username != providedUsername) {
            logger.info { "Name $username does not match in: $token" }
            return null
        }
        return parseUuid(body["wa:rand"].toString())
    }

    /**
     * Add removed '-' to parse UUID
     */
    fun parseUuid(id: String): UUID {
        val withDashes = StringBuilder(id)
            .insert(8, '-')
            .insert(13, '-')
            .insert(18, '-')
            .insert(23, '-')
            .toString()
        return UUID.fromString(withDashes)
    }

    fun decode(base64UrlEncoded: String) = String(Base64.getUrlDecoder().decode(base64UrlEncoded))
}
