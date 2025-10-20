package com.networkedcapital.rep.presentation.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.networkedcapital.rep.R
import com.networkedcapital.rep.domain.model.*
import com.networkedcapital.rep.domain.model.User
import com.networkedcapital.rep.presentation.main.MainViewModel.MainPage
import kotlinx.coroutines.delay

@Composable
fun ShimmerPortalItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth(0.5f)
                        .background(Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.8f)
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun ShimmerPersonItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth(0.5f)
                        .background(Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.8f)
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun ErrorStateView(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color.Red.copy(alpha = 0.7f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red.copy(alpha = 0.8f)
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

// Helper to robustly patch/validate image URLs (S3/full URLs)
fun patchPortalImageUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    // If already a full URL, return as is
    if (url.startsWith("http://") || url.startsWith("https://")) return url
    // S3 or relative path handling (customize as needed)
    val s3Base = "https://rep-portal-files.s3.amazonaws.com/"
    return if (url.startsWith("/")) s3Base + url.removePrefix("/") else s3Base + url
}

// NEW: iOS-Style Black Segmented Control
@Composable
fun MainSegmentedPicker(
    segments: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    attentionDotIndices: Set<Int> = emptySet(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(240.dp)
            .height(32.dp)
            .background(Color(0xFAF9F9F9))
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        segments.forEachIndexed { index, segment ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isSelected) Color.Black else Color.Transparent)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = segment,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    if (index in attentionDotIndices) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .offset(x = 24.dp, y = (-8).dp)
                                .background(Color(0xFF8CC55D), CircleShape)
                                .zIndex(2f)
                        )
                    }
                }
            }
            if (index < segments.lastIndex) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFE4E4E4))
                )
            }
        }
    }
}

// NEW: Enhanced Action Sheet with More Options
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActionSheet(
    showOnlySafePortals: Boolean,
    onToggleSafe: () -> Unit,
    onAddPurpose: () -> Unit,
    onTeamChat: () -> Unit,
    onSearch: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Show option (Safe/All)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Show:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(24.dp))
                
                // All button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { if (showOnlySafePortals) onToggleSafe() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(2.dp, Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!showOnlySafePortals) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Blue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "All",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!showOnlySafePortals) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Safe button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { if (!showOnlySafePortals) onToggleSafe() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(2.dp, Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showOnlySafePortals) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Blue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Safe",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (showOnlySafePortals) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Gray
                    )
                }
            }
            
            // Add Purpose button
            Button(
                onClick = { 
                    onAddPurpose()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF8CC55D)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Add Purpose",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Team Chat button
            Button(
                onClick = { 
                    onTeamChat()
                    onDismiss() 
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF8CC55D)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Team Chat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Search button
            Button(
                onClick = { 
                    onSearch()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF8CC55D)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Search",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Cancel button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProfile: (Int) -> Unit,
    onNavigateToPortalDetail: (Int) -> Unit,
    onNavigateToPersonDetail: (Int) -> Unit,
    onNavigateToChat: (Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // NEW: Track action sheet visibility
    var showActionSheet by remember { mutableStateOf(false) }
    
    // NEW: Attention state for sections
    val hasUnreadMessages by viewModel.hasUnreadDirectMessages.collectAsState()
    val hasUnreadGroupMessages by viewModel.hasUnreadGroupMessages.collectAsState()
    val openNeedsAttention by viewModel.openNeedsAttention.collectAsState()
    val attentionDotIndices = remember(hasUnreadMessages, hasUnreadGroupMessages, openNeedsAttention) {
        buildSet {
            if (openNeedsAttention) add(0)
            if (hasUnreadMessages || hasUnreadGroupMessages) add(1) 
        }
    }

    // Debounce search input
    var lastSearchQuery by remember { mutableStateOf("") }
    LaunchedEffect(uiState.searchQuery) {
        if (uiState.searchQuery != lastSearchQuery) {
            lastSearchQuery = uiState.searchQuery
            kotlinx.coroutines.delay(350) // 350ms debounce
            if (uiState.searchQuery == lastSearchQuery && uiState.showSearch && uiState.searchQuery.isNotBlank()) {
                viewModel.onSearchQueryChange(uiState.searchQuery)
            }
        }
    }

    LaunchedEffect(uiState.currentUser?.id) {
        val userId = uiState.currentUser?.id ?: 0
        viewModel.loadData(userId)

        // TODO: Setup socket notifications - requires baseURL and token from auth context
        // viewModel.setupSocketNotifications(baseURL, token, userId)

        // NEW: Check for unread messages
        viewModel.checkForUnreadMessages()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // Top Bar with profile, segmented control, and actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image (left)
            Box(modifier = Modifier.size(40.dp)) {
                IconButton(onClick = {
                    uiState.currentUser?.id?.let { onNavigateToProfile(it) }
                }) {
                    val profileImageUrl = uiState.currentUser?.profileImageUrlCompat
                    if (!profileImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // NEW: iOS-Style Black Segmented Control in center
            val segments = when (uiState.currentPage) {
                MainPage.PORTALS -> listOf("Open", "Network", "All")
                MainPage.PEOPLE -> listOf("Chats", "Network", "All")
            }
            MainSegmentedPicker(
                segments = segments,
                selectedIndex = uiState.selectedSection,
                onSelect = { section -> 
                    val userId = uiState.currentUser?.id ?: 0
                    viewModel.onSectionChanged(section, userId)
                },
                attentionDotIndices = attentionDotIndices
            )
            
            Spacer(modifier = Modifier.weight(1f))

            // NEW: Action button (right)
            IconButton(onClick = { showActionSheet = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options"
                )
            }
        }

        // Search Bar
        if (uiState.showSearch) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search portals or people") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = viewModel::toggleSearch) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close search")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // Main Content: Portals/People List
        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isLoading) {
                when (uiState.currentPage) {
                    MainPage.PORTALS -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.Top
                        ) {
                            repeat(4) { ShimmerPortalItem() }
                        }
                    }
                    MainPage.PEOPLE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.Top
                        ) {
                            repeat(4) { ShimmerPersonItem() }
                        }
                    }
                }
            } else if (!uiState.errorMessage.isNullOrBlank()) {
                ErrorStateView(
                    message = uiState.errorMessage ?: "An error occurred.",
                    onRetry = { viewModel.loadData(uiState.currentUser?.id ?: 0) }
                )
            } else {
                when (uiState.currentPage) {
                    MainPage.PORTALS -> {
                        val portals = if (uiState.showSearch && uiState.searchQuery.isNotBlank()) {
                            uiState.searchPortals
                        } else {
                            uiState.portals
                        }
                        if (portals.isEmpty()) {
                            EmptyStateView(
                                message = if (uiState.showSearch && uiState.searchQuery.isNotBlank()) "No portals match your search." else "No portals to display."
                            )
                        } else {
                            PortalsList(
                                portals = portals,
                                onPortalClick = onNavigateToPortalDetail,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    MainPage.PEOPLE -> {
                        if (uiState.selectedSection == 0 && uiState.activeChats.isNotEmpty()) {
                            // Show active chats for first tab
                            ActiveChatsList(
                                chats = uiState.activeChats,
                                onChatClick = { chatId ->
                                    val intId = when (chatId) {
                                        is Int -> chatId
                                        is String -> chatId.toIntOrNull()
                                        else -> null
                                    }
                                    if (intId != null) {
                                        onNavigateToChat(intId)
                                    } else {
                                        Log.e("MainScreen", "Invalid chat ID: $chatId")
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Show people list for other tabs
                            val people = if (uiState.showSearch && uiState.searchQuery.isNotBlank()) {
                                uiState.searchUsers
                            } else {
                                uiState.users
                            }
                            if (people.isEmpty()) {
                                EmptyStateView(
                                    message = if (uiState.showSearch && uiState.searchQuery.isNotBlank()) "No people match your search." else "No people to display."
                                )
                            } else {
                                PeopleList(
                                    people = people,
                                    onPersonClick = onNavigateToPersonDetail,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Bar: Chat and Safe Toggle with FAB
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Chat Icon with Badge
                Box {
                    IconButton(
                        onClick = {
                            val chatId = uiState.activeChats.firstOrNull()?.id
                            if (chatId != null) {
                                onNavigateToChat(chatId)
                            } else {
                                Log.e("MainScreen", "Invalid or missing chat ID: $chatId")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Messages",
                            tint = if (hasUnreadMessages || hasUnreadGroupMessages) {
                                Color(0xFF8CC55D)  // Green to match iOS
                            } else {
                                Color.Gray
                            }
                        )
                    }
                    if (hasUnreadMessages || hasUnreadGroupMessages) {
                        Badge(
                            modifier = Modifier.offset(x = 20.dp, y = (-4).dp),
                            containerColor = Color(0xFF8CC55D)
                        ) {
                            Text(text = "", fontSize = 8.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Safe Portals Toggle
                IconButton(
                    onClick = {
                        val userId = uiState.currentUser?.id ?: 0
                        viewModel.toggleSafePortals(userId)
                    }
                ) {
                    Icon(
                        imageVector = if (uiState.showOnlySafePortals) Icons.Default.Shield else Icons.Default.Public,
                        contentDescription = if (uiState.showOnlySafePortals) "Show All Portals" else "Show Safe Portals Only",
                        tint = if (uiState.showOnlySafePortals) Color(0xFF8CC55D) else Color.Gray
                    )
                }
            }

            // Rep Logo as Floating Action Button (bottom right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        val userId = uiState.currentUser?.id ?: 0
                        viewModel.togglePage(userId)
                    },
                    containerColor = Color(0xFF8CC55D)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.replogo),
                        contentDescription = "Rep Logo (Switch Portal/People)",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
    
    // NEW: Display action sheet when shown
    if (showActionSheet) {
        MainActionSheet(
            showOnlySafePortals = uiState.showOnlySafePortals,
            onToggleSafe = { 
                val userId = uiState.currentUser?.id ?: 0
                viewModel.toggleSafePortals(userId) 
            },
            onAddPurpose = { /* TODO: Navigate to add purpose */ },
            onTeamChat = { /* TODO: Navigate to team chat */ },
            onSearch = { viewModel.toggleSearch() },
            onDismiss = { showActionSheet = false }
        )
    }
}

// NEW: Enhanced Portal Item with iOS styling
@Composable
fun EnhancedPortalItem(
    portal: Portal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Portal Image with proper error handling
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                val patchedUrl = patchPortalImageUrl(portal.imageUrl)
                if (!patchedUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = patchedUrl,
                        contentDescription = portal.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = portal.name.take(1),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Portal Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = portal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (!portal.subtitle.isNullOrBlank()) {
                    Text(
                        text = portal.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (portal.description.isNotBlank()) {
                    Text(
                        text = portal.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Show leads with profile pictures/initials
                if (!portal.leads.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        portal.leads.take(3).forEach { user ->
                            UserProfileImageThumbnail(user = user, size = 24.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        
                        if ((portal.leads.size > 3)) {
                            Text(
                                text = "+${portal.leads.size - 3}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Safety indicator
            if (portal.isSafe) {
                Box(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safe",
                        tint = Color(0xFF8CC55D),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// NEW: User Profile Thumbnail helper
@Composable
fun UserProfileImageThumbnail(user: User, size: Dp = 40.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        val profileImageUrl = user.profileImageUrlCompat
        if (!profileImageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = profileImageUrl,
                contentDescription = "${user.firstName} ${user.lastName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val initials = buildString {
                user.firstName?.firstOrNull()?.let { append(it) }
                user.lastName?.firstOrNull()?.let { append(it) }
                if (isEmpty() && !user.username.isNullOrEmpty()) {
                    append(user.username.first())
                }
            }
            Text(
                text = initials.take(2).uppercase(),
                fontSize = (size.value * 0.4).sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
    }
}

// NEW: Enhanced ActiveChat Item with iOS styling
@Composable
fun EnhancedActiveChatItem(
    chat: ActiveChat,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chat.type == "direct" && chat.user != null) {
            UserProfileImageThumbnail(user = chat.user, size = 64.dp)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.user.displayName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (chat.last_message_time != null) {
                        Text(
                            text = formatTimeAgo(chat.last_message_time),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                val isUnread = chat.last_message?.read == "0" && chat.last_message.senderId != chat.user.id
                Text(
                    text = chat.last_message?.text ?: "",
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                    color = if (isUnread) Color(0xFF8CC55D) else Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else if (chat.type == "group" && chat.chat != null) {
            // Group chat avatar (circle with first 2 letters)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (chat.chat.name?.take(2) ?: "GC").uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.chat.name ?: "Group Chat",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (chat.last_message_time != null) {
                        Text(
                            text = formatTimeAgo(chat.last_message_time),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                val isUnread = chat.last_message?.read == "0" // For group chats we don't compare IDs
                Text(
                    text = chat.last_message?.text ?: "",
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                    color = if (isUnread) Color(0xFF8CC55D) else Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Helper function for time formatting
fun formatTimeAgo(isoDateString: String): String {
    try {
        val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = java.time.OffsetDateTime.parse(isoDateString, formatter)
        val now = java.time.OffsetDateTime.now()
        val duration = java.time.Duration.between(dateTime, now)
        
        return when {
            duration.toMinutes() < 1 -> "just now"
            duration.toHours() < 1 -> "${duration.toMinutes()}m ago"
            duration.toDays() < 1 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d")
                dateTime.format(formatter)
            }
        }
    } catch (e: Exception) {
        return ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalDetailScreen(
    portal: Portal,
    leads: List<User>,
    sections: List<PortalSection>,
    storyBlocks: List<PortalText>,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Goal Teams", "Story")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(portal.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            // Image carousel (first section images)
            val images = sections.flatMap { it.aFiles }
            if (images.isNotEmpty()) {
                PortalImageCarousel(images = images)
            }
            // Segmented control (tabs)
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTab) {
                0 -> {
                    // Goal Teams tab (show goals if available)
                    // For demo, just show a placeholder
                    Text("Goal Teams content goes here", modifier = Modifier.padding(16.dp))
                }
                1 -> {
                    // Story tab
                    PortalStorySectionAndroid(leads = leads, storyBlocks = storyBlocks)
                }
            }
        }
    }
}

@Composable
fun PortalImageCarousel(images: List<PortalFile>) {
    // Simple horizontal scroll of images
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        images.forEach { file ->
            val patchedUrl = patchPortalImageUrl(file.url)
            AsyncImage(
                model = patchedUrl,
                contentDescription = "Portal Image",
                modifier = Modifier
                    .padding(8.dp)
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun PortalStorySectionAndroid(leads: List<User>, storyBlocks: List<PortalText>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Leads", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            leads.forEach { user: User ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 12.dp)) {
                    val profileImageUrl = user.profileImageUrlCompat
                    // FIX: Only safe or non-null asserted calls allowed on nullable receiver
                    if (!profileImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "${user.firstName} ${user.lastName}",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Gray, CircleShape)
                        )
                    }
                    Text(
                        text = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Divider()
        // Story text blocks
        storyBlocks.filter { it.section == "story" }.forEach { block ->
            if (!block.title.isNullOrBlank()) {
                Text(block.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            if (!block.text.isNullOrBlank()) {
                Text(block.text, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun String?.firstOrNull(): String {
    return if (this.isNullOrEmpty()) {
        ""
    } else {
        this.first().toString()
    }
}

// Replace SegmentedControl with our iOS-style one
@Composable
fun SegmentedControl(
    sections: List<String>,
    selectedIndex: Int,
    onSectionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    MainSegmentedPicker(
        segments = sections,
        selectedIndex = selectedIndex,
        onSelect = onSectionSelected,
        modifier = modifier
    )
}

@Composable
fun PortalsList(
    portals: List<Portal>,
    onPortalClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(portals) { portal ->
            // Use our enhanced portal item
            EnhancedPortalItem(
                portal = portal,
                onClick = { onPortalClick(portal.id) }
            )
        }
    }
}

// NEW: Active chats list
@Composable
fun ActiveChatsList(
    chats: List<ActiveChat>,
    onChatClick: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp) // iOS style: no gaps between items
    ) {
        items(chats) { chat ->
            EnhancedActiveChatItem(
                chat = chat,
                onClick = { chat.id?.let { onChatClick(it) } }
            )
            // Add divider between items
            Divider(
                modifier = Modifier.padding(start = 80.dp),
                color = Color(0xFFE0E0E0),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun PeopleList(
    people: List<User>,
    onPersonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(people) { person ->
            PersonItem(
                person = person,
                onClick = { onPersonClick(person.id) }
            )
        }
    }
}

// Enhance PersonItem to match iOS style
@Composable
fun PersonItem(
    person: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use our thumbnail helper
            UserProfileImageThumbnail(user = person, size = 60.dp)

            Spacer(modifier = Modifier.width(16.dp))

            // Person Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${person.firstName} ${person.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (person.about?.isNotBlank() == true) {
                    Text(
                        text = person.about,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (person.city?.isNotBlank() == true) {
                    Text(
                        text = person.city,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8CC55D), // iOS green color
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun UserProfileImage(
    user: User,
    modifier: Modifier = Modifier
) {
    UserProfileImageThumbnail(user = user, size = 60.dp)
}

// Add this extension property at the end of the file (only once, not duplicated)
val User.profileImageUrlCompat: String?
    get() = try {
        this::class.members.firstOrNull { it.name == "profileImageUrl" }
            ?.call(this) as? String
            ?: this::class.members.firstOrNull { it.name == "imageUrl" }
                ?.call(this) as? String
            ?: this::class.members.firstOrNull { it.name == "avatarUrl" }
                ?.call(this) as? String
    } catch (e: Exception) {
        null
    }

// Add extension property for display name
val User.displayName: String
    get() {
        val firstName = this.firstName ?: this.fname ?: ""
        val lastName = this.lastName ?: this.lname ?: ""
        return if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            "$firstName $lastName".trim()
        } else {
            this.username ?: "User"
        }
    }