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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.networkedcapital.rep.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalDetailScreen(
    portalId: Int,
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToGoalDetail: (Int) -> Unit,
    onNavigateToEditPortal: (Int) -> Unit,
    onNavigateToChat: (Int, String, String?) -> Unit,
    onNavigateToEditGoal: (Int?, Int) -> Unit,
    viewModel: PortalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showActionSheet by remember { mutableStateOf(false) }
    var showFlagDialog by remember { mutableStateOf(false) }
    var showFullscreenImages by remember { mutableStateOf(false) }
    var fullscreenImageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(portalId, userId) {
        viewModel.loadPortalDetail(portalId, userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            // DEBUG: Show portal name and ID at top
            Text(
                text = "Portal: ${portal.name} (ID: ${portal.id})",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow)
                    .padding(8.dp),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            // DEBUG: Show raw portal data
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .padding(8.dp)
            ) {
                val portalJson = try {
                    Json.encodeToString(portal)
                } catch (e: Exception) {
                    portal.toString()
                }
                Text(
                    text = "RAW DATA: $portalJson",
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Show image gallery if images exist
            val images = portal.aSections?.flatMap { it.aFiles } ?: emptyList()
            if (images.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ImageGallery(images = images, onImageClick = { index ->
                            fullscreenImageIndex = index
                            showFullscreenImages = true
                        })
                        Spacer(modifier = Modifier.height(12.dp))
                        PortalContentSection(
                            portal = portal,
                            goals = goals,
                            selectedSection = selectedSection,
                            onSectionChange = { selectedSection = it },
                            onGoalClick = onNavigateToGoalDetail
                        )
                    }
                } else {
                    PortalContentSection(
                        portal = portal,
                        goals = goals,
                        selectedSection = selectedSection,
                        onSectionChange = { selectedSection = it },
                        onGoalClick = onNavigateToGoalDetail
                    )
                }
        } else {
            // Fallback: Show message if portal data is missing
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No portal data loaded.",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Action Sheet
        if (showActionSheet) {
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF8CC55D) // Rep green color
            )
        }

        Text(
            text = portalName,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(onClick = onMoreClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
    
    Divider(color = Color(0xFFE4E4E4))
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
            AsyncImage(
                model = images[page].url,
                contentDescription = "Portal image ${page + 1}",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick(page) },
                contentScale = ContentScale.Crop
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
    Row(
        modifier = modifier
            .background(
                Color(0xFFE4E4E4),
                RoundedCornerShape(4.dp)
            )
            .padding(1.dp)
    ) {
        sections.forEachIndexed { index, section ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedIndex == index) Color.Black else Color.Transparent,
                        RoundedCornerShape(3.dp)
                    )
                    .clickable { onSelectionChanged(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = section,
                    color = if (selectedIndex == index) Color.White else Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(portal.aLeads ?: emptyList()) { lead ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AsyncImage(
                            model = lead.profileImageUrlCompat,
                            contentDescription = "${lead.firstName} ${lead.lastName}",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "${lead.firstName?.take(1) ?: ""}${lead.lastName?.take(1) ?: ""}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                textBlock.title?.takeIf { it.isNotBlank() }?.let { title ->
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                textBlock.text?.takeIf { it.isNotBlank() }?.let { text ->
                    Text(
                        text = text,
                        fontSize = 16.sp
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
            modifier = modifier.fillMaxWidth(),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAddClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8CC55D)
                    )
                ) {
                    Text("Join Team", color = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = onMessageClick) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Message",
                        tint = Color(0xFF8CC55D)
                    )
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
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isCurrentUserLead) {
                TextButton(
                    onClick = onAddGoal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Add Goal",
                        color = Color(0xFF8CC55D),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = onDismiss, // TODO: Implement join team logic
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Select Goal Team",
                    color = Color(0xFF8CC55D),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isPortalOwner) {
                TextButton(
                    onClick = onEditPortal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Edit Purpose",
                        color = Color(0xFF8CC55D),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = onFlag,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Flag as Inappropriate",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
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
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    AsyncImage(
        model = imageUrl,
        contentDescription = "Fullscreen image",
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() }, // Single tap to dismiss
        contentScale = ContentScale.Fit
    )
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
