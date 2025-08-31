package com.networkedcapital.rep.presentation.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.networkedcapital.rep.domain.model.*
import com.networkedcapital.rep.presentation.main.MainViewModel.MainPage

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

    LaunchedEffect(uiState.currentUser?.id) {
        val userId = uiState.currentUser?.id ?: 0
        viewModel.loadData(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    if (uiState.showSearch) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            placeholder = {
                                Text(
                                    "Search ${if (uiState.currentPage == MainPage.PORTALS) "portals" else "people"}...",
                                    fontSize = 16.sp
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    keyboardController?.hide()
                                }
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        viewModel.toggleSearch()
                                        keyboardController?.hide()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close search",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SegmentedControl(
                                sections = listOf("OPEN", "NTWK", "ALL"),
                                selectedSection = when (uiState.selectedSection) {
                                    0 -> "OPEN"
                                    1 -> "NTWK"
                                    else -> "ALL"
                                },
                                onSectionSelected = { section ->
                                    val sectionIdx = when (section) {
                                        "OPEN" -> 0
                                        "NTWK" -> 1
                                        else -> 2
                                    }
                                    val userId = uiState.currentUser?.id ?: 0
                                    viewModel.onSectionChanged(sectionIdx, userId)
                                }
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (!uiState.showSearch) {
                        IconButton(onClick = {
                            uiState.currentUser?.id?.let { onNavigateToProfile(it) }
                        }) {
                            val profileImageUrl = uiState.currentUser?.profileImageUrlCompat
                            if (!profileImageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = profileImageUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(32.dp)
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
                },
                actions = {
                    if (!uiState.showSearch) {
                        IconButton(onClick = viewModel::toggleSearch) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = { /* TODO: Add new portal/person */ }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add"
                            )
                        }
                    }
                }
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (uiState.currentPage) {
                    MainPage.PORTALS -> {
                        PortalsList(
                            portals = if (uiState.showSearch && uiState.searchQuery.isNotBlank()) {
                                uiState.searchPortals
                            } else {
                                uiState.portals
                            },
                            onPortalClick = onNavigateToPortalDetail,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    MainPage.PEOPLE -> {
                        PeopleList(
                            people = if (uiState.showSearch && uiState.searchQuery.isNotBlank()) {
                                uiState.searchUsers
                            } else {
                                uiState.users
                            },
                            onPersonClick = onNavigateToPersonDetail,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            BottomAppBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val userId = uiState.currentUser?.id ?: 0
                            viewModel.togglePage(userId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.currentPage == MainPage.PORTALS) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (uiState.currentPage == MainPage.PORTALS) "PORTALS" else "PEOPLE",
                            color = if (uiState.currentPage == MainPage.PORTALS) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // FIX: Use robust type checking for chat id
                    IconButton(
                        onClick = {
                            val chatId = uiState.activeChats.firstOrNull()?.id
                            val intId = when (chatId) {
                                is Int -> chatId
                                is String -> chatId.toIntOrNull()
                                else -> null
                            }

                            if (intId != null) {
                                onNavigateToChat(intId)
                            } else {
                                Log.e("MainScreen", "Invalid or missing chat ID: $chatId")
                            }
                        }
                    ) {
                        Badge(
                            modifier = Modifier.offset(x = 8.dp, y = (-8).dp)
                        ) {
                            if (uiState.activeChats.isNotEmpty()) {
                                Text(
                                    text = uiState.activeChats.size.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Messages",
                            tint = if (uiState.activeChats.isNotEmpty()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    IconButton(
                        onClick = {
                            val userId = uiState.currentUser?.id ?: 0
                            viewModel.toggleSafePortals(userId)
                        }
                    ) {
                        Icon(
                            imageVector = if (uiState.showOnlySafePortals) Icons.Default.Shield else Icons.Default.Public,
                            contentDescription = if (uiState.showOnlySafePortals) "Show All Portals" else "Show Safe Portals Only"
                        )
                    }
                }
            }
        }
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
            AsyncImage(
                model = file.url,
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

@Composable
fun SegmentedControl(
    sections: List<String>,
    selectedSection: String,
    onSectionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
    ) {
        sections.forEachIndexed { idx, section ->
            val isSelected = section == selectedSection
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onSectionSelected(section) }
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = section,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
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
            PortalItem(
                portal = portal,
                // FIX: Only pass Int to onPortalClick, portal.id is Int in your model
                onClick = { onPortalClick(portal.id) }
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

// Update PortalItem to show leads and subtitle, similar to Swift
@Composable
fun PortalItem(
    portal: Portal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Portal Image using Coil
            AsyncImage(
                model = portal.imageUrl,
                contentDescription = portal.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

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
                if (portal.subtitle?.isNotBlank() == true) {
                    Text(
                        text = portal.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (portal.description.isNotBlank()) {
                    Text(
                        text = portal.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val leads: List<User> = portal.leads ?: emptyList()
                if (leads.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leads.take(3).forEach { user: User ->
                            val userProfileImageUrl = user.profileImageUrlCompat
                            // FIX: Only safe or non-null asserted calls allowed on nullable receiver
                            val firstInitial = user.firstName?.firstOrNull()?.toString()
                                ?: user.fname?.firstOrNull()?.toString()
                                ?: ""
                            val lastInitial = user.lastName?.firstOrNull()?.toString()
                                ?: user.lname?.firstOrNull()?.toString()
                                ?: ""
                            val contentDescription = "$firstInitial$lastInitial"
                            if (!userProfileImageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = userProfileImageUrl,
                                    contentDescription = contentDescription,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .padding(end = 2.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color.Gray, CircleShape)
                                        .padding(end = 2.dp)
                                )
                            }
                        }
                        if (leads.size > 3) {
                            Text("+${leads.size - 3}", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Safe indicator
            if (portal.isSafe) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Safe Portal",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val profileImageUrl = person.profileImageUrlCompat
            if (!profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "${person.firstName} ${person.lastName}",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Gray, CircleShape)
                )
            }

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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (person.city?.isNotBlank() == true) {
                    Text(
                        text = person.city,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
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
    val profileImageUrl = user.profileImageUrlCompat
    if (!profileImageUrl.isNullOrEmpty()) {
        AsyncImage(
            model = profileImageUrl,
            contentDescription = "${user.firstName} ${user.lastName}",
            modifier = modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(60.dp)
                .background(Color.Gray, CircleShape)
        )
    }
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