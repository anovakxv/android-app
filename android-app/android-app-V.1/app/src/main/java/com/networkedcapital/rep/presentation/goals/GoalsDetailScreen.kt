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
import coil.compose.AsyncImage
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.BarChartData
import com.networkedcapital.rep.domain.model.User
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.networkedcapital.rep.presentation.goals.GoalsDetailViewModel
import com.networkedcapital.rep.domain.model.FeedItem
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri

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
    var selectedProfileUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(goalId) {
        viewModel.loadGoal(goalId)
    }

    // State for modal bottom sheet
    val showActionSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }
    var showSupportSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // Top Bar (iOS style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF8CC55D)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = goal?.title ?: "Goal Detail",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(24.dp)) // Placeholder for right icon
            }
            Divider(
                color = Color(0xFFE4E4E4),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        // Progress Bar and Richer Goal Metadata
        if (goal != null) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Title and Portal Name
                Text(goal!!.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                if (!goal!!.portalName.isNullOrBlank()) {
                    Text(goal!!.portalName ?: "", fontSize = 14.sp, color = Color(0xFF8CC55D), fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
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
                    Text("Quota: ${goal!!.quotaString}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Progress: ${goal!!.valueString}", style = MaterialTheme.typography.bodySmall)
                }
                if (!goal!!.reportingName.isNullOrBlank()) {
                    Text("Reporting: ${goal!!.reportingName}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
                if (goal!!.subtitle.isNotBlank()) {
                    Text(goal!!.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                if (goal!!.description.isNotBlank()) {
                    Text(goal!!.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
        // Black & White Segmented Control (PortalPage style)
        val segmentLabels = listOf("Feed", "Report", "Team")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                    .height(40.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                segmentLabels.forEachIndexed { idx, label ->
                    val isSelected = selectedTab == idx
                    val bgColor = if (isSelected) Color.Black else Color.White
                    val textColor = if (isSelected) Color.White else Color.Black
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .clickable { selectedTab = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
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
                    if (feed.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Message, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No feed activity yet.", color = Color.Gray, fontSize = 16.sp)
                                Text("Updates and progress will appear here.", color = Color.LightGray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(feed) { item ->
                                FeedItemView(item, onProfileClick = { user -> selectedProfileUser = user })
                            }
                        }
                    }
                }
                1 -> {
                    if (error != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Message, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Oops! Couldn't load reporting data.", color = Color.Red, fontSize = 16.sp)
                                Text(error ?: "Unknown error.", color = Color.LightGray, fontSize = 13.sp)
                            }
                        }
                    } else if (goal?.chartData?.isEmpty() != false) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Message, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No reporting data available.", color = Color.Gray, fontSize = 16.sp)
                                Text("Charts and stats will appear here.", color = Color.LightGray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        LargeBarChartView(goal!!.chartData, goal!!.quota)
                    }
                }
                2 -> {
                    if (team.isNullOrEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Message, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No team members yet.", color = Color.Gray, fontSize = 16.sp)
                                Text("Invite teammates to join this goal.", color = Color.LightGray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(team) { user ->
                                TeamMemberItem(user, onMessage, onProfileClick = { selectedProfileUser = user })
                            }
                        }
                    }
                }
            }
        }
            // Bottom Bar (iOS parity: Action + Message)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showSheet = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8CC55D), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Actions", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { showChatSheet = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Message, contentDescription = "Message Team", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Message", fontSize = 18.sp)
                }
            }
        }

        // Floating Support FAB (Fund/Sales only)
        if (goal?.typeName == "Fund" || goal?.typeName == "Sales") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { showSupportSheet = true },
                    containerColor = Color(0xFFFCB900),
                    contentColor = Color.Black
                ) {
                    Text("Support", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Modal Bottom Sheet for actions
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                ) {
                    Text("Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    Divider()
                    SheetActionButton("Edit Goal") {
                        showSheet = false
                        onEditGoal()
                    }
                    SheetActionButton("Update Progress") {
                        showSheet = false
                        onUpdateGoal()
                    }
                    SheetActionButton("Invite Team") {
                        showSheet = false
                        /* TODO: Implement Invite Team action */
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }

        // Placeholder for Team Chat modal
        if (showChatSheet) {
            AlertDialog(
                onDismissRequest = { showChatSheet = false },
                title = { Text("Team Chat") },
                text = { Text("Team chat modal will be implemented here.") },
                confirmButton = {
                    TextButton(onClick = { showChatSheet = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Placeholder for Support/payment modal
        if (showSupportSheet) {
            AlertDialog(
                onDismissRequest = { showSupportSheet = false },
                title = { Text("Support Goal") },
                text = { Text("Payment processing modal will be implemented here.") },
                confirmButton = {
                    TextButton(onClick = { showSupportSheet = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Profile dialog placeholder (must be inside composable)
        if (selectedProfileUser != null) {
            AlertDialog(
                onDismissRequest = { selectedProfileUser = null },
                title = { Text("User Profile") },
                text = {
                    Column {
                        Text("ID: ${selectedProfileUser!!.id}")
                        Text("Username: ${selectedProfileUser!!.username ?: "(none)"}")
                        Text("Full Name: ${selectedProfileUser!!.fullName ?: selectedProfileUser!!.fname ?: "(none)"}")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedProfileUser = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }

@Composable
private fun SheetActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8CC55D),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}

@Composable
fun FeedItemView(item: FeedItem, onProfileClick: (User) -> Unit) {
    val user = User(
        id = item.userId ?: 0,
        username = item.userName,
        fullName = item.userName,
        fname = null,
        profilePictureUrl = item.profilePictureUrl
    )
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Profile image with Coil, fallback to initials or icon on error
        Box(modifier = Modifier.size(40.dp)) {
            if (!user.profilePictureUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE0E0E0))
                        .clickable { onProfileClick(user) },
                    onError = { _ -> },
                    fallback = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                                .clickable { onProfileClick(user) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.userName.take(1), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                        .clickable { onProfileClick(user) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.userName.take(1), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f).clickable { onProfileClick(user) }
        ) {
            Text(item.userName, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text(item.date, fontSize = 12.sp, color = Color.Gray)
            Text(item.value, fontSize = 14.sp)
            if (item.note.isNotBlank()) {
                Text("Note: ${item.note}", fontSize = 12.sp, color = Color.Gray)
            }
            // Attachments section
            if (!item.attachments.isNullOrEmpty()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    item.attachments.forEach { attachment ->
                        if (attachment.isImage == true && !attachment.url.isNullOrBlank()) {
                            // Image attachment
                            AsyncImage(
                                model = attachment.url,
                                contentDescription = attachment.fileName ?: "Attachment image",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE0E0E0))
                                    .padding(bottom = 4.dp),
                                onError = { _ ->
                                    // Show fallback icon if image fails
                                    Image(
                                        painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                                        contentDescription = "Image error",
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            )
                        } else if (!attachment.url.isNullOrBlank()) {
                            // Document or non-image attachment
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        // Open document in browser or viewer
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(attachment.url))
                                        context.startActivity(intent)
                                    }
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_save),
                                    contentDescription = "Document",
                                    tint = Color(0xFF4A90E2),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(attachment.fileName ?: "Document", color = Color(0xFF4A90E2), fontSize = 13.sp)
                            }
                        }
                        if (!attachment.note.isNullOrBlank()) {
                            Text("Attachment note: ${attachment.note}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamMemberItem(user: User, onMessage: (User) -> Unit, onProfileClick: (User) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image with Coil, fallback to initials or icon on error
        Box(modifier = Modifier.size(40.dp)) {
            if (!user.profilePictureUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE0E0E0))
                        .clickable { onProfileClick(user) },
                    onError = { _ -> },
                    fallback = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                                .clickable { onProfileClick(user) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text((user.fullName ?: user.username ?: "").take(1), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                        .clickable { onProfileClick(user) },
                    contentAlignment = Alignment.Center
                ) {
                    Text((user.fullName ?: user.username ?: "").take(1), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.clickable { onProfileClick(user) }
        ) {
            Text("ID: ${user.id}", fontSize = 12.sp, color = Color.Gray)
            Text("Username: ${user.username ?: "(none)"}", fontSize = 12.sp, color = Color.Gray)
            Text("Full Name: ${user.fullName ?: user.fname ?: "(none)"}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onMessage(user) }) {
            Icon(Icons.Filled.Message, contentDescription = "Message")
        }
    }
}
    // Profile dialog placeholder
    if (selectedProfileUser != null) {
        AlertDialog(
            onDismissRequest = { selectedProfileUser = null },
            title = { Text("User Profile") },
            text = {
                Column {
                    Text("ID: ${selectedProfileUser!!.id}")
                    Text("Username: ${selectedProfileUser!!.username ?: "(none)"}")
                    Text("Full Name: ${selectedProfileUser!!.fullName ?: selectedProfileUser!!.fname ?: "(none)"}")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedProfileUser = null }) {
                    Text("Close")
                }
            }
        )
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
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                // Value label above bar
                Text(item.valueLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(barHeight.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color(0xFF8CC55D), Color(0xFFB6E388))
                            ),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
                        )
                )
                // Bottom label
                Text(item.bottomLabel, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

