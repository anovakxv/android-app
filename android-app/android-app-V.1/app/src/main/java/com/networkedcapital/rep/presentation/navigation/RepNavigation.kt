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
import com.networkedcapital.rep.presentation.onboarding.TermsOfUseScreen
import com.networkedcapital.rep.presentation.onboarding.AboutRepScreen
import com.networkedcapital.rep.presentation.onboarding.EditProfileScreen
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
            authState.isLoggedIn && authState.userId > 0 -> Screen.Main.route
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
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    // You can navigate to a forgot password screen if you have one, or just pop for now
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
                TermsOfUseScreen(
                    onAccept = {
                        navController.navigate(Screen.AboutRep.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.AboutRep.route) {
                AboutRepScreen(
                    onContinue = {
                        navController.navigate(Screen.EditProfile.route) {
                            popUpTo(Screen.AboutRep.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    onProfileSaved = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.EditProfile.route) { inclusive = true }
                        }
                    }
                )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToProfile = { userId ->
                    navController.navigate("${Screen.Profile.route}/$userId")
                },
                onNavigateToPortalDetail = { portalId ->
                    // TODO: Implement navigation to portal detail screen
                },
                onNavigateToPersonDetail = { personId ->
                    // TODO: Implement navigation to person detail screen
                },
                onNavigateToChat = { chatId ->
                    // TODO: Implement navigation to chat screen
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
    object AboutRep : Screen("about_rep")
    object EditProfile : Screen("edit_profile")
    object Main : Screen("main")
    object Profile : Screen("profile")
    object ApiTest : Screen("api_test")
}
