package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CraftHostTopBar(
    isDemoMode: Boolean,
    onToggleDemoMode: () -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenApkDownload: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier
            .border(1.dp, SlateDarkBorder.copy(alpha = 0.5f)),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ObsidianDarkBg,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Menu",
                    tint = TextDarkPrimary
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Brand Emblem
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldContainerDark)
                        .border(1.dp, EmeraldLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "CraftHost Logo",
                        tint = EmeraldLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "CraftHost",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF064E3B))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "24/7",
                                color = EmeraldLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Java • Bedrock • Proximity Voice",
                        color = TextDarkMuted,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Demo Mode Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDemoMode) AmberAccent.copy(alpha = 0.2f) else SlateDarkCard)
                    .border(
                        1.dp,
                        if (isDemoMode) AmberAccent.copy(alpha = 0.7f) else SlateDarkBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onToggleDemoMode() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isDemoMode) AmberAccent else EmeraldLight)
                    )
                    Text(
                        text = if (isDemoMode) "DEMO MODE" else "LIVE CLOUD",
                        color = if (isDemoMode) AmberAccent else EmeraldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(onClick = onOpenApkDownload) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = "Direct Download APK",
                    tint = EmeraldLight
                )
            }

            IconButton(onClick = onOpenNotifications) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = EmeraldPrimary) {
                            Text("2", color = Color.Black, fontSize = 9.sp)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextDarkSecondary
                    )
                }
            }
        }
    )
}
