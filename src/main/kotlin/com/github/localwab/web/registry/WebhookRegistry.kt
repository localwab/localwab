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

import com.github.localwab.web.domain.ConversationId
import com.github.localwab.web.domain.Pricing
import com.github.localwab.web.domain.StatusType
import com.github.localwab.web.domain.WabMessage
import com.github.localwab.web.domain.WabStatus
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * Send outgoing callbacks to provided webhook URL
 */
@Service
class WebhookRegistry(val settingsRegistry: SettingsRegistry) {
    private val logger = KotlinLogging.logger {}

    private val callbackMap: MutableMap<String, List<WabStatus>> = ConcurrentHashMap()

    fun notify(id: String, requested: WabMessage, conversationId: ConversationId? = null): Boolean {
        val urlString = settingsRegistry.getWebhookUrl()
        return if (urlString.isEmpty()) {
            logger.info { "No webhook URL is set, do not send callbacks" }
            false
        } else {
            val url = URL(urlString)
            val timestamp = "${System.currentTimeMillis()}"
            val pricing = Pricing(false, "NBP")

            val wabStatusSent =
                WabStatus(id, requested.to, StatusType.sent, timestamp, conversationId, pricing)
            processStatus(url, wabStatusSent)

            val wabStatusDelivered = wabStatusSent.copy(
                status = StatusType.delivered,
                timestamp = timestamp + 10
            )
            processStatus(url, wabStatusDelivered)

            val wabStatusRead =
                wabStatusSent.copy(
                    status = StatusType.read,
                    timestamp = timestamp + 20,
                    conversation = null,
                    pricing = null
                )
            processStatus(url, wabStatusRead)

            true
        }
    }

    fun processStatus(url: URL, status: WabStatus) {
        val previous = callbackMap.getOrDefault(status.id, listOf())
        val next = previous + status
        callbackMap[status.id] = next
        val json = Json.encodeToString(WabStatus.serializer(), status)
        val success = sendCallback(url, json)
        if (success) {
            logger.info { "Failed to send status Callback: $status" }
        }
    }

    fun sendCallback(url: URL, json: String): Boolean {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            outputStream.bufferedWriter().write(json)
            return if (responseCode == 200) {
                logger.info { "Callback returned code: $responseCode" }
                for (key in headerFields.keys) {
                    val value = headerFields[key]
                    logger.info { "Header key=$key, value=$value" }
                }
                var lines: List<String> = listOf()
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        lines = lines + line
                    }
                }
                val reply = lines.joinToString { "\n" }
                logger.info { "Reply=$reply" }
                true
            } else {
                logger.info { "Callback returned code: $responseCode" }
                false
            }
        }
    }
}