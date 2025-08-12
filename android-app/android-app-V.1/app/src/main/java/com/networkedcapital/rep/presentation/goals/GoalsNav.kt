package com.networkedcapital.rep.presentation.goals

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun GoalsNavHost(
    navController: NavController = rememberNavController(),
    goalsViewModel: GoalsViewModel = hiltViewModel()
) {
    NavHost(navController = navController, startDestination = "goalsList") {
        composable("goalsList") {
            GoalsListScreen(
                viewModel = goalsViewModel,
                onGoalClick = { goal ->
                    goalsViewModel.selectGoal(goal)
                    navController.navigate("goalDetail")
                }
            )
        }
        composable("goalDetail") {
            val uiState = goalsViewModel.uiState.collectAsState().value
            uiState.selectedGoal?.let { goal ->
                GoalsDetailScreen(
                    goal = goal,
                    feed = uiState.feed,
                    team = uiState.team,
                    onBack = { navController.popBackStack() },
                    onMessage = { /* TODO: Implement message navigation */ },
                    onEditGoal = { /* TODO: Implement edit navigation */ },
                    onUpdateGoal = { /* TODO: Implement update navigation */ }
                )
            }
        }
    }
}
