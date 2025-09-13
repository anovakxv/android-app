package com.networkedcapital.rep.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfUseScreen(
    onAccept: () -> Unit,
    viewModel: com.networkedcapital.rep.presentation.auth.AuthViewModel = hiltViewModel()
) {
    Surface(
        color = com.networkedcapital.rep.presentation.theme.RepBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Terms of Use",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = com.networkedcapital.rep.presentation.theme.RepGreen
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = com.networkedcapital.rep.presentation.theme.RepLightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Please read and accept the terms to continue.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    viewModel.acceptTermsOfUse()
                    onAccept()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = com.networkedcapital.rep.presentation.theme.RepGreen)
            ) {
                Text("Accept", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun AboutRepScreen(
    onContinue: () -> Unit,
    viewModel: com.networkedcapital.rep.presentation.auth.AuthViewModel = hiltViewModel()
) {
    Surface(
        color = com.networkedcapital.rep.presentation.theme.RepBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "About Rep",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = com.networkedcapital.rep.presentation.theme.RepGreen
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = com.networkedcapital.rep.presentation.theme.RepLightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Rep is a platform for representatives to connect, set goals, and collaborate.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    viewModel.continueAboutRep()
                    onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = com.networkedcapital.rep.presentation.theme.RepGreen)
            ) {
                Text("Continue", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    onProfileSaved: () -> Unit,
    viewModel: com.networkedcapital.rep.presentation.auth.AuthViewModel = hiltViewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var broadcast by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }
    var otherSkill by remember { mutableStateOf("") }
    var repType by remember { mutableStateOf("Lead") }
    val repTypes = listOf("Lead", "Specialist", "Partner", "Founder")
    // Skills picker
    val allSkills = listOf("Leadership", "Sales", "Marketing", "Fundraising", "Networking", "Other") // Replace with backend fetch if needed
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    // Profile image upload
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImageUri = uri
    }
    val authState = viewModel.authState.collectAsState().value
    val isLoading = authState.isLoading
    val errorMessage = authState.errorMessage
    Surface(
        color = com.networkedcapital.rep.presentation.theme.RepBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = com.networkedcapital.rep.presentation.theme.RepGreen
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = com.networkedcapital.rep.presentation.theme.RepLightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = broadcast,
                        onValueChange = { broadcast = it },
                        label = { Text("Broadcast (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Rep Type Picker
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {}
                    ) {
                        OutlinedTextField(
                            value = repType,
                            onValueChange = {},
                            label = { Text("Rep Type") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = false,
                            onDismissRequest = {}
                        ) {
                            repTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = { repType = type }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = about,
                        onValueChange = { about = it },
                        label = { Text("About") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otherSkill,
                        onValueChange = { otherSkill = it },
                        label = { Text("Other Skill") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Skills", style = MaterialTheme.typography.titleMedium)
                    Column {
                        allSkills.forEach { skill ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedSkills.contains(skill),
                                    onCheckedChange = { checked ->
                                        selectedSkills = if (checked) selectedSkills + skill else selectedSkills - skill
                                    }
                                )
                                Text(skill)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Profile Image", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (profileImageUri != null) "Change Image" else "Upload Image")
                    }
                    if (profileImageUri != null) {
                        // Show image preview using Coil
                        androidx.compose.foundation.Image(
                            painter = coil.compose.rememberAsyncImagePainter(profileImageUri),
                            contentDescription = "Profile Image",
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            if (isLoading) {
                CircularProgressIndicator()
            }
            if (errorMessage != null) {
                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = {
                    viewModel.saveProfile(
                        firstName, lastName, email, broadcast, repType, city, about, otherSkill,
                        selectedSkills,
                        profileImageUri?.toString()
                    )
                    onProfileSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = com.networkedcapital.rep.presentation.theme.RepGreen)
            ) {
                Text("Save Profile", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
