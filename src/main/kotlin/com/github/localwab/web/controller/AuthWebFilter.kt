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

import com.github.localwab.web.registry.UserRegistry
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AuthWebFilter(
    private val userRegistry: UserRegistry,
    @Value("\${wab.wa-api-key}") val waApiKey: String
) : WebFilter {
    private val logger = KotlinLogging.logger {}

    override fun filter(
        serverWebExchange: ServerWebExchange,
        webFilterChain: WebFilterChain
    ): Mono<Void?> {
        val path = serverWebExchange.request.path
        logger.info { "------------- $path" }
        if (path.value().startsWith("/v1")) {
            when (path.value()) {
                "/v1/users/login" -> {
                    serverWebExchange.response
                        .headers.add("WWW-Authenticate", """Basic realm="Secured"""")
                }
                "/v1/health" -> {
                    val header = getAuthHeader(serverWebExchange)
                    val (prefix, token) = UserRegistry.checkAuthIsWellFormatted(header)
                    when (prefix) {
                        "Bearer" -> {
                            val uuid = userRegistry.validateToken(token)
                            logger.info { "Token is valid. wa:rand=$uuid" }
                        }
                        "Apikey" -> {
                            if (waApiKey != token) {
                                val info =
                                    "Provided token (Apikey) is invalid: $token (expected: ${waApiKey})"
                                logger.info { info }
                                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, info)
                            }
                        }
                        else -> {
                            val info =
                                "Auth is invalid (Bearer or Apikey token is expected): $prefix"
                            logger.info { info }
                            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, info)
                        }
                    }
                }
                else -> {
                    val header = getAuthHeader(serverWebExchange)
                    val (prefix, token) = UserRegistry.checkAuthIsWellFormatted(header)
                    if ("Bearer" != prefix) {
                        val info = "Auth is invalid (Bearer token is expected): $prefix"
                        logger.info { info }
                        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, info)
                    }
                    val uuid = userRegistry.validateToken(token)
                    logger.info { "Token is valid. wa:rand=$uuid" }
                }
            }
        } else {
            logger.info { "Ignore AuthWebFilter" }
        }
        return webFilterChain.filter(serverWebExchange)
    }

    private fun getAuthHeader(serverWebExchange: ServerWebExchange): String {
        val headerList = serverWebExchange.request.headers.getOrEmpty(AUTHORIZATION)
        return if (headerList.isEmpty()) {
            val info = "Authorization must be provided."
            logger.info { info }
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, info)
        } else {
            headerList.first()
        }
    }
}