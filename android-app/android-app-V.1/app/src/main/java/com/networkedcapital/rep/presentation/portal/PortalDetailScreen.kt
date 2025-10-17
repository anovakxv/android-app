@file:OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.networkedcapital.rep.presentation.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

    Column(modifier = Modifier.fillMaxSize()) {
        // Add header with back button and portal name
        if (uiState.portalDetail != null) {
            PortalDetailHeader(
                portalName = uiState.portalDetail.name,
                onBackClick = onNavigateBack,
                onMoreClick = { showActionSheet = true }
            )
        } else {
            // Show a placeholder header if portal is not loaded
            PortalDetailHeader(
                portalName = "Portal",
                onBackClick = onNavigateBack,
                onMoreClick = { }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.portalDetail != null) {
                val portal = uiState.portalDetail!!
                val goals = uiState.portalGoals ?: portal.aGoals ?: emptyList()
                var selectedSection by remember { mutableStateOf(0) }

                // DEBUG: Show portal name and ID at top (optional, can remove)
                // Text(
                //     text = "Portal: ${portal.name} (ID: ${portal.id})",
                //     modifier = Modifier.padding(8.dp),
                //     color = Color.Black,
                //     fontWeight = FontWeight.Bold
                // )
                // DEBUG: Show raw portal data (optional, can remove)
                // Box(
                //     modifier = Modifier.padding(8.dp)
                // ) {
                //     val portalJson = try {
                //         Json.encodeToString(portal)
                //     } catch (e: Exception) { "" }
                //     Text(
                //         text = portalJson,
                //         fontSize = 10.sp,
                //         color = Color.Gray,
                //         maxLines = 8,
                //         overflow = TextOverflow.Ellipsis
                //     )
                // }

                // Show image gallery if images exist
                val images = portal.aSections?.flatMap { it.aFiles } ?: emptyList()
                if (images.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {/* Lines 114-126 omitted */}
                } else {/* Lines 128-135 omitted */}
            } else {
                // Fallback: Show message if portal data is missing
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        /* Lines 143-146 omitted */
                    )
                }
            }
        }
            val portal = uiState.portalDetail!!
            val goals = uiState.portalGoals ?: portal.aGoals ?: emptyList()
            var selectedSection by remember { mutableStateOf(0) }

            // DEBUG: Show portal name and ID at top
            Text(
                text = "Portal: ${portal.name} (ID: ${portal.id})",
                Column(modifier = Modifier.fillMaxSize()) {
                    // Polished header: center title, balanced icons, no debug text
                    Surface(shadowElevation = 4.dp) {
                        if (uiState.portalDetail != null) {
                            PortalDetailHeader(
                                portalName = uiState.portalDetail.name,
                                onBackClick = onNavigateBack,
                                onMoreClick = { showActionSheet = true }
                            )
                        } else {
                            PortalDetailHeader(
                                portalName = "Portal",
                                onBackClick = onNavigateBack,
                                onMoreClick = { }
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (uiState.portalDetail != null) {
                            val portal = uiState.portalDetail!!
                            val goals = uiState.portalGoals ?: portal.aGoals ?: emptyList()
                            var selectedSection by remember { mutableStateOf(0) }

                            // Show image gallery if images exist
                            val images = portal.aSections?.flatMap { it.aFiles } ?: emptyList()
                            if (images.isNotEmpty()) {
                                Column(modifier = Modifier.fillMaxWidth()) {/* Lines 114-126 omitted */}
                            } else {/* Lines 128-135 omitted */}
                        } else {
                            // Fallback: Show message if portal data is missing
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    /* Lines 143-146 omitted */
                                )
                            }
                        }
                    }
            PortalActionSheet(
                portal = uiState.portalDetail!!,
                userId = userId,
                onDismiss = { showActionSheet = false },
                onAddGoal = {
                    showActionSheet = false
                    onNavigateToEditGoal(null, portalId)
                },
                onEditPortal = {
                    showActionSheet = false
                    onNavigateToEditPortal(portalId)
                },
                onFlag = {
                    showActionSheet = false
                    showFlagDialog = true
                }
            )
        }

        // Flag Dialog
        if (showFlagDialog) {
            AlertDialog(
                onDismissRequest = { showFlagDialog = false },
                title = { Text("Flag Portal") },
                text = { Text("Are you sure you want to flag this portal as inappropriate?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.flagPortal(portalId)
                            showFlagDialog = false
                        }
                    ) {
                        Text("Flag", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFlagDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Fullscreen Image Viewer
        if (showFullscreenImages) {
            val images = uiState.portalDetail?.aSections?.flatMap { it.aFiles } ?: emptyList()
            FullscreenImageViewer(
                images = images,
                startIndex = fullscreenImageIndex,
                onDismiss = { showFullscreenImages = false }
            )
        }
    }

    // Handle flag result
    uiState.flagResult?.let { result ->
        LaunchedEffect(result) {
            // Show snackbar or toast for flag result
        }
    }
}

@Composable
fun PortalDetailHeader(
    portalName: String,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF8CC55D)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = portalName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }
        Divider(
            color = Color(0xFFE4E4E4),
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ImageGallery(
    images: List<PortalFile>,
    onImageClick: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val imageHeight = (configuration.screenWidthDp * 9 / 16).dp
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ZoomableImage(
                imageUrl = images[page].url,
                onDismiss = { /* no-op for gallery */ }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onImageClick(page) }
            )
        }

        // Page indicator
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun PortalContentSection(
    portal: PortalDetail,
    goals: List<Goal>,
    selectedSection: Int,
    onSectionChange: (Int) -> Unit,
    onGoalClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = Color(0xFFE4E4E4))
        
        // Segmented Control
        PortalSegmentedControl(
            sections = listOf("Goal Teams", "Story"),
            selectedIndex = selectedSection,
            onSelectionChanged = onSectionChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Content based on selected section
        when (selectedSection) {
            0 -> GoalTeamsSection(
                goals = goals,
                onGoalClick = onGoalClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            1 -> StorySection(
                portal = portal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun PortalSegmentedControl(
    sections: List<String>,
    selectedIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // iOS-style: black and white segmented control
    val backgroundColor = Color.White
    val selectedColor = Color.White
    val unselectedTextColor = Color.Black
    val selectedTextColor = Color.White
    val borderColor = Color.Black
    val indicatorColor = Color.Black

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(2.dp)
    ) {
        Row(
            modifier = Modifier.height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sections.forEachIndexed { index, section ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) indicatorColor else Color.Transparent
                        )
                        .border(
                            width = if (isSelected) 0.dp else 0.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectionChanged(index) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = section,
                        color = if (isSelected) selectedColor else unselectedTextColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp,
                        letterSpacing = 0.2.sp
                    )
                }
                if (index < sections.lastIndex) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

@Composable
fun GoalTeamsSection(
    goals: List<Goal>,
    onGoalClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(goals) { goal ->
            GoalListItem(
                goal = goal,
                onClick = {
                    // Navigate to the goal team page for the clicked goal
                    onGoalClick(goal.id)
                }
            )
            if (goal != goals.last()) {
                Divider(color = Color(0xFFE4E4E4))
            }
        }
    }
}

@Composable
fun StorySection(
    portal: PortalDetail,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Leads section
            Text(
                text = "Leads",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(portal.aLeads ?: emptyList()) { lead ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.width(44.dp)
                    ) {
                        if (!lead.profileImageUrlCompat.isNullOrBlank()) {
                            AsyncImage(
                                model = lead.profileImageUrlCompat,
                                contentDescription = "${lead.firstName} ${lead.lastName}",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.15f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${lead.firstName?.take(1) ?: ""}${lead.lastName?.take(1) ?: ""}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                        Text(
                            text = "${lead.firstName?.take(1) ?: ""}${lead.lastName?.take(1) ?: ""}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        item {
            Divider(color = Color(0xFFE4E4E4))
        }

        // Story text blocks
            items(portal.aTexts?.filter { it.section == "story" } ?: emptyList()) { textBlock ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    textBlock.title?.takeIf { it.isNotBlank() }?.let { title ->
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    textBlock.text?.takeIf { it.isNotBlank() }?.let { text ->
                        LinkableText(
                            text = text
                        )
                    }
                }
            }
    }
}

@Composable
fun PortalBottomBar(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onMessageClick: () -> Unit
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .shadow(10.dp, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
            color = Color.White,
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider(
                    color = Color(0xFFE4E4E4),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAddClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("Join Team", color = Color.Black, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    IconButton(
                        onClick = onMessageClick,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE4E4E4), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Message",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
}

@Composable
fun PortalActionSheet(
    portal: PortalDetail,
    userId: Int,
    onDismiss: () -> Unit,
    onAddGoal: () -> Unit,
    onEditPortal: () -> Unit,
    onFlag: () -> Unit
) {
    val isCurrentUserLead = portal.aUsers?.any { it.id == userId } ?: false
    val isPortalOwner = portal.usersId == userId

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // iOS order: Add Goal, Select Goal Team, Edit Purpose, Flag, Cancel
            if (isCurrentUserLead) {
                Button(
                    onClick = onAddGoal,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF8CC55D)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Add Goal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onDismiss, // TODO: Implement join team logic
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF8CC55D)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Select Goal Team",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isPortalOwner) {
                Button(
                    onClick = onEditPortal,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF8CC55D)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Edit Purpose",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onFlag,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Flag as Inappropriate",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FullscreenImageViewer(
    images: List<PortalFile>,
    startIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { images.size }
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ZoomableImage(
                imageUrl = images[page].url,
                onDismiss = onDismiss
            )
        }

        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Page indicator
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomableImage(
    imageUrl: String?,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset += panChange
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .transformable(state)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onDismiss() }
                )
            }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Fullscreen image",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun GoalListItem(goal: Goal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = when {
                    goal.title != null && goal.title.isNotBlank() -> goal.title
                    !goal.description.isNullOrBlank() -> goal.description
                    else -> "Goal"
                },
                fontWeight = FontWeight.Bold
            )
            if (!goal.description.isNullOrBlank() && (goal.title == null || goal.title != goal.description)) {
                Text(goal.description, fontSize = 14.sp, color = Color.Gray)
            }
            // Show bar chart if chartData exists
            if (goal.chartData != null && goal.chartData.isNotEmpty()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(Color(0xFF8CC55D), RoundedCornerShape(4.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        goal.chartData.forEach { bar ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = bar.valueLabel,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .height((bar.value / (goal.chartData.maxOfOrNull { it.value } ?: 1.0) * 24).dp)
                                        .fillMaxWidth(0.7f)
                                        .background(Color.White, RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = bar.bottomLabel,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalBarChart(data: List<BarChartData>) {
    val quota = if (data.isNotEmpty()) data.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1.0 else 1.0
    val barsToShow = if (data.size > 4) data.takeLast(4) else data
    Row(
        modifier = Modifier
            .height(81.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        barsToShow.forEach { bar ->
            val percent = (bar.value / quota).coerceIn(0.0, 1.0)
            val barHeight = (percent * 77).dp
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(24.dp)
                    .padding(horizontal = 3.dp)
            ) {
                Spacer(modifier = Modifier.height(81.dp - barHeight))
                Box(
                    modifier = Modifier
                        .height(barHeight)
                        .width(24.dp)
                        .background(Color(0xFF8CC55D), RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(bar.bottomLabel, fontSize = 10.sp, color = Color.Black)
            }
        }
    }
}

val User.profileImageUrlCompat: String?
    get() = this::class.members.firstOrNull { it.name == "profileImageUrl" }
        ?.call(this) as? String
        ?: this::class.members.firstOrNull { it.name == "imageUrl" }
            ?.call(this) as? String
        ?: this::class.members.firstOrNull { it.name == "avatarUrl" }
            ?.call(this) as? String

@Composable
fun LinkableText(text: String) {
    val urlRegex = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)".toRegex()
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        for (match in urlRegex.findAll(text)) {
            val url = match.value
            val start = match.range.first
            val end = match.range.last + 1
            append(text.substring(lastIndex, start))
            pushStringAnnotation(tag = "URL", annotation = url)
            withStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                append(url)
            }
            pop()
            lastIndex = end
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    ClickableText(
        text = annotatedString,
        style = LocalTextStyle.current.copy(fontSize = 16.sp),
        onClick = { offset: Int ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(annotation.item))
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
        }
    )
}
