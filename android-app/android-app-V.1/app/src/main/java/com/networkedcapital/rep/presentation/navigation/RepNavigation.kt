package com.networkedcapital.rep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
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
import com.networkedcapital.rep.presentation.payment.PaymentsScreen
import com.networkedcapital.rep.presentation.payment.PayTransactionScreen
import com.networkedcapital.rep.presentation.payment.PortalPaymentSetupScreen
import com.networkedcapital.rep.presentation.invites.InvitesScreen
import com.networkedcapital.rep.presentation.settings.SettingsScreen
import com.networkedcapital.rep.presentation.portal.EditPortalScreen
import com.networkedcapital.rep.presentation.goals.EditGoalScreen
import com.networkedcapital.rep.presentation.goals.UpdateGoalScreen
import com.networkedcapital.rep.domain.model.TransactionType
import com.networkedcapital.rep.presentation.test.ApiTestScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType

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
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToPortal = { portalId ->
                    navController.navigate(Screen.PortalDetail.createRoute(portalId, "Portal"))
                },
                onNavigateToGoal = { goalId ->
                    navController.navigate("${Screen.GoalDetail.route}/$goalId")
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToMessage = { userId, userName ->
                    // TODO: Wire up to IndividualChatScreen
                }
            )
        }
        
        composable("${Screen.PortalDetail.route}/{portalId}/{portalName}") { backStackEntry ->
            val portalId = backStackEntry.arguments?.getString("portalId")?.toIntOrNull() ?: 0
            val portalName = backStackEntry.arguments?.getString("portalName") ?: "Portal"

            com.networkedcapital.rep.presentation.portal.PortalDetailScreen(
                portalId = portalId,
                portalName = portalName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGoal = { goalId ->
                    navController.navigate("${Screen.GoalDetail.route}/$goalId")
                },
                onNavigateToPayment = { portalId, portalName, goalId, goalName, transactionType ->
                    navController.navigate(
                        Screen.PayTransaction.createRoute(
                            portalId, portalName, goalId, goalName, transactionType
                        )
                    )
                }
            )
        }

        composable(Screen.ApiTest.route) {
            ApiTestScreen(
                authViewModel = authViewModel
            )
        }

        composable("${Screen.GoalDetail.route}/{goalId}") { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")?.toIntOrNull() ?: 0
            // TODO: Replace with your actual GoalDetailScreen implementation
            // For now, show an empty Box to avoid syntax errors
            Box(modifier = Modifier.fillMaxSize())
        }

        // Payments Screen
        composable(Screen.Payments.route) {
            PaymentsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pay Transaction Screen
        composable(
            route = Screen.PayTransaction.route,
            arguments = listOf(
                navArgument("portalId") { type = NavType.IntType },
                navArgument("portalName") { type = NavType.StringType },
                navArgument("goalId") { type = NavType.IntType },
                navArgument("goalName") { type = NavType.StringType },
                navArgument("transactionType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portalId = backStackEntry.arguments?.getInt("portalId") ?: 0
            val portalName = backStackEntry.arguments?.getString("portalName") ?: ""
            val goalId = backStackEntry.arguments?.getInt("goalId") ?: 0
            val goalName = backStackEntry.arguments?.getString("goalName") ?: ""
            val transactionTypeStr = backStackEntry.arguments?.getString("transactionType") ?: "DONATION"
            val transactionType = when (transactionTypeStr.uppercase()) {
                "PAYMENT" -> TransactionType.PAYMENT
                "PURCHASE" -> TransactionType.PURCHASE
                else -> TransactionType.DONATION
            }

            PayTransactionScreen(
                portalId = portalId,
                portalName = portalName,
                goalId = goalId,
                goalName = goalName,
                transactionType = transactionType,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTransactionComplete = {
                    navController.popBackStack()
                }
            )
        }

        // Portal Payment Setup Screen
        composable(
            route = Screen.PortalPaymentSetup.route,
            arguments = listOf(
                navArgument("portalId") { type = NavType.IntType },
                navArgument("portalName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portalId = backStackEntry.arguments?.getInt("portalId") ?: 0
            val portalName = backStackEntry.arguments?.getString("portalName") ?: ""

            PortalPaymentSetupScreen(
                portalId = portalId,
                portalName = portalName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Team Invites Screen
        composable(
            route = Screen.Invites.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            InvitesScreen(
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onViewGoal = { goalId ->
                    navController.navigate("${Screen.GoalDetail.route}/$goalId")
                }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToPayments = {
                    navController.navigate(Screen.Payments.route)
                },
                onNavigateToTerms = {
                    navController.navigate(Screen.Onboarding.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Edit Portal Screen
        composable(
            route = Screen.EditPortal.route,
            arguments = listOf(
                navArgument("portalId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val portalId = backStackEntry.arguments?.getInt("portalId") ?: 0
            // TODO: Load existing portal by portalId
            EditPortalScreen(
                existingPortal = null, // Will be loaded via ViewModel in future
                onSave = { portal ->
                    // TODO: Save portal via API
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Goal Screen
        composable(
            route = Screen.EditGoal.route,
            arguments = listOf(
                navArgument("goalId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt("goalId") ?: 0
            // TODO: Load existing goal by goalId
            EditGoalScreen(
                existingGoal = null, // Will be loaded via ViewModel in future
                onSave = { goal ->
                    // TODO: Save goal via API
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // Update Goal Screen
        composable(
            route = Screen.UpdateGoal.route,
            arguments = listOf(
                navArgument("goalId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt("goalId") ?: 0
            // TODO: Load goal quota and metric by goalId
            UpdateGoalScreen(
                goalId = goalId,
                quota = 100.0, // TODO: Load from API
                metricName = "Units", // TODO: Load from API
                onSubmit = { addedValue, note ->
                    // TODO: Submit progress update via API
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
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
    object PortalDetail : Screen("portal_detail/{portalId}/{portalName}") {
        fun createRoute(portalId: Int, portalName: String) = "portal_detail/$portalId/$portalName"
    }
    object GoalDetail : Screen("goal_detail")
    object ApiTest : Screen("api_test")

    // Payment & Subscription screens
    object Payments : Screen("payments")
    object PayTransaction : Screen("pay_transaction/{portalId}/{portalName}/{goalId}/{goalName}/{transactionType}") {
        fun createRoute(portalId: Int, portalName: String, goalId: Int, goalName: String, transactionType: String) =
            "pay_transaction/$portalId/$portalName/$goalId/$goalName/$transactionType"
    }
    object PortalPaymentSetup : Screen("portal_payment_setup/{portalId}/{portalName}") {
        fun createRoute(portalId: Int, portalName: String) = "portal_payment_setup/$portalId/$portalName"
    }

    // Team Invites screen
    object Invites : Screen("invites/{userId}") {
        fun createRoute(userId: Int) = "invites/$userId"
    }

    // Settings screen
    object Settings : Screen("settings")

    // Edit screens
    object EditPortal : Screen("edit_portal/{portalId}") {
        fun createRoute(portalId: Int) = "edit_portal/$portalId"
    }
    object EditGoal : Screen("edit_goal/{goalId}") {
        fun createRoute(goalId: Int) = "edit_goal/$goalId"
    }
    object UpdateGoal : Screen("update_goal/{goalId}") {
        fun createRoute(goalId: Int) = "update_goal/$goalId"
    }
}
