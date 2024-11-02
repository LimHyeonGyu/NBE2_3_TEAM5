package edu.example.dev_3_5_cc.entity

import jakarta.persistence.Entity

data class OAuthToken(
    val access_token: String? = null,
    val token_type: String? = null,
    val refresh_token: String? = null,
    val expires_in: Int? = null,
    val scope: String? = null,
    val refresh_token_expires_in: Int? = null,
) {
}