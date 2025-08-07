package com.networkedcapital.rep.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProfile: (Int) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("OPEN", "NTWK", "ALL")
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar with profile icon and plus icon
        TopAppBar(
            title = {
                // Segmented control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SegmentedControl(
                        items = tabs,
                        selectedIndex = selectedTab,
                        onSelectionChanged = { selectedTab = it }
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { onNavigateToProfile(1) }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile"
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* TODO: Add action */ }) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
            }
        )
        
        // Main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome to Rep!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start connecting with other representatives and building your network.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Placeholder for portals/people content
            items(5) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("IMG")
                                }
                            }
                        }
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Sample Item ${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Sample description for item ${index + 1}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        // Bottom bar with toggle and message buttons
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* TODO: Toggle between portals/people */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Toggle View")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                IconButton(onClick = { /* TODO: Messages */ }) {
                    Text("💬")
                }
            }
        }
    }
}

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            Button(
                onClick = { onSelectionChanged(index) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedIndex == index) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item,
                    color = if (selectedIndex == index) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
