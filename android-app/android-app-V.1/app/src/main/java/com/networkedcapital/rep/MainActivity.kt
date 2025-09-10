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

import androidx.compose.foundation.layout.*

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

// ...existing code...

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


