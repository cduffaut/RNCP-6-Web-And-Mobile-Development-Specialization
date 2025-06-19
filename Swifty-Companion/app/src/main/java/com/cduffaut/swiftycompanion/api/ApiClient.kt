package com.cduffaut.swiftycompanion.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.cduffaut.swiftycompanion.ApiConfig
import com.cduffaut.swiftycompanion.TokenManager
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking

object ApiClient {
    private var token: String? = null
    private lateinit var appContext: Context

    // Initialiser avec le contexte de l'application
    fun init(context: Context) {
        appContext = context.applicationContext
        TokenManager.init(context)
    }

    // m à j le token pour accès
    fun setToken(newToken: String?) {
        token = newToken
    }

    // msg pour absence de connexion internet
    class NoNetworkException(message: String) : IOException(message)

    // msg pour limite de taux de requêtes
    class RateLimitException(message: String) : IOException(message)

    // msg pour erreurs serveur
    class ServerException(message: String) : IOException(message)

    // renouveler automatiquement le token exp
    private val authenticator = object : Authenticator {
        override fun authenticate(route: Route?, response: Response): Request? {
            // ne pas refresh si on n'a pas de refresh token
            val refreshToken = TokenManager.getRefreshToken() ?: return null

            // si rep 401 => essayer de refresh le token
            if (response.code == 401) {
                return runBlocking {
                    try {
                        // Appel synchrone pour rafraîchir le token
                        val newToken = service.refreshToken(
                            grantType = "refresh_token",
                            clientId = ApiConfig.CLIENT_ID,
                            clientSecret = ApiConfig.CLIENT_SECRET,
                            refreshToken = refreshToken
                        )
                        TokenManager.saveToken(newToken)

                        // re-creer une nouv requete avec le nouv token
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer ${TokenManager.getAccessToken()}")
                            .build()
                    } catch (e: Exception) {
                        // si echec => effacer token
                        TokenManager.clearToken()
                        null
                    }
                }
            }
            return null
        }
    }

    // interceptor pour check la connexion internet
    private val networkInterceptor = Interceptor { chain ->
        if (!isNetworkAvailable(appContext)) {
            throw NoNetworkException("Pas de connexion internet disponible")
        }
        chain.proceed(chain.request())
    }

    // interceptor pour l'authentification
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = this.token

        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        chain.proceed(request)
    }

    // interceptor pour gerer la norme des err de l'API
    private val errorInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())

        when (response.code) {
            429 -> throw RateLimitException("Trop de requêtes, veuillez réessayer plus tard")
            500, 502, 503, 504 -> throw ServerException("Erreur serveur, veuillez réessayer plus tard")
        }

        response
    }

    // check la dispo du réseau
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // modele de struct init tardivement => quand il a besoin d'ê utilisé
    // compromis entre perfo et simplicité
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(networkInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val service: ApiService by lazy { retrofit.create(ApiService::class.java) }
}