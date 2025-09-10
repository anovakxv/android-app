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
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
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
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
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
                    viewModel.saveProfile(name, email)
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
