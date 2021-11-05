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

import com.github.localwab.web.domain.Callback
import com.github.localwab.web.domain.ConversationId
import com.github.localwab.web.domain.MessageId
import com.github.localwab.web.domain.WABID
import com.github.localwab.web.domain.WabMessage
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class MessageRegistry(val webhookRegistry: WebhookRegistry) {
    private val logger = KotlinLogging.logger {}

    private val messages: MutableMap<WABID, WabMessage> = ConcurrentHashMap()
    private val conversations: MutableMap<String, List<WABID>> = ConcurrentHashMap()

    private fun generateId(): String = "gBGG${UUID.randomUUID()}"

    fun send(requested: WabMessage): List<MessageId> {
        val id = generateId()
        val conversationId = "123"
        val sent = webhookRegistry.notify(id, requested, ConversationId(conversationId))
        logger.info { "Callbacks sent=$sent" }
        messages[id] = requested
        val messageId = MessageId(id)
        return listOf(messageId)
    }

    fun getAll(): List<Callback> {
        return listOf()
    }
}