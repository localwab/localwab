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

import com.github.localwab.web.domain.About
import com.github.localwab.web.domain.ApplicationSettings
import com.github.localwab.web.domain.BusinessProfile
import com.github.localwab.web.domain.CreateAccount
import com.github.localwab.web.domain.Webhook
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SettingsRegistry(@Value("\${mock.account-verification-code}") val accountVerificationCode: String) {
    private var waId: String = ""
    private var webhookUrl: String = ""
    private var account: CreateAccount? = null
    private var accountIsVerified: Boolean = false
    private var applicationSettings: ApplicationSettings = createApplicationSettings()
    private var profileText: About = About("")
    private var photo: ByteArray = ByteArray(0)
    private var businessProfile = BusinessProfile(
        "Address",
        "Description",
        "me@here.com",
        "Telecom",
        listOf("http://www.here.work")
    )

    fun getAccount(): CreateAccount? = account
    fun setAccount(accountToRegister: CreateAccount) {
        if (this.account != null) {
            throw UnsupportedOperationException("TODO: ${this.account}")
        } else {
            this.account = accountToRegister
            accountIsVerified = false
        }
    }

    //TODO set wa|_id
    fun getWaId(): String = waId

    //TODO set webhook
    fun getWebhookUrl(): String = webhookUrl

    private fun createApplicationSettings() =
        ApplicationSettings(wa_id = getWaId(), webhooks = Webhook(getWebhookUrl()))

    fun getApplicationSettings() = applicationSettings
    fun resetApplicationSettings(): ApplicationSettings {
        applicationSettings = createApplicationSettings()
        return applicationSettings
    }

    fun setApplicationSettings(settings: ApplicationSettings) {
        applicationSettings = settings
    }

    fun verifyAccount(verification: String): Boolean {
        return if ((verification == accountVerificationCode) && (getAccount() != null)) {
            accountIsVerified = true
            true
        } else {
            false
        }
    }

    fun getProfileText() = profileText
    fun setProfileText(text: About) {
        profileText = text
    }

    fun getPhoto() = photo
    fun getBusinessProfile() = businessProfile
}