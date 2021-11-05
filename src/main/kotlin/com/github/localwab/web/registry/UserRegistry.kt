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

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@Service
class UserRegistry {

    companion object {
        private val logger = KotlinLogging.logger {}

        /**
         * Check and split the auth header
         */
        fun checkAuthIsWellFormatted(authorization: String?): Pair<String, String> {
            if (authorization == null || authorization.isBlank()) {
                val info = "Authorization must be provided."
                logger.info { info }
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, info)
            }
            val regex = """\s+""".toRegex() // any space(s) as the delimiter
            val split = regex.split(authorization.trim())
            if (split.size != 2) {
                val info =
                    "Valid authorization must be provided (size=${split.size}): $authorization"
                logger.info { info }
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, info)
            }
            return Pair(split[0].trim(), split[1].trim())
        }
    }

    val ADMIN_USERNAME = "admin"
    val ADMIN_PASSWORD = "secret"
    val SIGNING_KEY = "zk-ZJafbsym5L7gpmXHUgYNYVbxUpuHnts6ndOfmaF4"
    private var requiredPasswordChange: AtomicBoolean = AtomicBoolean(true)
    private var adminPassword: AtomicReference<String> = AtomicReference(ADMIN_PASSWORD)
    private val logger = KotlinLogging.logger {}

    private val tokens: MutableMap<UUID, String> = ConcurrentHashMap<UUID, String>()

    fun isAdminPasswordChangeRequired(): Boolean = requiredPasswordChange.get()

    /**
     * length between 8 and 64 characters, at least 1 each of upper-case character,
     * lower-case character, digit,
     * special character (!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~) are required
     */
    fun isPasswordWellFormatted(password: String): Boolean {
        if (password.length < 8 || password.length > 64) return false
        if (password.uppercase() == password || password.lowercase() == password) return false
        val specs = """!\"#${'$'}%&'()*+,-./:;<=>?@[\\]^_`{|}~"""
        val digits = "0123456789"
        if (!password.any { ch -> specs.contains(ch) }) return false
        if (!password.any { ch -> digits.contains(ch) }) return false
        return true
    }

    private fun setPassword(password: String) {
        adminPassword.set(password)
        logger.info { "Password set: $password" }
        requiredPasswordChange.set(false)
    }

    private fun generateToken(): Pair<String, Date> {
        val util = JwtUtil(SIGNING_KEY) { Date() }
        val uuid = UUID.randomUUID()
        val (token, expiration) = util.createToken(ADMIN_USERNAME, uuid)
        tokens[uuid] = token
        return token to expiration
    }

    fun validateToken(token: String): UUID {
        val util = JwtUtil(SIGNING_KEY) { Date() }
        val uuid = util.validateToken(token, ADMIN_USERNAME)
        return uuid ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }

    /**
     * Remove all the tokens created for this user
     */
    fun logoutToken(token: String) {
        val uuid = validateToken(token)
        val value = tokens.remove(uuid)
        val found = value ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        logger.info { "Token invalidated: $found" }
        tokens.clear()
    }

    private fun isPasswordCorrect(username: String, password: String): Boolean {
        val pass: String = if (requiredPasswordChange.get())
            ADMIN_PASSWORD
        else
            adminPassword.get()
        return username == ADMIN_USERNAME && password == pass
    }

    fun login(username: String, password: String, newPassword: String?): Pair<String, Date>? {
        val authenticated = isPasswordCorrect(username, password)
        if (isAdminPasswordChangeRequired()) setPassword(newPassword!!)
        return if (authenticated) generateToken() else null
    }

    fun mockLogin(newPassword: String): Pair<String, Date> {
        setPassword(newPassword)
        return generateToken()
    }
}