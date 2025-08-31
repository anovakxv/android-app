package com.networkedcapital.rep.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

data class SimpleMessage(
    val id: Int,
    val senderId: Int,
    val text: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    userName: String,
    userPhotoUrl: String?,
    messages: List<SimpleMessage>,
    currentUserId: Int,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top Bar
        TopAppBar(
            title = { Text(userName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        // Messages
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 8.dp),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.senderId == currentUserId) Arrangement.End else Arrangement.Start
                ) {
                    if (msg.senderId != currentUserId) {
                        if (userPhotoUrl != null) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = userName,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.3f))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Column(horizontalAlignment = if (msg.senderId == currentUserId) Alignment.End else Alignment.Start) {
                        Surface(
                            color = if (msg.senderId == currentUserId) Color(0xFF222222) else Color(0xFFF2F2F2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                msg.text,
                                modifier = Modifier.padding(10.dp),
                                color = if (msg.senderId == currentUserId) Color(0xFF00C853) else Color.Black
                            )
                        }
                        Text(
                            msg.timestamp,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        // Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSend,
                enabled = inputText.trim().isNotEmpty()
            ) {
                Text("Send")
            }
        }
    }
}
