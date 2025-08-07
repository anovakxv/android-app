package com.networkedcapital.rep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.networkedcapital.rep.presentation.auth.AuthViewModel
import com.networkedcapital.rep.presentation.auth.LoginScreen
import com.networkedcapital.rep.presentation.auth.RegisterScreen
import com.networkedcapital.rep.presentation.auth.OnboardingScreen
import com.networkedcapital.rep.presentation.main.MainScreen
import com.networkedcapital.rep.presentation.profile.ProfileScreen

import com.networkedcapital.rep.presentation.test.ApiTestScreen

@Composable
fun RepNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = when {
            !authState.isRegistered -> Screen.Register.route
            !authState.onboardingComplete -> Screen.Onboarding.route
            authState.jwtToken.isNotEmpty() && authState.userId > 0 -> Screen.Main.route
            else -> Screen.Login.route
        },
        modifier = modifier
    ) {
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegistrationSuccess = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToApiTest = {
                    navController.navigate(Screen.ApiTest.route)
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToProfile = { userId ->
                    navController.navigate("${Screen.Profile.route}/$userId")
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable("${Screen.Profile.route}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            ProfileScreen(
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ApiTest.route) {
            ApiTestScreen(
                authViewModel = authViewModel
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object Login : Screen("login")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object Profile : Screen("profile")
    object ApiTest : Screen("api_test")
}
