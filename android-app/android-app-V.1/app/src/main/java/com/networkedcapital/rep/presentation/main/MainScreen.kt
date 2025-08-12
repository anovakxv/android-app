package com.networkedcapital.rep.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.networkedcapital.rep.presentation.main.MainViewModel

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

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar with Search
            TopAppBar(
                title = {
                    if (uiState.isSearchMode) {
                        OutlinedTextField(
                            value = uiState.searchText,
                            onValueChange = viewModel::updateSearchText,
                            placeholder = { 
                                Text(
                                    "Search ${if (uiState.currentPage == MainViewModel.Page.PORTALS) "portals" else "people"}...",
                                    fontSize = 16.sp
                                ) 
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { 
                                    keyboardController?.hide()
                                    viewModel.performSearch()
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
                        // Segmented Control for OPEN/NTWK/ALL
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SegmentedControl(
                                sections = listOf("OPEN", "NTWK", "ALL"),
                                selectedSection = uiState.selectedSection,
                                onSectionSelected = viewModel::selectSection
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (!uiState.isSearchMode) {
                        IconButton(onClick = { 
                            uiState.currentUser?.id?.let { onNavigateToProfile(it) } 
                        }) {
                            if (uiState.currentUser?.profileImageUrl != null) {
                                AsyncImage(
                                    model = uiState.currentUser.profileImageUrl,
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
                    if (!uiState.isSearchMode) {
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

            // Main Content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (uiState.currentPage) {
                    MainViewModel.Page.PORTALS -> {
                        PortalsList(
                            portals = if (uiState.isSearchMode && uiState.searchText.isNotBlank()) {
                                uiState.searchResults.portals
                            } else {
                                uiState.filteredPortals
                            },
                            onPortalClick = onNavigateToPortalDetail,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    MainViewModel.Page.PEOPLE -> {
                        PeopleList(
                            people = if (uiState.isSearchMode && uiState.searchText.isNotBlank()) {
                                uiState.searchResults.people
                            } else {
                                uiState.filteredPeople
                            },
                            onPersonClick = onNavigateToPersonDetail,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Bottom Navigation
            BottomAppBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Toggle between Portals and People
                    Button(
                        onClick = { viewModel.togglePage() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.currentPage == MainViewModel.Page.PORTALS) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (uiState.currentPage == MainViewModel.Page.PORTALS) "PORTALS" else "PEOPLE",
                            color = if (uiState.currentPage == MainViewModel.Page.PORTALS) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Active Chats Button
                    IconButton(
                        onClick = { 
                            // Navigate to chat list or first active chat
                            if (uiState.activeChats.isNotEmpty()) {
                                onNavigateToChat(uiState.activeChats.first().id)
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

                    // Safe Portals Toggle
                    IconButton(
                        onClick = viewModel::toggleSafePortals
                    ) {
                        Icon(
                            imageVector = if (uiState.safePortalsOnly) Icons.Default.Shield else Icons.Default.Public,
                            contentDescription = "Safe Portals",
                            tint = if (uiState.safePortalsOnly) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
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
        sections.forEach { section ->
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
                
                if (portal.description.isNotBlank()) {
                    Text(
                        text = portal.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (portal.location.isNotBlank()) {
                    Text(
                        text = portal.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
            // Profile Image using Coil
            AsyncImage(
                model = person.profileImageUrl,
                contentDescription = "${person.firstName} ${person.lastName}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

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
