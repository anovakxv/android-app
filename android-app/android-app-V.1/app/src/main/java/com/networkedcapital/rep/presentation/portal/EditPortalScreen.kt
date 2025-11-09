package com.networkedcapital.rep.presentation.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.networkedcapital.rep.domain.model.Portal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPortalScreen(
    existingPortal: Portal? = null,
    onSave: (Portal) -> Unit,
    onCancel: () -> Unit,
    viewModel: EditPortalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf(existingPortal?.name ?: "") }
    var subtitle by remember { mutableStateOf(existingPortal?.subtitle ?: "") }
    var about by remember { mutableStateOf(existingPortal?.description ?: "") }
    val scope = rememberCoroutineScope()

    val isEdit = existingPortal != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Portal") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel")
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
            if (uiState.errorMessage != null) {
                Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (isEdit && existingPortal != null) {
                            // Update existing portal
                            viewModel.updatePortal(
                                portalId = existingPortal.id.toString(),
                                name = name,
                                description = about,
                                category = "General",
                                location = "",
                                isPrivate = false,
                                onSuccess = {
                                    onSave(
                                        Portal(
                                            id = existingPortal.id,
                                            name = name,
                                            subtitle = subtitle,
                                            description = about,
                                            imageUrl = existingPortal.imageUrl,
                                            location = existingPortal.location,
                                            isSafe = existingPortal.isSafe
                                        )
                                    )
                                }
                            )
                        } else {
                            // Create new portal
                            viewModel.createPortal(
                                name = name,
                                description = about,
                                category = "General",
                                location = "",
                                isPrivate = false,
                                onSuccess = {
                                    onSave(
                                        Portal(
                                            id = 0,
                                            name = name,
                                            subtitle = subtitle,
                                            description = about
                                        )
                                    )
                                }
                            )
                        }
                    },
                    enabled = !uiState.isSaving && name.isNotBlank()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isEdit) "Update Portal" else "Create Portal")
                    }
                }
            }
        }
    }
}
