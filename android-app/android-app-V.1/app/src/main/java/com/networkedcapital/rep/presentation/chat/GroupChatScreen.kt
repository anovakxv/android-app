package com.networkedcapital.rep.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.networkedcapital.rep.domain.model.GroupMessage
import com.networkedcapital.rep.domain.model.GroupMember

@Composable
fun GroupChatScreen(
    groupName: String,
    groupMembers: List<GroupMember>,
    messages: List<GroupMessage>,
    currentUserId: Int,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top Bar
        TopAppBar(
            title = { Text(groupName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        // Group Members Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF6F6F6))
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(groupMembers) { member ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = member.photoUrl,
                        contentDescription = member.name,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f))
                    )
                    Text(member.name, fontSize = MaterialTheme.typography.labelSmall.fontSize, maxLines = 1)
                }
            }
        }
        Divider()
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
                        AsyncImage(
                            model = msg.senderPhotoUrl,
                            contentDescription = msg.senderName,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.3f))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Column(horizontalAlignment = if (msg.senderId == currentUserId) Alignment.End else Alignment.Start) {
                        if (msg.senderId != currentUserId) {
                            Text(msg.senderName, fontSize = MaterialTheme.typography.labelSmall.fontSize, color = Color.Gray)
                        }
                        Surface(
                            color = if (msg.senderId == currentUserId) Color(0xFF00C853) else Color(0xFFF2F2F2),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                msg.text,
                                modifier = Modifier.padding(10.dp),
                                color = if (msg.senderId == currentUserId) Color.White else Color.Black
                            )
                        }
                        Text(msg.timestamp, fontSize = MaterialTheme.typography.labelSmall.fontSize, color = Color.Gray)
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
