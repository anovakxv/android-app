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
import com.networkedcapital.rep.presentation.auth.RegisterScreen
import com.networkedcapital.rep.presentation.main.MainScreen
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
import androidx.compose.material3.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
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
                // Navigation graph
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToSignUp = { navController.navigate("register") },
                            onNavigateToForgotPassword = { /* TODO: Navigate to forgot password screen */ }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onNavigateToLogin = { navController.popBackStack("login", inclusive = false) },
                            onRegistrationSuccess = { navController.navigate("editProfile") }
                        )
                    }
                    composable("editProfile") {
                        EditProfileScreen(onNext = { navController.navigate("terms") })
                    }
                    composable("terms") {
                        TermsScreen(onNext = { navController.navigate("about") })
                    }
                    composable("about") {
                        AboutScreen(onFinish = { navController.navigate("main") })
                    }

// --- Edit Profile Screen ---
@Composable
fun EditProfileScreen(onNext: () -> Unit) {
    // Mimics iOS EditProfileView: profile image, name fields, type, city, skills, save button
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier.size(108.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(80.dp), tint = Color.Gray)
            }
            Button(onClick = { /* TODO: Add photo picker */ }, modifier = Modifier.offset(x = (-10).dp, y = 10.dp)) {
                Text("+Edit\nPhoto")
            }
        }
        Spacer(Modifier.height(16.dp))
        // Name fields
        Row {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("First Name") }, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Last Name") }, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Broadcast (optional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        // Rep Type Picker
        OutlinedTextField(value = "Lead", onValueChange = {}, label = { Text("Rep Type") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        // City
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("City (optional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        // Skills (multi-select)
        Text("Select up to 3 Skills", fontWeight = FontWeight.Bold)
        Row {
            // Placeholder skill chips
            Button(onClick = {}, modifier = Modifier.padding(end = 4.dp)) { Text("Skill 1") }
            Button(onClick = {}, modifier = Modifier.padding(end = 4.dp)) { Text("Skill 2") }
            Button(onClick = {}) { Text("Skill 3") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Other Skill (optional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Save & Continue") }
    }
}

// --- Terms Screen ---
@Composable
fun TermsScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Terms of Use", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF5F5F5)).padding(8.dp)) {
            // Scrollable terms text
            val termsText = "Terms of Use: Version 1.1\nEffective Date: 7/30/2025\nApp Name: Rep 1\nDeveloper: Networked Capital Inc.\n...\nBefore proceeding, you must confirm acceptance of these terms."
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(termsText)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Accept Terms of Use") }
    }
}

// --- About Screen (Welcome) ---
@Composable
fun AboutScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        // Profile Image
        Box(
            modifier = Modifier.size(108.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(80.dp), tint = Color.Gray)
        }
        Spacer(Modifier.height(16.dp))
        Text("Hi, User!\n\nWe’re here to help you become your best self.\n\nWe do this by leveraging the people in your life who care about you, +AI.\n\nStart by viewing the list of ‘Purposes’ and joining a Team.\n\nOr, search for someone you know and see what Goal Teams they’re on!", fontSize = 18.sp, lineHeight = 24.sp)
        Spacer(Modifier.weight(1f))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Find Goal Team to Join", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(Modifier.height(32.dp))
    }
}
                    composable("main") {
                        // This is where your main screen content with Scaffold should go
                        MainScreenContent(navController = navController)
                    }
                    composable("editProfile") {
                        // Your EditProfileScreen Composable
                        // EditProfileScreen(onProfileSaved = { navController.navigate("main") })
                        Text("Edit Profile Screen Placeholder") // Placeholder
                    }
                }
            }
        }
    }
}

enum class MainPage { Portals, People }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(navController: NavHostController) {
    var page by remember { mutableStateOf(MainPage.Portals) }
    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showActionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showOnlySafePortals by remember { mutableStateOf(false) }
    var currentSection by remember { mutableStateOf(0) } // For MainTopBar

    Scaffold(
        topBar = {
            if (!showSearch) { // Only show top bar if not searching
                MainTopBar(
                    section = currentSection,
                    onSectionChange = { currentSection = it /* TODO: Handle section change logic */ },
                    onProfileClick = { /* TODO: Navigate to profile */ },
                    onPlusClick = { showActionSheet = true }
                )
            }
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
        Column(modifier = Modifier.padding(innerPadding)) { // Apply innerPadding
            // Search bar overlay (conditional rendering)
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

            // Main content based on the 'page' state
            when (page) {
                MainPage.Portals -> {
                    // Example usage of LazyColumn for portals
                    PortalsList(portals = listOf("Portal 1", "Portal 2", "Portal 3")) // Replace with actual data
                }
                MainPage.People -> {
                    PeopleList(people = listOf("Alice", "Bob", "Charlie")) // Replace with actual data
                }
            }
        }

        // ModalBottomSheet for the action sheet
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
                                // showActionSheet = false // Hide sheet after selection
                                // TODO: Fetch all portals
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showActionSheet = false // Ensure it's hidden after animation
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!showOnlySafePortals) Color(0xFF8CCF5D) else Color.LightGray
                            )
                        ) { Text("All") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showOnlySafePortals = true
                                // showActionSheet = false
                                // TODO: Fetch safe portals
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showActionSheet = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showOnlySafePortals) Color(0xFF8CCF5D) else Color.LightGray
                            )
                        ) { Text("Safe") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // showActionSheet = false
                            // TODO: Navigate to add purpose screen
                            coroutineScope.launch {
                                sheetState.hide()
                                showActionSheet = false
                                // navController.navigate("addPurpose") // Example navigation
                            }
                        }
                    ) { Text("Add Purpose") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            // showActionSheet = false
                            showSearch = true // Show search bar
                            coroutineScope.launch {
                                sheetState.hide()
                                showActionSheet = false
                            }
                        }
                    ) { Text("Search") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button( // Cancel button for the modal sheet
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                showActionSheet = false
                            }
                        }
                    ) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
fun MainTopBar(
    section: Int,
    onSectionChange: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    Surface( // TopAppBar is often better here, or just a Row with elevation
        shadowElevation = 4.dp,
        color = Color(0xFFF9F9F9) // Or MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp) // Consider height(56.dp) for standard app bar height
                .height(56.dp), // Typical TopAppBar height
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(28.dp)
                )
            }
            // Place MainSegmentedPicker in the center
            MainSegmentedPicker(
                segments = listOf("OPEN", "NTWK", "ALL"),
                selectedIndex = section,
                onSelected = onSectionChange
            )
            IconButton(onClick = onPlusClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF8CCF5D), // Consider MaterialTheme.colorScheme.primary
                    modifier = Modifier.size(24.dp) // Standard icon size
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
            .width(240.dp) // Consider making this more flexible or use weights
            .height(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF9F9F9)) // Or MaterialTheme.colorScheme.surfaceVariant
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)) // Or MaterialTheme.colorScheme.outline
    ) {
        segments.forEachIndexed { idx, label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (selectedIndex == idx) Color.Black else Color.White) // Or MaterialTheme.colorScheme.primary / surface
                    .clickable { onSelected(idx) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selectedIndex == idx) Color.White else Color.Black, // Or MaterialTheme.colorScheme.onPrimary / onSurface
                    fontSize = 14.sp
                )
            }
            if (idx < segments.lastIndex) {
                Divider(
                    color = Color(0xFFE4E4E4), // Or MaterialTheme.colorScheme.outlineVariant
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
            .background(Color.White) // Or MaterialTheme.colorScheme.surface
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search, // Ensure this icon is available
            contentDescription = "Search"
        )
        // Add a TextField for search input here
        // TextField(value = searchText, onValueChange = onSearchTextChange, ...)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Search Bar Placeholder...", modifier = Modifier.weight(1f)) // Placeholder for TextField
        Button(onClick = onCancel) { // Simple cancel button
            Text("Cancel")
        }
    }
}

// Placeholder composables for PortalsList and PeopleList
@Composable
fun PortalsList(portals: List<String>) {
    LazyColumn {
        items(portals) { portal ->
            Text(portal, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun PeopleList(people: List<String>) {
    LazyColumn {
        items(people) { person ->
            Text(person, modifier = Modifier.padding(16.dp))
        }
    }
}

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
                // Navigation graph
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToSignUp = { navController.navigate("register") },
                            onNavigateToForgotPassword = { /* TODO: Navigate to forgot password screen */ }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onNavigateToLogin = { navController.popBackStack("login", inclusive = false) },
                            onRegistrationSuccess = { navController.navigate("editProfile") }
                        )
                    }
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
        Surface(
            shadowElevation = 4.dp,
            color = Color(0xFFF9F9F9)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(28.dp)
                    )
                }
                // Place MainSegmentedPicker in the center
                MainSegmentedPicker(
                    segments = listOf("OPEN", "NTWK", "ALL"),
                    selectedIndex = section,
                    onSelected = onSectionChange
                )
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

}


