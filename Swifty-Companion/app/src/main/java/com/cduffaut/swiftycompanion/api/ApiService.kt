package com.cduffaut.swiftycompanion.api

import com.cduffaut.swiftycompanion.model.Token
import com.cduffaut.swiftycompanion.model.User
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken( // reclamer le token d'accès
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Token

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun refreshToken( // obtenir un nouveau token d'accès
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String
    ): Token

    @GET("v2/users/{login}")
    suspend fun getUser( // recup info student 42
        @Path("login") login: String
    ): User
}