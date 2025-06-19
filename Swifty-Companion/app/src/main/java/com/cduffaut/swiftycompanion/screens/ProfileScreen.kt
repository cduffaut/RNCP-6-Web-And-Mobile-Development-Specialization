package com.cduffaut.swiftycompanion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cduffaut.swiftycompanion.api.ApiClient
import com.cduffaut.swiftycompanion.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(login: String, navController: NavHostController) {
    // affiche infos detaillees stud 42
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // recup info user
    LaunchedEffect(login) {
        coroutineScope.launch {
            try {
                isLoading = true
                // appel API
                user = ApiClient.service.getUser(login)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Une erreur s'est produite"
            } finally {
                isLoading = false
            }
        }
    }

    // affiche login strud + bouton retour topbar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(login) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Une erreur s'est produite",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                user?.let { userData ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            // en tete + pp
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(userData.image?.link ?: "")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Photo de profil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = userData.login,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = userData.email,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Niveau: ${String.format("%.2f", userData.getLevel())}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            // details stud
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Informations",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Divider()

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Wallet")
                                        Text("${userData.wallet}₳")
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Points de correction")
                                        Text("${userData.correctionPoint}")
                                    }

                                    userData.location?.let {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Location")
                                            Text(it)
                                        }
                                    }

                                    userData.phone?.let {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Téléphone")
                                            Text(it)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // competences
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Compétences",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Divider()

                                    Spacer(modifier = Modifier.height(8.dp))

                                    userData.getSkills().let { skills ->
                                        if (skills.isNotEmpty()) {
                                            skills.forEach { skill ->
                                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(skill.name)
                                                        Text("Niveau: ${String.format("%.1f", skill.level)}")
                                                    }

                                                    LinearProgressIndicator(
                                                        progress = (skill.level / 20).toFloat(),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(top = 4.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        } else {
                                            Text("Aucune compétence trouvée", modifier = Modifier.padding(vertical = 8.dp))
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // projets
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Projets",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Divider()
                                }
                            }
                        }

                        userData.getProjects().let { projects ->
                            if (projects.isNotEmpty()) {
                                items(projects) { projectUser ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = projectUser.project.name,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = "Status: ${projectUser.status}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            projectUser.finalMark?.let {
                                                val color = when {
                                                    it >= 80 -> MaterialTheme.colorScheme.primary
                                                    it >= 60 -> MaterialTheme.colorScheme.secondary
                                                    it < 60 -> MaterialTheme.colorScheme.error
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }

                                                Text(
                                                    text = "$it/100",
                                                    color = color,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                item {
                                    Text("Aucun projet trouvé", modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}