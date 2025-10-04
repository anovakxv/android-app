package com.networkedcapital.rep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
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
                    navController.navigate(Screen.EditProfile.route) {
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
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onProfileSaved = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.EditProfile.route) { inclusive = true }
                    }
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
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.AboutRep.route) { inclusive = true }
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
                        val userId = authState.userId
                        navController.navigate("${Screen.PortalDetail.route}/$portalId/$userId")
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
        
        composable("${Screen.PortalDetail.route}/{portalId}/{userId}") { backStackEntry ->
            val portalId = backStackEntry.arguments?.getString("portalId")?.toIntOrNull() ?: 0
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            com.networkedcapital.rep.presentation.portal.PortalDetailScreen(
                portalId = portalId,
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGoalDetail = { goalId ->
                    navController.navigate("${Screen.GoalDetail.route}/$goalId")
                },
                onNavigateToEditPortal = { /* TODO */ },
                onNavigateToChat = { _, _, _ -> /* TODO */ },
                onNavigateToEditGoal = { _, _ -> /* TODO */ }
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
    object PortalDetail : Screen("portal_detail")
    object GoalDetail : Screen("goal_detail")
    object ApiTest : Screen("api_test")
        composable("${Screen.GoalDetail.route}/{goalId}") { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")?.toIntOrNull() ?: 0
            // TODO: Replace with your actual GoalDetailScreen implementation
            // Example:
            // com.networkedcapital.rep.presentation.goals.GoalsDetailScreen(
            //     goalId = goalId,
            //     ...
            // )
        }
}
