package com.networkedcapital.rep.presentation.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.BarChartData
import com.networkedcapital.rep.domain.model.User
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.networkedcapital.rep.presentation.goals.GoalsDetailViewModel
import com.networkedcapital.rep.domain.model.FeedItem

@Composable
fun GoalsDetailScreen(
    goalId: Int,
    onBack: () -> Unit,
    onMessage: (User) -> Unit,
    onEditGoal: () -> Unit,
    onUpdateGoal: () -> Unit
) {
    val viewModel: GoalsDetailViewModel = viewModel()
    val goal by viewModel.goal.collectAsState()
    val feed by viewModel.feed.collectAsState()
    val team by viewModel.team.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(goalId) {
        viewModel.loadGoal(goalId)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF8CC55D))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(goal?.title ?: "Goal Detail", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(24.dp)) // Placeholder for right icon
        }
        // Progress Bar and Metrics
        if (goal != null) {
            Column(modifier = Modifier.padding(16.dp)) {
                LinearProgressIndicator(
                    progress = goal!!.progress.toFloat().coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFF8CC55D),
                    trackColor = Color(0xFFE0E0E0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text("Metric: ${goal!!.metricName}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Goal Type: ${goal!!.typeName}", style = MaterialTheme.typography.bodySmall)
                }
                Row {
                    Text("Quota: ${goal!!.quota.toInt()}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Progress: ${goal!!.filledQuota.toInt()}", style = MaterialTheme.typography.bodySmall)
                }
                if (goal!!.subtitle.isNotBlank()) {
                    Text(goal!!.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                if (goal!!.description.isNotBlank()) {
                    Text(goal!!.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
        // Segmented Control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.White, RoundedCornerShape(4.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Feed", "Report", "Team").forEachIndexed { idx, label ->
                TextButton(
                    onClick = { selectedTab = idx },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (selectedTab == idx) Color.White else Color.Black,
                        containerColor = if (selectedTab == idx) Color.Black else Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(label)
                }
            }
        }
        // Content
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error")
            }
        } else {
            when (selectedTab) {
                0 -> {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(feed) { item ->
                            FeedItemView(item)
                        }
                    }
                }
                1 -> {
                    if (error != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Error loading reporting data.", color = Color.Red)
                        }
                    } else if (goal?.chartData?.isEmpty() != false) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No reporting data available.", color = Color.Gray)
                        }
                    } else {
                        LargeBarChartView(goal!!.chartData, goal!!.quota)
                    }
                }
                2 -> {
                    if (team.isNullOrEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("No team members found.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(team) { user ->
                                TeamMemberItem(user, onMessage)
                            }
                        }
                    }
                }
            }
        }
        // Bottom Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onEditGoal, modifier = Modifier.weight(1f)) {
                Text("Edit Goal")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onUpdateGoal, modifier = Modifier.weight(1f)) {
                Text("Update Progress")
            }
        }
    }
}

@Composable
fun FeedItemView(item: FeedItem) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {
        // ...profile image if available...
        Column {
            Text(item.userName, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text(item.date, fontSize = 12.sp, color = Color.Gray)
            Text(item.value, fontSize = 14.sp)
            if (item.note.isNotBlank()) {
                Text("Note: ${item.note}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun TeamMemberItem(user: User, onMessage: (User) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ...profile image if available...
        Text(user.fullName ?: "", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onMessage(user) }) {
            Icon(Icons.Filled.Message, contentDescription = "Message")
        }
    }
}

@Composable
fun LargeBarChartView(data: List<BarChartData>, quota: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { item ->
            val quotaValue = if (quota > 0) quota else 1.0
            val percent = (item.value / quotaValue).coerceIn(0.0, 1.0)
            val barHeight = percent * 200f
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(item.valueLabel, fontSize = 10.sp)
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(barHeight.dp)
                        .background(Color(0xFF8CC55D), RoundedCornerShape(3.dp))
                )
                Text(item.bottomLabel, fontSize = 10.sp)
            }
        }
    }
}

