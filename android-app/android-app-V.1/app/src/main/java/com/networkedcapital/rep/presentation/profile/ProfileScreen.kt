package com.networkedcapital.rep.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.networkedcapital.rep.domain.model.User
import com.networkedcapital.rep.domain.model.Portal
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.WriteBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToPortal: (Int) -> Unit = {},
    onNavigateToGoal: (Int) -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToMessage: (Int, String) -> Unit = { _, _ -> },
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showActionSheet by remember { mutableStateOf(false) }
    var showDeleteWriteDialog by remember { mutableStateOf<WriteBlock?>(null) }
    var showFlagDialog by remember { mutableStateOf(false) }
    var showActionResultDialog by remember { mutableStateOf(false) }

    // Dark green and rep green colors
    val darkGreen = Color(0xFF006600)
    val repGreen = Color(red = 0.549f, green = 0.78f, blue = 0.365f)
    val backgroundColor = Color.White

    LaunchedEffect(userId) {
        viewModel.initialize(userId)
    }

    LaunchedEffect(uiState.actionResultMessage) {
        if (uiState.actionResultMessage != null) {
            showActionResultDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Custom Header
        ProfileNavigationHeader(
            name = uiState.user?.displayName ?: "",
            showSettings = viewModel.isCurrentUser,
            onBack = onNavigateBack,
            onSettings = onNavigateToSettings
        )

        if (!uiState.isLoaded) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = repGreen)
            }
        } else if (uiState.user != null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Profile Info Section
                item {
                    ProfileInfoSection(
                        user = uiState.user!!,
                        repGreen = repGreen
                    )
                }

                // Broadcast Section
                item {
                    if (!uiState.user!!.broadcast.isNullOrEmpty()) {
                        ProfileBroadcastSection(
                            broadcast = uiState.user!!.broadcast!!
                        )
                    }
                }

                // Segmented Picker
                item {
                    ProfileSegmentedPicker(
                        segments = listOf("Rep", "Goals", "Write"),
                        selectedIndex = uiState.selectedTab,
                        onSelectionChanged = { viewModel.selectTab(it) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Tab Content
                when (uiState.selectedTab) {
                    0 -> {
                        // Rep Tab - Portals
                        if (uiState.portals.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No portals yet.",
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(uiState.portals) { portal ->
                                PortalListItem(
                                    portal = portal,
                                    onClick = { onNavigateToPortal(portal.id) }
                                )
                                if (portal != uiState.portals.last()) {
                                    Divider(color = Color(0xFFE4E4E4))
                                }
                            }
                        }
                    }
                    1 -> {
                        // Goals Tab
                        if (uiState.goals.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No goals yet.",
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(uiState.goals) { goal ->
                                GoalListItemProfile(
                                    goal = goal,
                                    onClick = { onNavigateToGoal(goal.id) }
                                )
                                if (goal != uiState.goals.last()) {
                                    Divider(color = Color(0xFFE4E4E4))
                                }
                            }
                        }
                    }
                    2 -> {
                        // Write Tab
                        items(uiState.writeBlocks) { write ->
                            WriteBlockItem(
                                write = write,
                                isCurrentUser = viewModel.isCurrentUser,
                                onEdit = { viewModel.startEditingWrite(write) },
                                onDelete = { showDeleteWriteDialog = write }
                            )
                        }

                        if (viewModel.isCurrentUser) {
                            item {
                                WriteEditor(
                                    title = uiState.writeTitle,
                                    content = uiState.writeContent,
                                    isEditing = uiState.editingWrite != null,
                                    onTitleChange = { viewModel.updateWriteTitle(it) },
                                    onContentChange = { viewModel.updateWriteContent(it) },
                                    onSave = { viewModel.saveWrite() },
                                    onCancel = { viewModel.cancelEditingWrite() }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Action Bar
            ProfileBottomBar(
                isCurrentUser = viewModel.isCurrentUser,
                repGreen = repGreen,
                onAddAction = { showActionSheet = true },
                onMessage = {
                    uiState.user?.let { user ->
                        onNavigateToMessage(user.id, user.displayName)
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "User not found.",
                    color = Color.Gray
                )
            }
        }
    }

    // Action Sheet for Current User
    if (showActionSheet && viewModel.isCurrentUser) {
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = repGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showActionSheet = false
                            onNavigateToEditProfile()
                        }
                        .padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showActionSheet = false }
                        .padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Action Sheet for Other Users
    if (showActionSheet && !viewModel.isCurrentUser) {
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+ to NTWK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = repGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.addToNetwork { success, message ->
                                showActionSheet = false
                            }
                        }
                        .padding(vertical = 12.dp)
                )

                Divider()

                if (uiState.isBlocked) {
                    Text(
                        text = "Unblock User",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.unblockUser { success, message ->
                                    showActionSheet = false
                                }
                            }
                            .padding(vertical = 12.dp)
                    )
                } else {
                    Text(
                        text = "Block User",
                        fontSize = 16.sp,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.blockUser { success, message ->
                                    showActionSheet = false
                                }
                            }
                            .padding(vertical = 12.dp)
                    )
                }

                Divider()

                Text(
                    text = "Flag as Inappropriate",
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showActionSheet = false
                            showFlagDialog = true
                        }
                        .padding(vertical = 12.dp)
                )

                Divider()

                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showActionSheet = false }
                        .padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Delete Write Confirmation Dialog
    showDeleteWriteDialog?.let { write ->
        AlertDialog(
            onDismissRequest = { showDeleteWriteDialog = null },
            title = { Text("Delete Writing Block") },
            text = { Text("Are you sure you want to delete this writing block? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWrite(write)
                        showDeleteWriteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWriteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Flag User Confirmation Dialog
    if (showFlagDialog) {
        AlertDialog(
            onDismissRequest = { showFlagDialog = false },
            title = { Text("Flag User?") },
            text = { Text("Are you sure you want to flag this person as inappropriate?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.flagUser { success, message ->
                            showFlagDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Flag")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFlagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Action Result Dialog
    if (showActionResultDialog && uiState.actionResultMessage != null) {
        AlertDialog(
            onDismissRequest = {
                showActionResultDialog = false
                viewModel.clearActionResult()
            },
            title = { Text("Success") },
            text = { Text(uiState.actionResultMessage!!) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showActionResultDialog = false
                        viewModel.clearActionResult()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ProfileNavigationHeader(
    name: String,
    showSettings: Boolean,
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    val repGreen = Color(red = 0.549f, green = 0.78f, blue = 0.365f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Back",
                tint = repGreen
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        if (showSettings) {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Settings",
                    tint = repGreen
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }

    Divider(color = Color(0xFFE4E4E4))
}

@Composable
private fun ProfileInfoSection(
    user: User,
    repGreen: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Profile Image
        AsyncImage(
            model = user.profile_picture_url ?: user.imageUrl ?: user.avatarUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.3f)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // User Info
        Column {
            if (!user.city.isNullOrEmpty()) {
                Text(
                    text = user.city!!,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Skills
            user.skills?.take(3)?.forEach { skill ->
                Text(
                    text = skill,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileBroadcastSection(broadcast: String) {
    Text(
        text = broadcast,
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileSegmentedPicker(
    segments: List<String>,
    selectedIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
    ) {
        segments.forEachIndexed { index, segment ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (selectedIndex == index) Color.Black else Color.White)
                    .clickable { onSelectionChanged(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = segment,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedIndex == index) Color.White else Color.Black
                )
            }
            if (index < segments.size - 1) {
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(),
                    color = Color(0xFFE4E4E4)
                )
            }
        }
    }
}

@Composable
private fun PortalListItem(
    portal: Portal,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = portal.mainImageUrl,
            contentDescription = portal.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray.copy(alpha = 0.3f)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = portal.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            portal.subtitle?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun GoalListItemProfile(
    goal: Goal,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = goal.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = goal.description ?: "",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        val progress = (goal.progressPercent / 100.0).toFloat()

        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth(),
            color = Color(red = 0.549f, green = 0.78f, blue = 0.365f)
        )

        Text(
            text = "${goal.filledQuota.toInt()} / ${goal.quota.toInt()} (${goal.progressPercent.toInt()}%)",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun WriteBlockItem(
    write: WriteBlock,
    isCurrentUser: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (!write.title.isNullOrEmpty()) {
            Text(
                text = write.title!!,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = write.content,
            fontSize = 18.sp
        )

        if (isCurrentUser) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit", color = Color.Blue, fontSize = 18.sp)
                }

                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color.Red, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun WriteEditor(
    title: String,
    content: String,
    isEditing: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val repGreen = Color(red = 0.549f, green = 0.78f, blue = 0.365f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isEditing) "Edit block:" else "Add new block:",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (isEditing) {
                TextButton(onClick = onCancel) {
                    Text("Cancel", color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = repGreen)
            ) {
                Text(if (isEditing) "Update" else "Save")
            }
        }
    }
}

@Composable
private fun ProfileBottomBar(
    isCurrentUser: Boolean,
    repGreen: Color,
    onAddAction: () -> Unit,
    onMessage: () -> Unit
) {
    Column {
        Divider(color = Color(0xFFE4E4E4))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onAddAction,
                modifier = Modifier
                    .weight(1f)
                    .height(41.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 0.482f, green = 0.749f, blue = 0.294f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (!isCurrentUser) {
                IconButton(onClick = onMessage) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Message",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
