package com.misw.abcalls.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.misw.abcalls.ui.screens.CreateIncidentScreen
import com.misw.abcalls.ui.screens.IncidentListScreen
import com.misw.abcalls.ui.screens.LoginScreen
import com.misw.abcalls.ui.screens.UserRegistrationScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        // Auth navigation graph
        navigation(
            startDestination = "login",
            route = "auth"
        ) {
            composable("login") {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate("register") {
                            popUpTo("login")
                        }
                    },
                    onLoginSuccess = {
                        navController.navigate("main") {
                            popUpTo("auth") {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable("register") {
                UserRegistrationScreen(
                    onNavigateToLogin = {
                        navController.navigate("main") {
                            popUpTo("auth") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        navigation(
            startDestination = "incidentList",
            route = "main"
        ) {
            composable("incidentList") {
                IncidentListScreen(
                    onCreateIncident = {
                        navController.navigate("createIncident")
                    }
                )
            }

            composable("createIncident") {
                CreateIncidentScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onIncidentCreated = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}