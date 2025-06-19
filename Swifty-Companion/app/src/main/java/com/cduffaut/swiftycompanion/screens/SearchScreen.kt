package com.cduffaut.swiftycompanion.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cduffaut.swiftycompanion.ApiConfig
import com.cduffaut.swiftycompanion.TokenManager
import com.cduffaut.swiftycompanion.api.ApiClient
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(navController: NavHostController, intentUri: Uri? = null) {
    // cree interfqce utilisateur
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isAuthenticated by remember { mutableStateOf(false) }

    LaunchedEffect(intentUri) {
        TokenManager.init(context)

        // check si on a deja un token et valide
        if (TokenManager.hasToken() && !TokenManager.isTokenExpired()) {
            ApiClient.setToken(TokenManager.getAccessToken())
            isAuthenticated = true
        } else if (TokenManager.hasToken() && TokenManager.isTokenExpired()) {
            // sinon on essaye de le refraichir
            try {
                val refreshToken = TokenManager.getRefreshToken() ?: throw Exception("Refresh token non disponible")
                val newToken = ApiClient.service.refreshToken(
                    clientId = ApiConfig.CLIENT_ID,
                    clientSecret = ApiConfig.CLIENT_SECRET,
                    refreshToken = refreshToken
                )
                TokenManager.saveToken(newToken)
                isAuthenticated = true
            } catch (e: Exception) {
                // si le refresh echoue on efface le token
                TokenManager.clearToken()
                isAuthenticated = false
                errorMessage = "Erreur de rafraîchissement: ${e.message}"
            }
        }

        // check si on vient depuis l'authentification
        // si code edxiste: on echange contre un token
        intentUri?.getQueryParameter("code")?.let { code ->
            try {
                isLoading = true
                // appel à l'api
                val token = ApiClient.service.getToken(
                    grantType = "authorization_code",
                    clientId = ApiConfig.CLIENT_ID,
                    clientSecret = ApiConfig.CLIENT_SECRET,
                    code = code,
                    redirectUri = ApiConfig.REDIRECT_URI
                )
                TokenManager.saveToken(token)
                isAuthenticated = true
            } catch (e: Exception) {
                errorMessage = "Échec de l'authentification: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Swifty Companion",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (!isAuthenticated) {
            Button(
                onClick = {
                    val authUrl = "${ApiConfig.AUTH_URL}?client_id=${ApiConfig.CLIENT_ID}" +
                            "&redirect_uri=${ApiConfig.REDIRECT_URI}" +
                            "&response_type=code" +
                            "&scope=${ApiConfig.SCOPE}"

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
                    context.startActivity(intent)
                }
            ) {
                Text("Se connecter avec l'API 42")
            }
        } else {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Login 42") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    if (searchQuery.isBlank()) {
                        errorMessage = "Veuillez entrer un login"
                    } else {
                        errorMessage = null
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val user = ApiClient.service.getUser(searchQuery)
                                navController.navigate(Screen.Profile.createRoute(searchQuery))
                            } catch (e: ApiClient.NoNetworkException) {
                                errorMessage = "Pas de connexion internet. Veuillez vérifier votre connexion."
                            } catch (e: ApiClient.RateLimitException) {
                                errorMessage = "Trop de requêtes. Veuillez réessayer dans quelques instants."
                            } catch (e: ApiClient.ServerException) {
                                errorMessage = "Erreur serveur. Veuillez réessayer plus tard."
                            } catch (e: Exception) {
                                errorMessage = if (e.message?.contains("HTTP 404") == true) {
                                    "Utilisateur non trouvé."
                                } else {
                                    "Erreur: ${e.message}"
                                }
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading && searchQuery.isNotBlank(),
                modifier = Modifier.fillMaxWidth() // smart screen size adaptation instead of pixel value
            ) {
                Text("Rechercher")
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}