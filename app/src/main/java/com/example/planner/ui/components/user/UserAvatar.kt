package com.example.planner.ui.components.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.planner.domain.model.User

@Composable
fun UserAvatar(
    user: User,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(user.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    if (user.profilePictureUri != null) {
        AsyncImage(
            model = user.profilePictureUri,
            contentDescription = "${user.name}'s profile picture",
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.firstOrNull()?.uppercase() ?: "?",
                style = when {
                    size >= 64.dp -> MaterialTheme.typography.headlineMedium
                    size >= 48.dp -> MaterialTheme.typography.titleLarge
                    else -> MaterialTheme.typography.titleMedium
                },
                color = Color.White
            )
        }
    }
}

@Composable
fun UserAvatarWithName(
    user: User,
    avatarSize: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = user, size = avatarSize)
        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(12.dp)
        )
        androidx.compose.foundation.layout.Column {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = user.role.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
