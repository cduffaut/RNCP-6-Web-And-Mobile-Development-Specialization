package com.cduffaut.swiftycompanion.screens

import androidx.compose.runtime.Composable
import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// pour gerer transition entre les 2 ecrans
// def les deux routes possibles pour l'app
sealed class Screen(val route: String) {
    object Search : Screen("search")
    object Profile : Screen("profile/{login}") {
        fun createRoute(login: String) = "profile/$login"
    }
}

@Composable
fun AppNavigation(intentUri: Uri? = null) {
    // creation de la struct de navigation de l'app
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            SearchScreen(navController = navController, intentUri = intentUri)
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("login") { type = NavType.StringType })
        ) { backStackEntry ->
            val login = backStackEntry.arguments?.getString("login") ?: ""
            ProfileScreen(login = login, navController = navController)
        }
    }
}