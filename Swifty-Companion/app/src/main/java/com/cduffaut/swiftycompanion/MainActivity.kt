package com.cduffaut.swiftycompanion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.cduffaut.swiftycompanion.screens.AppNavigation
import com.cduffaut.swiftycompanion.ui.theme.SwiftyCompanionTheme
import com.cduffaut.swiftycompanion.api.ApiClient

class MainActivity : ComponentActivity() {
    // addr pour acces API
    val intentUri = mutableStateOf<Uri?>(null)

    // point d'entrée
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // prep connex avec API
        ApiClient.init(applicationContext)

        // si non null on assigne la val à la var
        intent?.data?.let { uri ->
            intentUri.value = uri
        }

        // construit l'app et ce qui sera afficher
        setContent {
            SwiftyCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(intentUri.value)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // si le user clique sur le bouton de recherche
        intent.data?.let { uri ->
            intentUri.value = uri
            // refresh l'affichage avec les nouvelles datas
            setContent {
                SwiftyCompanionTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(intentUri.value)
                    }
                }
            }
        }
    }
}