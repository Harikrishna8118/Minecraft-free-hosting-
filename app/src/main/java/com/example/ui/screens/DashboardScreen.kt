package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    servers: List<MinecraftServer>,
    nodes: List<ServerNode>,
    activityLogs: List<ActivityLogItem>,
    onSelectServer: (String) -> Unit,
    onStartServer: (String) -> Unit,
    onStopServer: (String) -> Unit,
    onRestartServer: (String) -> Unit,
    onCreateServerClick: () -> Unit,
    onViewInfrastructureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedAddress by remember { mutableStateOf<String?>(null) }

    val onlineCount = servers.count { it.status == ServerStatus.ONLINE }
    val alwaysOnlineCount = servers.count { it.isAlwaysOnline }
    val totalPlayers = servers.sumOf { it.onlinePlayers }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcome Header & Quick Create
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Server Dashboard",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Global Control Plane • 3 Node Regions Active",
                        color = TextDarkMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onCreateServerClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Server", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Stats Counter Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "SERVERS",
                    value = "${servers.size}",
                    subtext = "$onlineCount Online",
                    color = EmeraldLight,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "24/7 HOSTING",
                    value = "$alwaysOnlineCount",
                    subtext = "Always Active",
                    color = CyanLight,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "PLAYERS",
                    value = "$totalPlayers",
                    subtext = "Voice Connected",
                    color = AmberAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // My Minecraft Servers Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MY MINECRAFT SERVERS",
                    color = TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Continuous Hosting Active",
                    color = EmeraldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Servers List
        items(servers) { server ->
            ServerDashboardCard(
                server = server,
                onCardClick = { onSelectServer(server.id) },
                onStart = { onStartServer(server.id) },
                onStop = { onStopServer(server.id) },
                onRestart = { onRestartServer(server.id) },
                onCopyAddress = {
                    val full = "${server.address}:${server.port}"
                    clipboardManager.setText(AnnotatedString(full))
                    copiedAddress = full
                }
            )
        }

        // Infrastructure Status Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
                    .clickable { onViewInfrastructureClick() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanContainerDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = CyanLight, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Compute Nodes & Supervisor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("3/3 Nodes Healthy • Zero Crash Downtime", color = TextDarkSecondary, fontSize = 11.sp)
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextDarkSecondary)
                }
            }
        }

        // Recent Activity Feed
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "RECENT PLATFORM ACTIVITY",
                    color = TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                activityLogs.take(4).forEach { act ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateDarkSurface)
                            .border(1.dp, Color(0xFF1B232C), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val dotColor = when (act.level) {
                                "SUCCESS" -> EmeraldLight
                                "WARN" -> AmberAccent
                                else -> CyanLight
                            }
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
                            Text(text = act.action, color = TextDarkPrimary, fontSize = 12.sp)
                        }
                        Text(text = act.timestamp, color = TextDarkMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        modifier = modifier.border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(text = subtext, color = TextDarkSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ServerDashboardCard(
    server: MinecraftServer,
    onCardClick: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRestart: () -> Unit,
    onCopyAddress: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateDarkBorder, RoundedCornerShape(14.dp))
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (server.edition == MinecraftEdition.JAVA) Color(0xFF0F2B20) else Color(0xFF132839)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = if (server.edition == MinecraftEdition.JAVA) EmeraldLight else CyanLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(text = server.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${server.address}:${server.port}",
                                color = CyanLight,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            IconButton(onClick = onCopyAddress, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy IP", tint = TextDarkMuted, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }

                ServerStatusBadge(status = server.status)
            }

            // Tags row: Edition, Software, 24/7, Voice
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditionBadge(edition = server.edition)
                AlwaysOnlineTag(isAlwaysOnline = server.isAlwaysOnline)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateDarkSurface)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = server.software.displayName, color = TextDarkSecondary, fontSize = 10.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyanContainerDark)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "Voice 400b", color = CyanLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Specs Row
            if (server.status == ServerStatus.ONLINE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResourceLinearBar(
                        label = "CPU",
                        valueText = "${server.cpuUsagePercent}%",
                        progress = server.cpuUsagePercent / 100f,
                        color = EmeraldLight,
                        modifier = Modifier.weight(1f)
                    )
                    ResourceLinearBar(
                        label = "RAM",
                        valueText = "${(server.ramUsageMb / 1024f).toInt()} / ${server.resources.ramGb}GB",
                        progress = server.ramUsageMb / (server.resources.ramGb * 1024f),
                        color = CyanLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = SlateDarkBorder)

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextDarkMuted, modifier = Modifier.size(14.dp))
                    Text(
                        text = "${server.onlinePlayers} / ${server.maxPlayers} players",
                        color = TextDarkSecondary,
                        fontSize = 11.sp
                    )
                    Text(text = "• TPS: ${server.tps}", color = EmeraldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Controls: Start, Stop, Restart
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (server.status == ServerStatus.ONLINE) {
                        IconButton(
                            onClick = onRestart,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SlateDarkSurface)
                                .border(1.dp, SlateDarkBorder, RoundedCornerShape(6.dp))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = AmberAccent, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onStop,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SlateDarkSurface)
                                .border(1.dp, RedstoneRed.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = RedstoneRed, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = onStart,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onCardClick,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface, contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Manage", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
