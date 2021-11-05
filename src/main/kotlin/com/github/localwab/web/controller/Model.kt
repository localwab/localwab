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

import com.github.localwab.web.domain.ApplicationSettings
import com.github.localwab.web.domain.AuthToken
import com.github.localwab.web.domain.Contact
import com.github.localwab.web.domain.ErrorMessage
import com.github.localwab.web.domain.Identity
import com.github.localwab.web.domain.MessageId
import com.github.localwab.web.domain.Meta
import kotlinx.serialization.Serializable

// -------------- Responses -------------------------------
interface WabResponse

@Serializable
data class ErrorResponse(val errors: List<ErrorMessage>, val meta: Meta = Meta()) : WabResponse

@Serializable
data class SuccessLoginResponse(val users: List<AuthToken>, val meta: Meta = Meta()) : WabResponse

@Serializable
data class LogoutResponse(val meta: Meta = Meta()) : WabResponse

@Serializable
data class AccountResponse(val account: List<Map<String, String>>) : WabResponse

@Serializable
data class ContactsResponse(val contacts: List<Contact>) : WabResponse

@Serializable
data class IdentityResponse(val contacts: Identity) : WabResponse

@Serializable
data class ApplicationSettingsResponse(val settings: ApplicationSettings, val meta: Meta = Meta()) :
    WabResponse

@Serializable
data class TextResponse(val body: String) : WabResponse

@Serializable
data class MessagesResponse(val messages: List<MessageId>, val meta: Meta = Meta()) : WabResponse

