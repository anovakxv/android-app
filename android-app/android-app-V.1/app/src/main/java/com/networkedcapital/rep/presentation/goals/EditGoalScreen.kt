package com.networkedcapital.rep.presentation.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.networkedcapital.rep.domain.model.Goal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalScreen(
    existingGoal: Goal? = null,
    reportingIncrements: List<String> = listOf("Daily", "Weekly", "Monthly"),
    onSave: (Goal) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(existingGoal?.title ?: "") }
    var subtitle by remember { mutableStateOf(existingGoal?.subtitle ?: "") }
    var description by remember { mutableStateOf(existingGoal?.description ?: "") }
    var quota by remember { mutableStateOf(existingGoal?.quota?.toString() ?: "") }
    var goalType by remember { mutableStateOf(existingGoal?.typeName ?: "Recruiting") }
    var metric by remember { mutableStateOf(existingGoal?.metricName ?: "") }
    var reportingIncrement by remember { mutableStateOf(existingGoal?.reportingName ?: reportingIncrements.first()) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val goalTypes = listOf("Recruiting", "Sales", "Fund", "Marketing", "Hours", "Other")
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingGoal != null) "Edit Goal" else "Add Goal") },
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
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Subtitle") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {}
            ) {
                OutlinedTextField(
                    value = goalType,
                    onValueChange = { goalType = it },
                    label = { Text("Goal Type") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) }
                )
                DropdownMenu(
                    expanded = false,
                    onDismissRequest = {}
                ) {
                    goalTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { goalType = type }
                        )
                    }
                }
            }
            if (goalType == "Other") {
                OutlinedTextField(
                    value = metric,
                    onValueChange = { metric = it },
                    label = { Text("Metric") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = quota,
                onValueChange = { quota = it },
                label = { Text("Quota") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {}
            ) {
                OutlinedTextField(
                    value = reportingIncrement,
                    onValueChange = { reportingIncrement = it },
                    label = { Text("Reporting Increment") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) }
                )
                DropdownMenu(
                    expanded = false,
                    onDismissRequest = {}
                ) {
                    reportingIncrements.forEach { inc ->
                        DropdownMenuItem(
                            text = { Text(inc) },
                            onClick = { reportingIncrement = inc }
                        )
                    }
                }
            }
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
                                Goal(
                                    id = existingGoal?.id ?: 0,
                                    title = title,
                                    subtitle = subtitle,
                                    description = description,
                                    quota = quota.toDoubleOrNull() ?: 0.0,
                                    progress = 0.0,
                                    progressPercent = 0.0,
                                    target = 0.0,
                                    achieved = 0.0,
                                    typeName = goalType,
                                    metricName = metric,
                                    reportingName = reportingIncrement,
                                    createdAt = "",
                                    updatedAt = "",
                                    chartData = emptyList(),
                                    portalsId = 0,
                                    portalName = null
                                )
                            )
                        }
                    },
                    enabled = !isSaving && title.isNotBlank() && quota.isNotBlank()
                ) {
                    Text(if (existingGoal != null) "Save Changes" else "Add Goal")
                }
            }
        }
    }
}
