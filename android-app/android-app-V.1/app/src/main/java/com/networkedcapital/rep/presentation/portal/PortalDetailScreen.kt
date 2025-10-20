@file:OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.networkedcapital.rep.presentation.portal

import android.content.res.Configuration
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
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.networkedcapital.rep.domain.model.User
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.PortalDetail
import com.networkedcapital.rep.domain.model.PortalFile
import com.networkedcapital.rep.domain.model.BarChartData

@Composable
fun PortalDetailScreen(
    uiState: PortalDetailUiState,
    userId: Int,
    portalId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToGoal: (Int) -> Unit,
    onNavigateToEditGoal: (Int?, Int) -> Unit,
    onNavigateToEditPortal: (Int) -> Unit,
    viewModel: PortalDetailViewModel
) {
    var showActionSheet by remember { mutableStateOf(false) }
    var showFlagDialog by remember { mutableStateOf(false) }
    var showFullscreenImages by remember { mutableStateOf(false) }
    var fullscreenImageIndex by remember { mutableStateOf(0) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showMessageSheet by remember { mutableStateOf(false) }
    var selectedLeadUser by remember { mutableStateOf<User?>(null) }

    // Detect device orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    // Load portal data when screen opens
    LaunchedEffect(portalId, userId) {
        viewModel.loadPortalDetail(portalId, userId)
    }

    // Auto-show fullscreen gallery in landscape mode
    LaunchedEffect(isLandscape) {
        if (isLandscape && !showFullscreenImages && uiState.portalDetail?.aSections?.flatMap { it.aFiles ?: emptyList() }?.isNotEmpty() == true) {
            showFullscreenImages = true
            fullscreenImageIndex = 0
        }
    }

    // Find the first goal that can accept support/payment
    val supportGoal = uiState.portalDetail?.aGoals?.firstOrNull { 
        it.typeName == "Fund" || it.typeName == "Sales" 
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(shadowElevation = 4.dp) {
            PortalDetailHeader(
                portalName = uiState.portalDetail?.name ?: "Portal",
                onBackClick = onNavigateBack,
                onMoreClick = { if (uiState.portalDetail != null) showActionSheet = true }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8CC55D))
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.error ?: "Could not load portal details.",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadPortalDetail(portalId, userId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8CC55D)
                                )
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }
                uiState.portalDetail != null -> {
                    val portal = uiState.portalDetail!!
                    val goals = uiState.portalGoals ?: portal.aGoals ?: emptyList()
                    var selectedSection by remember { mutableStateOf(0) }

                    // The main content of the screen
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        // Show image gallery if images exist
                        val images = portal.aSections?.flatMap { it.aFiles ?: emptyList() } ?: emptyList()
                        if (images.isNotEmpty()) {
                            item {
                                ImageGallery(images = images, onImageClick = { index ->
                                    fullscreenImageIndex = index
                                    showFullscreenImages = true
                                })
                            }
                        }

                        // Segmented control and content sections
                        item {
                            PortalContentSection(
                                portal = portal,
                                goals = goals,
                                selectedSection = selectedSection,
                                onSectionChange = { selectedSection = it },
                                onGoalClick = onNavigateToGoal
                            )
                        }
                    }
                }
                else -> {
                    // Fallback: Show message if portal data is missing
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Could not load portal details.",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        // Bottom Bar
        if (uiState.portalDetail != null) {
            PortalBottomBar(
                onAddClick = { /* TODO: Implement Join Team logic */ },
                onMessageClick = {
                    // Find lead user to message
                    val lead = uiState.portalDetail?.aLeads?.firstOrNull()
                    if (lead != null) {
                        selectedLeadUser = lead
                        showMessageSheet = true
                    } else {
                        // TODO: Navigate to group chat for portal
                        // Portal group chat navigation not yet implemented
                    }
                }
            )
        }
    }

    // Action Sheet (requires portal detail to be not null)
    if (showActionSheet && uiState.portalDetail != null) {
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
            },
            onSupport = {
                if (supportGoal != null) {
                    showActionSheet = false
                    showPaymentSheet = true
                }
            },
            supportGoal = supportGoal
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
        val images = uiState.portalDetail?.aSections?.flatMap { it.aFiles ?: emptyList() } ?: emptyList()
        FullscreenImageViewer(
            images = images,
            startIndex = fullscreenImageIndex,
            onDismiss = { showFullscreenImages = false }
        )
    }

    // Payment/Support Sheet
    if (showPaymentSheet && supportGoal != null) {
        AlertDialog(
            onDismissRequest = { showPaymentSheet = false },
            title = { 
                Text(
                    "Support ${uiState.portalDetail?.name ?: "This Portal"}",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Choose how you'd like to support:")
                    // Navigate to payment screen or web view
                    Button(
                        onClick = { 
                            /* Navigate to payment flow */
                            showPaymentSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AttachMoney,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Make a Payment", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPaymentSheet = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Message Sheet
    if (showMessageSheet && selectedLeadUser != null) {
        AlertDialog(
            onDismissRequest = { showMessageSheet = false },
            title = { 
                Text(
                    "Send Message to ${selectedLeadUser?.firstName} ${selectedLeadUser?.lastName}",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Start a conversation with this portal's lead:")
                    // Navigate to chat screen
                    Button(
                        onClick = {
                            // TODO: Navigate to individual chat with selected lead
                            showMessageSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8CC55D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Chat", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMessageSheet = false }) {
                    Text("Cancel")
                }
            }
        )
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
                    textAlign = TextAlign.Center
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.03f))
                    .clickable { onImageClick(page) }
            ) {
                AsyncImage(
                    model = images[page].url,
                    contentDescription = "Portal image ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
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
        
        // Segmented Control - iOS style
        PortalSegmentedControl(
            sections = listOf("Goal Teams", "Story"),
            selectedIndex = selectedSection,
            onSelectionChanged = onSectionChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Divider(color = Color(0xFFE4E4E4))

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
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
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
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) Color.Black else Color.Transparent
                        )
                        .clickable { onSelectionChanged(index) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = section,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
                if (index < sections.lastIndex) {
                    Spacer(modifier = Modifier.width(2.dp))
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
        modifier = modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(goals) { goal ->
            GoalListItem(
                goal = goal,
                onClick = { onGoalClick(goal.id) }
            )
        }
    }
}

@Composable
fun StorySection(
    portal: PortalDetail,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Leads section
        item {
            Text(
                text = "Leads",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(portal.aLeads ?: emptyList()) { lead ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.width(40.dp)
                    ) {
                        if (!lead.profileImageUrlCompat.isNullOrBlank()) {
                            AsyncImage(
                                model = lead.profileImageUrlCompat,
                                contentDescription = "${lead.firstName} ${lead.lastName}",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.15f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            InitialsAvatar(user = lead)
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
            
            Divider(
                color = Color(0xFFE4E4E4), 
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Story text blocks
        items(portal.aTexts?.filter { it.section == "story" } ?: emptyList()) { textBlock ->
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
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
fun InitialsAvatar(user: User) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${user.firstName?.take(1) ?: ""}${user.lastName?.take(1) ?: ""}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
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
            .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = Color.White
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
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
                    border = BorderStroke(1.dp, Color(0xFFE4E4E4))
                ) {
                    Text("Join Team", color = Color.Black, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = onMessageClick,
                    modifier = Modifier
                        .size(44.dp)
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
    onFlag: () -> Unit,
    onSupport: () -> Unit,
    supportGoal: Goal?
) {
    val isCurrentUserLead = portal.aLeads?.any { it.id == userId } ?: false
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
            // Support/Payment option - match iOS
            if (supportGoal != null) {
                Button(
                    onClick = onSupport,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF006400)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AttachMoney,
                            contentDescription = "Support",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Support",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Add Goal option for leads
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

            // Select Goal Team option
            Button(
                onClick = onDismiss,
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

            // Edit Purpose option for owner
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

            // Flag option
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

            // Cancel button
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
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
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

        // Image counter
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${images.size}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Navigation arrows (if more than one image)
        if (images.size > 1) {
            // Previous
            if (pagerState.currentPage > 0) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        tint = Color.White
                    )
                }
            }
            
            // Next
            if (pagerState.currentPage < images.size - 1) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
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
    var lastScale by remember { mutableStateOf(1f) }
    
    val state = rememberTransformableState { zoomChange, panChange, _ ->
        // Apply zoom constraints
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        
        // Only allow panning when zoomed in
        if (scale > 1.01f) {
            offset = offset + panChange
        } else {
            offset = Offset.Zero
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .transformable(state)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1.01f) 1f else 2.5f
                        lastScale = scale
                        if (scale <= 1.01f) {
                            offset = Offset.Zero
                        }
                    },
                    onTap = { 
                        if (scale <= 1.01f) onDismiss() 
                    }
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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Goal title & description
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = when {
                        goal.title != null && goal.title.isNotBlank() -> goal.title
                        !goal.description.isNullOrBlank() -> goal.description
                        else -> "Goal"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                if (!goal.description.isNullOrBlank() && (goal.title == null || goal.title != goal.description)) {
                    Text(
                        goal.description, 
                        fontSize = 14.sp, 
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
            
            // Show bar chart if chartData exists
            if (goal.chartData != null && goal.chartData.isNotEmpty()) {
                GoalBarChart(data = goal.chartData)
            }
            
            // Divider
            if (goal.typeName != null) {
                Divider(color = Color(0xFFE4E4E4))
                
                // Type label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                when (goal.typeName) {
                                    "Fund" -> Color(0xFF006400)
                                    "Sales" -> Color(0xFF8CC55D)
                                    else -> Color(0xFF8CC55D)
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = goal.typeName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalBarChart(data: List<BarChartData>) {
    // Match iOS styling
    val quota = if (data.isNotEmpty()) data.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1.0 else 1.0
    val barsToShow = if (data.size > 4) data.takeLast(4) else data
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Chart title
        Text(
            text = "Progress", 
            fontWeight = FontWeight.Medium, 
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // The actual chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            barsToShow.forEach { bar ->
                val percent = (bar.value / quota).coerceIn(0.0, 1.0)
                val barHeight = (percent * 60).dp
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Value label
                    Text(
                        text = bar.valueLabel,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Bar
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(barHeight)
                            .background(Color(0xFF8CC55D), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                    
                    // Bottom label
                    Text(
                        text = bar.bottomLabel,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
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
    // Improved URL detection - handles more types of URLs
    val urlRegex = "(?:https?://|www\\.)[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_+.~#?&/=]*)".toRegex()
    
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        for (match in urlRegex.findAll(text)) {
            val url = match.value
            val start = match.range.first
            val end = match.range.last + 1
            
            // Text before the URL
            append(text.substring(lastIndex, start))
            
            // The URL itself
            pushStringAnnotation(tag = "URL", annotation = if (url.startsWith("www.")) "https://$url" else url)
            withStyle(SpanStyle(
                color = Color(0xFF0000FF),  // iOS blue link color
                textDecoration = TextDecoration.Underline
            )) {
                append(url)
            }
            pop()
            
            lastIndex = end
        }
        
        // Remaining text
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
    
    val context = LocalContext.current
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    try {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(annotation.item)
                        )
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle URL opening failure
                    }
                }
        }
    )
}

// Extension property to simplify goal support detection
val Goal.isSupported: Boolean
    get() = this.typeName == "Fund" || this.typeName == "Sales"