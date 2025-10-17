import androidx.compose.ui.tooling.preview.Preview
@Preview(showBackground = true)
@Composable
fun PreviewPortalItem() {
    val portal = Portal(
        id = 1,
        name = "Sample Portal",
        subtitle = "A subtitle for the portal",
        description = "This is a sample portal used for previewing the PortalItem UI.",
        imageUrl = null,
        leads = listOf(
            User(id = 1, firstName = "Alice", lastName = "Smith", profileImageUrl = null),
            User(id = 2, firstName = "Bob", lastName = "Jones", profileImageUrl = null)
        ),
        isSafe = true
    )
    PortalItem(portal = portal, onClick = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewPersonItem() {
    val user = User(
        id = 1,
        firstName = "Jane",
        lastName = "Doe",
        about = "A passionate developer from NYC.",
        city = "New York",
        profileImageUrl = null
    )
    PersonItem(person = user, onClick = {})
}
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
package com.networkedcapital.rep.presentation.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.networkedcapital.rep.R
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
import com.networkedcapital.rep.domain.model.User
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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // Top Bar: Profile, Segmented Picker, Search
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

            Spacer(modifier = Modifier.width(12.dp))

            // Search icon (center)
            IconButton(onClick = viewModel::toggleSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Add button (right)
            IconButton(onClick = { /* TODO: Add new portal/person */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
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

        // Bottom Bar: Chat and Safe Toggle only (no page switch)
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
                    if (uiState.activeChats.isNotEmpty()) {
                        Badge(
                            modifier = Modifier.offset(x = 20.dp, y = (-4).dp)
                        ) {
                            Text(text = "${uiState.activeChats.size}", fontSize = 10.sp)
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
                        contentDescription = if (uiState.showOnlySafePortals) "Show All Portals" else "Show Safe Portals Only"
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
                    containerColor = MaterialTheme.colorScheme.primary
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

@Composable
fun SegmentedControl(
    sections: List<String>,
    selectedIndex: Int,
    onSectionSelected: (Int) -> Unit,
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
            val isSelected = idx == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onSectionSelected(idx) }
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
            val patchedUrl = patchPortalImageUrl(portal.imageUrl)
            AsyncImage(
                model = patchedUrl,
                contentDescription = "Main image for portal: ${portal.name}",
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
                            val contentDescription = "Profile image for $firstInitial $lastInitial"
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
                                        .padding(end = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$firstInitial$lastInitial",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier
                                            .padding(2.dp)
                                    )
                                }
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
                    contentDescription = "Profile image for ${person.firstName} ${person.lastName}",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${person.firstName.firstOrNull() ?: ""}${person.lastName.firstOrNull() ?: ""}",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(2.dp)
                    )
                }
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