package com.networkedcapital.rep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

import com.networkedcapital.rep.presentation.auth.LoginScreen
import com.networkedcapital.rep.presentation.auth.RegisterScreen
import com.networkedcapital.rep.presentation.onboarding.EditProfileScreen
import com.networkedcapital.rep.presentation.onboarding.TermsOfUseScreen
import com.networkedcapital.rep.presentation.onboarding.AboutRepScreen
import com.networkedcapital.rep.presentation.main.MainScreen
// Removed unresolved import for OnboardingScreen

@androidx.compose.material3.ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Surface(color = Color.White) {
                com.networkedcapital.rep.presentation.navigation.RepNavigation(
                    navController = navController,
                    authViewModel = com.networkedcapital.rep.presentation.auth.AuthViewModel()
                )
            }
        }
    }
}

/* ---------------------- COMPONENTS ---------------------- */

// Segmented Picker
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

// Search Bar
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
        OutlinedTextField(
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

// Top Bar
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
}

// Action Sheet
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

// Lists
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
