@Composable
fun EditProfileScreen(
    onProfileSaved: () -> Unit,
    viewModel: com.networkedcapital.rep.presentation.auth.AuthViewModel = hiltViewModel()
) {
    val authState = viewModel.authState.collectAsState().value
    var firstName by remember { mutableStateOf(authState.firstName ?: "") }
    var lastName by remember { mutableStateOf(authState.lastName ?: "") }
    var email by remember { mutableStateOf(authState.email ?: "") }
    var broadcast by remember { mutableStateOf(authState.broadcast ?: "") }
    var city by remember { mutableStateOf(authState.city ?: "") }
    var about by remember { mutableStateOf(authState.about ?: "") }
    var otherSkill by remember { mutableStateOf(authState.otherSkill ?: "") }
    var repType by remember { mutableStateOf(authState.repType ?: RepType.LEAD) }
    var selectedSkills by remember { mutableStateOf(authState.skills ?: emptySet<RepSkill>()) }
    var profileImageUri by remember { mutableStateOf(authState.profileImageUri) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        profileImageUri = uri
    }
    val isLoading = authState.isLoading
    val errorMessage = authState.errorMessage
    val saveError = authState.saveError

    Surface(
        color = com.networkedcapital.rep.presentation.theme.RepBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: handle cancel/back */ }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropUp,
                        contentDescription = "Back",
                        tint = com.networkedcapital.rep.presentation.theme.RepGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        viewModel.saveProfile(
                            firstName,
                            lastName,
                            email,
                            broadcast,
                            repType,
                            city,
                            about,
                            otherSkill,
                            selectedSkills,
                            profileImageUri
                        )
                        onProfileSaved()
                    },
                    enabled = !isLoading,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        "Save",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = com.networkedcapital.rep.presentation.theme.RepGreen
                    )
                }
            }
            // Profile image with edit overlay
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .padding(top = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (profileImageUri != null) {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(108.dp)
                            .background(Color(0xFFF7F7F7), shape = RoundedCornerShape(54.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .background(Color(0xFFF7F7F7), shape = RoundedCornerShape(54.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "No Profile Image",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .offset(x = (-10).dp, y = 10.dp)
                        .height(32.dp)
                ) {
                    Text("+Edit\nPhoto", fontSize = 12.sp)
                }
            }
            // Info fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    StyledProfileTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "First Name",
                        modifier = Modifier.weight(1f)
                    )
                    StyledProfileTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Last Name",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                StyledProfileTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                StyledProfileTextField(
                    value = broadcast,
                    onValueChange = { broadcast = it },
                    placeholder = "Broadcast (optional)",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Rep Type Picker
                // ...existing code for dropdown...
                StyledProfileTextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "City",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                StyledProfileTextField(
                    value = about,
                    onValueChange = { about = it },
                    placeholder = "About",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                StyledProfileTextField(
                    value = otherSkill,
                    onValueChange = { otherSkill = it },
                    placeholder = "Other Skill",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = com.networkedcapital.rep.presentation.theme.RepGreen)
                Column {
                    viewModel.allSkills.forEach { skill ->
                        MultipleSelectionRow(
                            skill = skill,
                            isSelected = selectedSkills.contains(skill),
                            onClick = {
                                selectedSkills = if (selectedSkills.contains(skill)) selectedSkills - skill else selectedSkills + skill
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator()
            }
            if (errorMessage != null) {
                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
            if (saveError != null) {
                Text(text = saveError ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
package com.networkedcapital.rep.presentation.onboarding
import com.networkedcapital.rep.model.RepType
import com.networkedcapital.rep.model.RepSkill
import com.networkedcapital.rep.api.fetchSkills
import kotlinx.coroutines.launch

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border

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
            horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                    Text(
                        text = """
Terms of Use:  
Version: 1.1
Effective Date: 7/30/2025
App Name: Rep 1
Developer: Networked Capital Inc.

Welcome to Rep. By continuing, you agree to the following community guidelines and terms:

1. Community Standards
Users must not post objectionable, offensive, or abusive content.
Hate speech, harassment, and explicit material are strictly prohibited.
Violators may have their content removed and accounts suspended or banned.

2. User Responsibilities
You are solely responsible for the content you share, create, or promote.
Impersonation, deception, or targeted harassment is not tolerated.

3. Moderation & Enforcement
Rep reserves the right to monitor, moderate, and remove content at its discretion.
Inappropriate content can be flagged by users and reviewed by our team.
Users can block others to prevent unwanted or abusive interactions.

4. Removal of Objectionable Content    
We will review flagged content and remove content that violates this policy within 24 hours of the content being flagged. Users who repeatedly violate our content policy will be ejected from the platform. 

5. Intellectual Property
Rep and its underlying software, design, content, trademarks, logos, and features are the exclusive property of Networked Capital Inc., unless otherwise noted.
Users are not permitted to modify, reverse-engineer, reproduce, distribute, or exploit any part of the app or its codebase without prior written consent.
All feedback, suggestions, or feature ideas submitted by users may be used by Networked Capital Inc. to improve Rep, with no obligation of compensation unless explicitly agreed upon.
Unauthorized use of Rep’s intellectual property may result in termination of service and legal action.

6. Future Licensing
Certain components of Rep may, in the future, be released under an open-source license. However, until such licensing is explicitly announced and documented by Networked Capital Inc., all elements of the Rep software remain proprietary and fully protected under applicable intellectual property laws. Future licensing decisions will not retroactively affect ownership rights, nor shall they be construed as a waiver of any current protections.

7. Agreement
By using Rep, you acknowledge and agree to uphold these standards.
For questions, contact us via our website at:  https://networkedcapital.co/contact/

Before proceeding, you must confirm acceptance of these terms.
""",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = com.networkedcapital.rep.presentation.theme.RepBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Log.d("TermsOfUseScreen", "Accept Terms button clicked")
                        viewModel.acceptTermsOfUse()
                        Log.d("TermsOfUseScreen", "Calling onAccept callback")
                        onAccept()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = com.networkedcapital.rep.presentation.theme.RepGreen)
                ) {
                    Text("Accept Terms of Use", color = MaterialTheme.colorScheme.onPrimary)
                }
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
                    text = "Rep is a platform for representatives to connect, set goals, and collaborate.\n\nRep empowers you to build your network, achieve your goals, and work together with other reps in a secure, supportive environment.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
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

// Top-level composable definitions for use in EditProfileScreen and elsewhere
@Composable
fun StyledProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = Color(0xFF59595F),
                fontSize = 16.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )
        },
        singleLine = true,
        modifier = modifier
            .background(Color(0xFFF7F7F7), RoundedCornerShape(6.dp)),
        shape = RoundedCornerShape(6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = com.networkedcapital.rep.presentation.theme.RepGreen,
            unfocusedBorderColor = com.networkedcapital.rep.presentation.theme.RepGreen,
            cursorColor = Color.Black,
            focusedLabelColor = com.networkedcapital.rep.presentation.theme.RepGreen
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
        )
    )
}

@Composable
fun MultipleSelectionRow(skill: RepSkill, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            skill.name,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = com.networkedcapital.rep.presentation.theme.RepGreen,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color.White, shape = RoundedCornerShape(14.dp))
                .border(
                    width = 2.dp,
                    color = if (isSelected) com.networkedcapital.rep.presentation.theme.RepGreen else Color.Gray,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(com.networkedcapital.rep.presentation.theme.RepGreen, shape = RoundedCornerShape(9.dp))
                )
            }
        }
    }
}
// ...existing code...

@Composable
fun OnboardingFlowEntry(
    navController: androidx.navigation.NavHostController,
    viewModel: com.networkedcapital.rep.presentation.auth.AuthViewModel = hiltViewModel()
) {
    // Remove local step-based navigation, rely on RepNavigation
    // ...existing code...
}

@Composable
fun AppWalkthroughScreen(onFinish: () -> Unit) {
    // UI for 4-5 walkthrough screens, similar to Swift's AppWalkthroughView
    // Use a pager (e.g., Accompanist Pager) for swipeable screens
    // For brevity, show a single screen and a finish button
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Rep – our Purpose-Driven movement.", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Rep helps you champion your priorities—like a world-class sales rep. Let's accelerate.", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Finish Walkthrough", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
