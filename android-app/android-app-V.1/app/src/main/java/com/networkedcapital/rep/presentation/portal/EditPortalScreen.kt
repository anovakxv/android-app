package com.networkedcapital.rep.presentation.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.networkedcapital.rep.domain.model.Portal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPortalScreen(
    existingPortal: Portal? = null,
    onSave: (Portal) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(existingPortal?.name ?: "") }
    var subtitle by remember { mutableStateOf(existingPortal?.subtitle ?: "") }
    var about by remember { mutableStateOf(existingPortal?.description ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Portal") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Portal Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Subtitle") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("About") },
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        isSaving = true
                        scope.launch {
                            // TODO: Save to API
                            onSave(
                                Portal(
                                    id = existingPortal?.id ?: 0,
                                    name = name,
                                    subtitle = subtitle,
                                    description = about,
                                    imageUrl = existingPortal?.imageUrl ?: "",
                                    location = existingPortal?.location ?: "",
                                    isSafe = existingPortal?.isSafe ?: false
                                    // Remove 'leads' parameter, as it does not exist in your Portal constructor
                                )
                            )
                        }
                    },
                    enabled = !isSaving && name.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}
