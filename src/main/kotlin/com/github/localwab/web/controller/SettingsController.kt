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

import com.github.localwab.web.domain.About
import com.github.localwab.web.domain.ApplicationSettings
import com.github.localwab.web.domain.BusinessProfile
import com.github.localwab.web.registry.SettingsRegistry
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/v1/settings")
class SettingsController(val settingsRegistry: SettingsRegistry) {
    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "Show settings",
        description = "Setting are mostly ignored because they not used",
        externalDocs = ExternalDocumentation(
            description = "Retrieve the application settings of the WhatsApp Business API client.",
            url = "https://developers.facebook.com/docs/whatsapp/api/settings/app"
        ),
        tags = ["WhatsApp API"]
    )
    @GetMapping("/application")
    suspend fun getApplication(): ResponseEntity<WabResponse> {
        val result = ApplicationSettingsResponse(settingsRegistry.getApplicationSettings())
        return ResponseEntity(result, HttpStatus.OK)
    }

    @Operation(
        summary = "Reset the application settings to their default values.",
        description = "Setting are mostly ignored because they not used",
        externalDocs = ExternalDocumentation(
            description = "Reset the application settings to their default values.",
            url = "https://developers.facebook.com/docs/whatsapp/api/settings/app"
        ),
        tags = ["WhatsApp API"]
    )
    @DeleteMapping("/application")
    suspend fun resetApplication(): ResponseEntity<WabResponse> {
        val result = ApplicationSettingsResponse(settingsRegistry.resetApplicationSettings())
        return ResponseEntity(result, HttpStatus.OK)
    }

    @Operation(
        summary = "Update the application settings (currently only webhook URL)",
        description = "Setting are mostly ignored because they not used",
        externalDocs = ExternalDocumentation(
            description = "Patch the application settings",
            url = "https://developers.facebook.com/docs/whatsapp/api/settings/app"
        ),
        tags = ["WhatsApp API"]
    )
    @PatchMapping("/application")
    suspend fun updateApplication(@RequestBody body: ApplicationSettings): ResponseEntity<WabResponse> {
        settingsRegistry.setApplicationSettings(body)
        val result = ApplicationSettingsResponse(body)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // About

    @Operation(
        summary = "Show profile's About content.",
        description = "You must use the admin account to access the profile settings.",
        externalDocs = ExternalDocumentation(
            description = "Use this edge to manage your profile's About section.",
            url = "https://developers.facebook.com/docs/whatsapp/api/settings/profile/about"
        ),
        tags = ["WhatsApp API"]
    )
    @GetMapping("/profile/about")
    suspend fun getProfileAbout(): ResponseEntity<WabResponse> {
        val about = settingsRegistry.getProfileText()
        val jsonElement = buildJsonObject {
            putJsonObject("settings") {
                putJsonObject("profile") {
                    putJsonObject("about") {
                        put("text", about.text)
                    }
                }
            }
        }
        val jsonText = jsonElement.toString()
        val result = TextResponse(jsonText)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @Operation(
        summary = "Update profile's About content.",
        description = "You must use the admin account to access the profile settings.",
        externalDocs = ExternalDocumentation(
            description = "Use to update your profile's About section.",
            url = "https://developers.facebook.com/docs/whatsapp/api/settings/profile/about"
        ),
        tags = ["WhatsApp API"]
    )
    @PatchMapping("/profile/about")
    suspend fun patchProfileAbout(@RequestBody about: About): ResponseEntity<WabResponse> {
        settingsRegistry.setProfileText(about)
        val result = TextResponse(about.text)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Photo

    @Operation(
        summary = "Show profile's photo as binary or link",
        description = "You must use the admin account to access the profile settings.",
        externalDocs = ExternalDocumentation(
            description = "Use this endpoint to manage your profile photo.",
            url = "https://developers.facebook.com/docs/whatsapp/api/settings/profile/photo"
        ),
        tags = ["WhatsApp API"]
    )
    @GetMapping("/profile/photo")
    suspend fun getProfilePhoto(@RequestParam(required = false) format: String?): ResponseEntity<ByteArray> {
        if (format == "link") {
            val jsonElement = buildJsonObject {
                putJsonObject("settings") {
                    putJsonObject("profile") {
                        putJsonObject("photo") {
                            put("link", "http://localhost:9999/todo") //TODO provide photo URL
                        }
                    }
                }
            }
            val jsonText = jsonElement.toString()
            val result = jsonText.toByteArray()
            val responseHeaders = HttpHeaders()
            responseHeaders.contentType = MediaType.APPLICATION_JSON
            return ResponseEntity(result, HttpStatus.OK)
        } else {
            val responseHeaders = HttpHeaders()
            responseHeaders.contentType = MediaType.IMAGE_JPEG
            responseHeaders.contentLength = settingsRegistry.getPhoto().size.toLong()
            return ResponseEntity(settingsRegistry.getPhoto(), responseHeaders, HttpStatus.OK)
        }
    }

    // business

    @Operation(
        summary = "Show Business Profile Settings",
        description = "You must use the admin account to access the business profile settings.",
        externalDocs = ExternalDocumentation(
            description = "Endpoint to configure the following business profile settings: business address, business description, email for business contact, business industry, and business website.",
            url = "https://developers.facebook.com/docs/whatsapp/api/settings/business-profile"
        ),
        tags = ["WhatsApp API"]
    )
    @GetMapping("/business/profile")
    suspend fun getBusinessProfile(): ResponseEntity<WabResponse> {
        val business = settingsRegistry.getBusinessProfile()
        val jsonElement = buildJsonObject {
            putJsonObject("settings") {
                putJsonObject("business") {
                    put("profile", Json.encodeToJsonElement(BusinessProfile.serializer(), business))
                }
            }
        }
        val jsonText = jsonElement.toString()
        val result = TextResponse(jsonText)
        return ResponseEntity(result, HttpStatus.OK)
    }
}