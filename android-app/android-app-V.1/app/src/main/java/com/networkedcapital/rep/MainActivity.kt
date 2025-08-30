package com.networkedcapital.rep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.networkedcapital.rep.presentation.theme.RepTheme
import com.networkedcapital.rep.presentation.auth.AuthViewModel
import com.networkedcapital.rep.presentation.auth.LoginScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.networkedcapital.rep.presentation.goals.GoalsNavHost
import com.networkedcapital.rep.presentation.chat.GroupChatScreen
import com.networkedcapital.rep.presentation.chat.IndividualChatScreen
import com.networkedcapital.rep.presentation.goals.GoalsViewModel
import com.networkedcapital.rep.presentation.chat.GroupChatViewModel
import com.networkedcapital.rep.presentation.chat.IndividualChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.Divider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                var isLoggedIn by remember { mutableStateOf(false) }
                if (!isLoggedIn) {
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onNavigateToSignUp = { /* TODO: Navigate to sign up screen */ },
                        onNavigateToForgotPassword = { /* TODO: Navigate to forgot password screen */ }
                    )
                } else {
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(authViewModel = authViewModel,
                                onNavigateToGoals = { navController.navigate("goals") },
                                onNavigateToGroupChat = { chatId, currentUserId ->
                                    navController.navigate("groupChat/$chatId/$currentUserId")
                                },
                                onNavigateToIndividualChat = { otherUserId, currentUserId ->
                                    navController.navigate("individualChat/$otherUserId/$currentUserId")
                                }
                            )
                        }
                        composable("goals") {
                            GoalsNavHost(navController = navController)
                        }
                        composable("groupChat/{chatId}/{currentUserId}") { backStackEntry ->
                            val chatId = backStackEntry.arguments?.getString("chatId")?.toIntOrNull() ?: 0
                            val currentUserId = backStackEntry.arguments?.getString("currentUserId")?.toIntOrNull() ?: 0
                            val viewModel = GroupChatViewModel(chatId, currentUserId)
                            GroupChatScreen(
                                groupName = "Group Name",
                                groupMembers = emptyList(),
                                messages = emptyList(),
                                currentUserId = currentUserId,
                                inputText = "",
                                onInputTextChange = {},
                                onSend = {},
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("individualChat/{otherUserId}/{currentUserId}") { backStackEntry ->
                            val otherUserId = backStackEntry.arguments?.getString("otherUserId")?.toIntOrNull() ?: 0
                            val currentUserId = backStackEntry.arguments?.getString("currentUserId")?.toIntOrNull() ?: 0
                            val viewModel = IndividualChatViewModel(otherUserId, currentUserId)
                            IndividualChatScreen(
                                userName = "User Name",
                                userPhotoUrl = "",
                                messages = emptyList(),
                                currentUserId = currentUserId,
                                inputText = "",
                                onInputTextChange = {},
                                onSend = {},
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RepTheme {
        // Preview content will be added later
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(authViewModel: AuthViewModel, onNavigateToGoals: () -> Unit, onNavigateToGroupChat: (Int, Int) -> Unit, onNavigateToIndividualChat: (Int, Int) -> Unit) {
    // State for page (portals/people), section, search, etc.
    var page by remember { mutableStateOf(MainPage.Portals) }
    var section by remember { mutableStateOf(2) }
    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showActionSheet by remember { mutableStateOf(false) }
    var showOnlySafePortals by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    // TODO: Add ViewModels for portals and people, and fetch data as in SwiftUI

    Scaffold(
        topBar = {
            MainTopBar(
                section = section,
                onSectionChange = { section = it },
                onProfileClick = { /* TODO: Navigate to profile */ },
                onPlusClick = { showActionSheet = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    page = if (page == MainPage.Portals) MainPage.People else MainPage.Portals
                    // TODO: Fetch data for new page
                }
            ) {
                Icon(Icons.Filled.SwapHoriz, contentDescription = "Switch Page")
            }
        }
    ) { innerPadding ->
        // Search bar overlay
        if (showSearch) {
            MainSearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onCancel = {
                    showSearch = false
                    searchText = ""
                    // TODO: Clear search results
                }
            )
        }
        // Main content
        when (page) {
            MainPage.Portals -> {
                // Example usage of LazyColumn for portals
                PortalsList(portals = listOf("Portal 1", "Portal 2", "Portal 3"))
            }
            MainPage.People -> {
                PeopleList(people = listOf("Alice", "Bob", "Charlie"))
            }
        }
        // Use ModalBottomSheet for the action sheet
        if (showActionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showActionSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Show: ")
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showOnlySafePortals = false
                                showActionSheet = false
                                // TODO: Fetch all portals
                                coroutineScope.launch { sheetState.hide() }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!showOnlySafePortals) Color(0xFF8CCF5D) else Color.LightGray
                            )
                        ) { Text("All") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showOnlySafePortals = true
                                showActionSheet = false
                                // TODO: Fetch safe portals
                                coroutineScope.launch { sheetState.hide() }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showOnlySafePortals) Color(0xFF8CCF5D) else Color.LightGray
                            )
                        ) { Text("Safe") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showActionSheet = false
                            // TODO: Navigate to add purpose screen
                            coroutineScope.launch { sheetState.hide() }
                        }
                    ) { Text("Add Purpose") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showActionSheet = false
                            showSearch = true
                            coroutineScope.launch { sheetState.hide() }
                        }
                    ) { Text("Search") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showActionSheet = false
                            coroutineScope.launch { sheetState.hide() }
                        }
                    ) { Text("Cancel") }
                }
            }
        }
    }
}

// --- You will need to implement MainTopBar, MainSearchBar, MainActionSheet, and the lists ---
// --- Use Compose equivalents for segmented controls, lists, sheets, etc. ---

enum class MainPage { Portals, People }

@Composable
fun MainTopBar(
    section: Int,
    onSectionChange: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    Surface(
        shadowElevation = 4.dp,
        color = Color(0xFFF9F9F9)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            MainSegmentedPicker(
                segments = listOf("OPEN", "NTWK", "ALL"),
                selectedIndex = section,
                onSelected = onSectionChange
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onPlusClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF8CCF5D),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MainSegmentedPicker(
    segments: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .width(240.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF9F9F9))
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
    ) {
        segments.forEachIndexed { idx, label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (selectedIndex == idx) Color.Black else Color.White)
                    .clickable { onSelected(idx) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selectedIndex == idx) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
            if (idx < segments.lastIndex) {
                Divider(
                    color = Color(0xFFE4E4E4),
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun MainSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        androidx.compose.material3.OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text("Search...") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onCancel) {
            Text("Cancel")
        }
    }
}

@Composable
fun MainActionSheet(
    showOnlySafePortals: Boolean,
    onShowAll: () -> Unit,
    onShowSafe: () -> Unit,
    onAddPurpose: () -> Unit,
    onSearch: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Actions") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Show: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onShowAll,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showOnlySafePortals) Color(0xFF8CCF5D) else Color.LightGray
                        )
                    ) { Text("All") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onShowSafe,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showOnlySafePortals) Color(0xFF8CCF5D) else Color.LightGray
                        )
                    ) { Text("Safe") }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddPurpose) { Text("Add Purpose") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onSearch) { Text("Search") }
            }
        },
        confirmButton = {
            Button(onClick = onCancel) { Text("Cancel") }
        }
    )
}

// Placeholder lists for demonstration

@Composable
fun PortalsList(portals: List<String>) {
    LazyColumn {
        items(portals) { portal ->
            Text(
                text = portal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Divider()
        }
    }
}

@Composable
fun PeopleList(people: List<String>) {
    LazyColumn {
        items(people) { person ->
            Text(
                text = person,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Divider()
        }
    }
}

@Composable
fun ChatsList(chats: List<String>) {
    LazyColumn {
        items(chats) { chat ->
            Text(
                text = chat,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Divider()
        }
    }
}

@Composable
fun HorizontalListDemo(items: List<String>) {
    LazyRow {
        items(items) { item ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { /* Handle item click */ }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item)
            }
        }
    }
}
      
