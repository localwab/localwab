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

import com.github.localwab.web.domain.Callback
import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


@RestController
@RequestMapping("/mock/webhook")
class WebhookMockController {
    private val logger = KotlinLogging.logger {}

    private val callbacks: Queue<Callback> = ConcurrentLinkedQueue()

    @Operation(
        summary = "Dummy webhook URL for incoming callbacks (should not normally be used)",
        description = "Webhook URL to receive and keep callbacks. Application webhook should be used/tested instead.",
        tags = ["Mock"]
    )
    @PostMapping
    suspend fun postCallback(@RequestBody callback: Callback) {
        logger.info { "Received callback: $callback" }
        callbacks.add(callback)
    }

    @Operation(
        summary = "Get received callbacks",
        description = "Show all the received callbacks in the same order",
        tags = ["Mock"]
    )
    @GetMapping
    suspend fun getCallbacks() = callbacks.stream()
}