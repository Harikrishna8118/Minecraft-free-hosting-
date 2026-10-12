package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MinecraftEdition
import com.example.model.ServerStatus
import com.example.ui.theme.*

@Composable
fun ServerStatusBadge(status: ServerStatus, modifier: Modifier = Modifier) {
    val (bgColor, dotColor, textColor) = when (status) {
        ServerStatus.ONLINE -> Triple(Color(0xFF064E3B), EmeraldLight, Color(0xFFA7F3D0))
        ServerStatus.STARTING, ServerStatus.RECOVERING -> Triple(Color(0xFF451A03), AmberAccent, Color(0xFFFDE68A))
        ServerStatus.STOPPING -> Triple(Color(0xFF3F3F46), Color(0xFFA1A1AA), Color(0xFFE4E4E7))
        ServerStatus.OFFLINE -> Triple(Color(0xFF1E293B), Color(0xFF64748B), Color(0xFF94A3B8))
        ServerStatus.CRASHED -> Triple(Color(0xFF450A0A), RedstoneRed, Color(0xFFFECACA))
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, dotColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = status.displayName,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EditionBadge(edition: MinecraftEdition, modifier: Modifier = Modifier) {
    val (label, bg, stroke, text) = when (edition) {
        MinecraftEdition.JAVA -> Quad("JAVA", Color(0xFF143026), EmeraldPrimary, Color(0xFF6EE7B7))
        MinecraftEdition.BEDROCK -> Quad("BEDROCK", Color(0xFF1E2B3E), CyanSecondary, Color(0xFF7DD3FC))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, stroke.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun AlwaysOnlineTag(isAlwaysOnline: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isAlwaysOnline) Color(0xFF064E3B).copy(alpha = 0.7f) else Color(0xFF1E293B))
            .border(
                1.dp,
                if (isAlwaysOnline) EmeraldLight.copy(alpha = 0.3f) else SlateDarkBorder,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isAlwaysOnline) EmeraldLight else Color.Gray)
        )
        Text(
            text = if (isAlwaysOnline) "24/7 ONLINE" else "ON-DEMAND",
            color = if (isAlwaysOnline) Color(0xFFA7F3D0) else Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
