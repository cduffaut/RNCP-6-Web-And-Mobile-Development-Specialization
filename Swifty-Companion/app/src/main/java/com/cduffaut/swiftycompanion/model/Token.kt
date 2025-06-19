package com.cduffaut.swiftycompanion.model

import com.google.gson.annotations.SerializedName

// classe pour stocker toutes les infos de notre token
data class Token(
    @SerializedName("access_token") val accessToken: String,
    // token_type useless, to supp ?
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String,
    val scope: String,
    @SerializedName("created_at") val createdAt: Long
)