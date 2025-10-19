package com.networkedcapital.rep.presentation.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.TransactionType
import com.networkedcapital.rep.presentation.theme.RepGreen
import com.networkedcapital.rep.presentation.theme.RepGreenDark

/**
 * Portal Detail Screen - Simplified MVP version
 * Shows portal info, goals, and support button
 *
 * TODO for full iOS parity:
 * - Add 2-tab layout (Goal Teams / Story)
 * - Add image carousel with zoom
 * - Add action menu (Add Goal, Edit, Flag)
 * - Add leads display
 * - Add story/about section
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalDetailScreen(
    portalId: Int,
    portalName: String,
    onNavigateBack: () -> Unit,
    onNavigateToGoal: (Int) -> Unit,
    onNavigateToPayment: (Int, String, Int, String, String) -> Unit,
    viewModel: PortalDetailViewModel = hiltViewModel()
) {
    val portalState by viewModel.portalState.collectAsState()

    LaunchedEffect(portalId) {
        viewModel.loadPortalDetails(portalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(portalState.portal?.name ?: portalName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = RepGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                portalState.isLoading && portalState.portal == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                portalState.error != null -> {
                    Text(
                        text = portalState.error ?: "Unknown error",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                portalState.portal != null -> {
                    PortalContent(
                        portal = portalState.portal!!,
                        goals = portalState.goals,
                        supportGoal = portalState.supportGoal,
                        onNavigateToGoal = onNavigateToGoal,
                        onNavigateToPayment = onNavigateToPayment
                    )
                }
            }
        }
    }
}

@Composable
private fun PortalContent(
    portal: com.networkedcapital.rep.domain.model.Portal,
    goals: List<Goal>,
    supportGoal: Goal?,
    onNavigateToGoal: (Int) -> Unit,
    onNavigateToPayment: (Int, String, Int, String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Portal Image (if available)
        item {
            portal.mainImageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Portal Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.Gray.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Portal Info Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Portal Name
                Text(
                    text = portal.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Subtitle
                portal.subtitle?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                // About/Description
                portal.about?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                // Support Button (if supportable goal exists)
                supportGoal?.let { goal ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val transactionType = if (goal.typeName == "Fund") "DONATION" else "PAYMENT"
                            onNavigateToPayment(
                                portal.id,
                                portal.name,
                                goal.id,
                                goal.title,
                                transactionType
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RepGreenDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Support", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Goals Section Header
        item {
            Text(
                text = "Goal Teams",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Goals List
        if (goals.isEmpty()) {
            item {
                Text(
                    text = "No goals yet",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        } else {
            items(goals) { goal ->
                GoalListItemSimple(
                    goal = goal,
                    onClick = { onNavigateToGoal(goal.id) }
                )
                Divider()
            }
        }
    }
}

@Composable
private fun GoalListItemSimple(
    goal: Goal,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = goal.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            goal.subtitle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Progress indicator
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = goal.progress.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = RepGreen,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}
