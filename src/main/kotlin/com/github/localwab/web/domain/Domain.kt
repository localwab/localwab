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
package com.github.localwab.web.domain

import kotlinx.serialization.Serializable

@Serializable
data class Meta(val api_status: String = "stable", val version: String = "v2.33.4")

@Serializable
data class ProvidePassword(val new_password: String)

@Serializable
data class AuthToken(val expires_after: String, val token: String)

@Serializable
data class CreateAccount(
    val cc: String,
    val phone_number: String,
    val method: String,
    val cert: String,
    val pin: String = ""
)

@Serializable
data class GetContacts(
    val contacts: List<String>,
    val blocking: String = "no_wait",
    val force_check: Boolean = false,
)

@Serializable
data class AccountVerification(val code: String)

@Serializable
data class Contact(val input: String, val status: String, val wa_id: String? = null)

@Serializable
data class Identity(val hash: String, val created_timestamp: Long, val acknowledged: Boolean)

@Serializable
data class Media(
    val auto_download: List<String> = listOf(
        "voice",
        "sticker",
        "audio",
        "document",
        "image",
        "video"
    )
)

@Serializable
data class ApplicationSettings(
    val wa_id: String,
    val webhooks: Webhook,
    val callback_backoff_delay_ms: Int = 3000,
    val callback_persist: Boolean = true,
    val db_garbagecollector_enable: Boolean = true,
    val heartbeat_interval: Int = 5,
    val max_callback_backoff_delay_ms: Int = 900000,
    val media: Media = Media(),
    val notify_user_change_number: Boolean = true,
    val pass_through: Boolean = false,
    val sent_status: Boolean = true,
    val show_security_notifications: Boolean = false,
    val skip_referral_media_download: Boolean = false,
    val unhealthy_interval: Int = 30,
)

@Serializable
data class Webhook(val url: String)

@Serializable
//The max length for the string is 139 characters.
data class About(val text: String)

@Serializable
data class BusinessProfile(
    val address: String,
    val description: String,
    val email: String,
    val vertical: String,
    val websites: List<String>
)

@Serializable
data class MessageText(val body: String)

@Serializable
enum class MessageType {
    audio, contact, document, image, location, sticker, template, text, video, interactive
}

// Message ID type alias
typealias MID = String

// WAB ID (phone number) type alias
typealias WABID = String


//TODO implement non-text messages
@Serializable
data class WabMessage(
    val recipient_type: String,
    val to: WABID,
    val type: MessageType,
    val preview_url: String,
    val text: MessageText?
)

@Serializable
data class MessageId(val id: MID)

@Serializable
data class ConversationId(val id: String)

@Serializable
data class WabProfile(val name: String)

@Serializable
data class WabContact(val wa_id: WABID, val profile: WabProfile)

@Serializable
data class MessageCallback(
    val from: WABID,
    val id: MID,
    val text: MessageText,
    val timestamp: String,
    val type: MessageType
)

@Serializable
data class Pricing(val billable: Boolean, val pricing_model: String)

@Serializable
enum class StatusType {
    sent, delivered, read, failed, deleted
}

@Serializable
data class WabStatus(
    val id: MID,
    val recipient_id: WABID,
    val status: StatusType,
    val timestamp: String,
    val conversation: ConversationId? = null,
    val pricing: Pricing? = null
)

@Serializable
data class ErrorMessage(val code: Int, val details: String, val title: String)

@Serializable
data class Callback(
    val messages: List<MessageCallback>? = null,
    val statuses: List<WabStatus>? = null,
    val contacts: List<WabContact>? = null
)
