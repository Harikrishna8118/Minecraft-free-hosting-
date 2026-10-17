package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ServerDao
import com.example.data.local.ServerEntity
import com.example.model.MinecraftEdition
import com.example.model.ServerStatus
import com.example.ui.components.AlwaysOnlineTag
import com.example.ui.components.EditionBadge
import com.example.ui.components.ServerStatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

private enum class ServerFilter(val label: String) {
    ALL("All Servers"),
    ONLINE("Online"),
    OFFLINE("Offline"),
    JAVA("Java"),
    BEDROCK("Bedrock")
}

/**
 * Screen displaying the list of Minecraft servers fetched directly from Room database,
 * featuring status indicators and quick start/stop action triggers.
 */
@Composable
fun ServerListScreen(
    serverDao: ServerDao,
    onServerClick: (String) -> Unit,
    onCreateServerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Collect reactive stream of servers directly from Room database
    val serversList by serverDao.getAllServers().collectAsStateWithLifecycle(initialValue = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var selectedFilter by remember { mutableStateOf(ServerFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var copiedServerId by remember { mutableStateOf<String?>(null) }

    val filteredServers = remember(serversList, selectedFilter, searchQuery) {
        serversList.filter { server ->
            val matchesFilter = when (selectedFilter) {
                ServerFilter.ALL -> true
                ServerFilter.ONLINE -> server.status == ServerStatus.ONLINE
                ServerFilter.OFFLINE -> server.status == ServerStatus.OFFLINE || server.status == ServerStatus.CRASHED
                ServerFilter.JAVA -> server.edition == MinecraftEdition.JAVA
                ServerFilter.BEDROCK -> server.edition == MinecraftEdition.BEDROCK
            }
            val matchesSearch = searchQuery.isBlank() ||
                server.name.contains(searchQuery, ignoreCase = true) ||
                server.address.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    val onlineCount = serversList.count { it.status == ServerStatus.ONLINE }
    val offlineCount = serversList.size - onlineCount

    Scaffold(
        containerColor = ObsidianDarkBg,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateServerClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Server", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianDarkBg)
        ) {
            // Header with Statistics
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Minecraft Servers",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Room Persistence • ${serversList.size} Saved Servers",
                            color = TextDarkMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Online / Offline count badge
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF064E3B),
                            modifier = Modifier.border(1.dp, EmeraldLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldLight))
                                Text("$onlineCount Online", color = Color(0xFFA7F3D0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SlateDarkSurface,
                            modifier = Modifier.border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = "$offlineCount Offline",
                                color = TextDarkMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search servers by name or address...", fontSize = 12.sp, color = TextDarkMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextDarkMuted, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextDarkMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateDarkSurface,
                        unfocusedContainerColor = SlateDarkSurface,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateDarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = TextDarkPrimary
                    )
                )

                // Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ServerFilter.values()) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldContainerDark,
                                selectedLabelColor = EmeraldLight,
                                containerColor = SlateDarkSurface,
                                labelColor = TextDarkSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = SlateDarkBorder,
                                selectedBorderColor = EmeraldLight
                            )
                        )
                    }
                }
            }

            // Server List LazyColumn
            if (filteredServers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SlateDarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Dns, contentDescription = null, tint = TextDarkMuted, modifier = Modifier.size(28.dp))
                        }
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching servers found" else "No servers in local database",
                            color = TextDarkPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try a different search keyword." else "Create your first server to get started.",
                            color = TextDarkMuted,
                            fontSize = 12.sp
                        )
                        if (searchQuery.isEmpty()) {
                            Button(
                                onClick = onCreateServerClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Create Server", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredServers,
                        key = { it.id }
                    ) { server ->
                        ServerListItemCard(
                            server = server,
                            onCardClick = { onServerClick(server.id) },
                            onTogglePower = {
                                coroutineScope.launch {
                                    val newStatus = if (server.status == ServerStatus.ONLINE) {
                                        ServerStatus.OFFLINE
                                    } else {
                                        ServerStatus.ONLINE
                                    }
                                    // Direct quick action update into Room database
                                    serverDao.updateServer(
                                        server.copy(
                                            status = newStatus,
                                            updatedAt = System.currentTimeMillis()
                                        )
                                    )
                                }
                            },
                            onCopyAddress = {
                                val full = "${server.address}:${server.port}"
                                clipboardManager.setText(AnnotatedString(full))
                                copiedServerId = server.id
                            },
                            isCopied = copiedServerId == server.id
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerListItemCard(
    server: ServerEntity,
    onCardClick: () -> Unit,
    onTogglePower: () -> Unit,
    onCopyAddress: () -> Unit,
    isCopied: Boolean
) {
    val isOnline = server.status == ServerStatus.ONLINE

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isOnline) EmeraldPrimary.copy(alpha = 0.35f) else SlateDarkBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Server Name, Badges, Status
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isOnline) Color(0xFF0F2B20) else Color(0xFF1B232B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = if (isOnline) EmeraldLight else TextDarkMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = server.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${server.address}:${server.port}",
                                color = if (isOnline) CyanLight else TextDarkMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.clickable { onCopyAddress() }
                            )
                            IconButton(
                                onClick = onCopyAddress,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy IP",
                                    tint = if (isCopied) EmeraldLight else TextDarkMuted,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }

                // Status Badge (Online / Offline)
                ServerStatusBadge(status = server.status)
            }

            // Specs & Feature Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = "${server.ramGb}GB • ${server.vCpu} vCPU",
                        color = TextDarkSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (server.voiceEnabled) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyanContainerDark)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${server.voiceRangeBlocks}b Voice",
                            color = CyanLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = SlateDarkBorder.copy(alpha = 0.6f))

            // Bottom Action Row: Players Info & Quick Start/Stop Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextDarkMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isOnline) "${server.onlinePlayers}/${server.maxPlayers} players" else "0 players (offline)",
                        color = if (isOnline) TextDarkPrimary else TextDarkMuted,
                        fontSize = 11.sp
                    )
                    if (isOnline) {
                        Text(
                            text = "• TPS: ${server.tps}",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Quick Action Buttons (Start / Stop)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isOnline) {
                        // Quick Action: Stop Server
                        Button(
                            onClick = onTogglePower,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedstoneRed.copy(alpha = 0.2f),
                                contentColor = RedstoneRed
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RedstoneRed.copy(alpha = 0.6f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        // Quick Action: Start Server
                        Button(
                            onClick = onTogglePower,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    // Open Server Details
                    OutlinedButton(
                        onClick = onCardClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Manage", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
