package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

enum class ServerPanelTab(val label: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Default.Info),
    CONSOLE("Console", Icons.Default.Terminal),
    WORLD("World & Aternos", Icons.Default.Public),
    BACKUPS("Backups", Icons.Default.Backup),
    MODS_PLUGINS("Mods & Plugins", Icons.Default.Extension),
    ADDONS_PACKS("Packs & Addons", Icons.Default.Layers),
    FILES("Files", Icons.Default.Folder),
    PLAYERS("Players", Icons.Default.People),
    VOICE("Voice Chat", Icons.Default.RecordVoiceOver),
    SETTINGS("Settings", Icons.Default.Settings),
    METRICS("Metrics", Icons.Default.ShowChart)
}

@Composable
fun ServerControlPanelScreen(
    server: MinecraftServer,
    logs: List<String>,
    files: List<ServerFileItem>,
    backups: List<BackupSnapshot>,
    plugins: List<PluginModItem>,
    players: List<MinecraftPlayer>,
    voiceParticipants: List<VoiceParticipant>,
    listenerPos: VoicePosition,
    isMuted: Boolean,
    isPushToTalkActive: Boolean,
    worldMetadata: ServerWorldMetadata? = null,
    resourcePacks: List<ResourcePackItem> = emptyList(),
    addons: List<BedrockAddonItem> = emptyList(),
    onStartServer: () -> Unit,
    onStopServer: () -> Unit,
    onRestartServer: () -> Unit,
    onToggleAlwaysOnline: (Boolean) -> Unit,
    onTriggerCrash: () -> Unit,
    onSendCommand: (String) -> Unit,
    onClearConsole: () -> Unit,
    onSaveFile: (String, String) -> Unit,
    onCreateBackup: (String) -> Unit,
    onRestoreBackup: (String) -> Unit,
    onDeleteBackup: (String) -> Unit = {},
    onImportExternalBackup: (String, String) -> Unit = { _, _ -> },
    onImportAternosWorld: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onImportWorldZip: (String, WorldImportSource) -> Unit = { _, _ -> },
    onResetWorld: (String, String) -> Unit = { _, _ -> },
    onTogglePlugin: (String, Boolean) -> Unit,
    onInstallPlugin: (String) -> Unit = {},
    onDeletePlugin: (String) -> Unit = {},
    onUploadCustomMod: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onAddResourcePack: (ResourcePackItem) -> Unit = {},
    onToggleResourcePack: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteResourcePack: (String) -> Unit = {},
    onAddBedrockAddon: (BedrockAddonItem) -> Unit = {},
    onToggleBedrockAddon: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteBedrockAddon: (String) -> Unit = {},
    onTogglePlayerOp: (String, Boolean) -> Unit,
    onBanPlayer: (String) -> Unit,
    onUnbanPlayer: (String) -> Unit,
    onToggleMute: () -> Unit,
    onPushToTalkChange: (Boolean) -> Unit,
    onUpdateVoiceRange: (Int) -> Unit,
    onOpenVoiceSettings: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ServerPanelTab.OVERVIEW) }
    val clipboardManager = LocalClipboardManager.current
    var copiedToast by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg)
    ) {
        // Sticky Header: Server identity, status, and power controls
        ServerHeader(
            server = server,
            onStart = onStartServer,
            onStop = onStopServer,
            onRestart = onRestartServer,
            onToggleAlwaysOnline = onToggleAlwaysOnline,
            onTriggerCrash = onTriggerCrash,
            onBack = onBack,
            onCopyAddress = {
                val full = "${server.address}:${server.port}"
                clipboardManager.setText(AnnotatedString(full))
                copiedToast = true
            }
        )

        // Subtabs Navigation Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = SlateDarkSurface,
            contentColor = Color.White,
            edgePadding = 12.dp,
            indicator = { /* subtle default */ },
            divider = { HorizontalDivider(color = SlateDarkBorder) }
        ) {
            ServerPanelTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) EmeraldLight else TextDarkMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = tab.label,
                                color = if (isSelected) Color.White else TextDarkSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        }

        // Active Subtab View
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (selectedTab) {
                ServerPanelTab.OVERVIEW -> OverviewTab(server = server, onCopyAddress = {
                    val full = "${server.address}:${server.port}"
                    clipboardManager.setText(AnnotatedString(full))
                })
                ServerPanelTab.CONSOLE -> LiveConsoleView(
                    logs = logs,
                    onSendCommand = onSendCommand,
                    onClearConsole = onClearConsole,
                    modifier = Modifier.fillMaxSize()
                )
                ServerPanelTab.WORLD -> WorldManagementTab(
                    server = server,
                    worldMetadata = worldMetadata,
                    onImportAternosWorld = onImportAternosWorld,
                    onImportWorldZip = onImportWorldZip,
                    onResetWorld = onResetWorld
                )
                ServerPanelTab.BACKUPS -> BackupsManagementTab(
                    backups = backups,
                    onCreateBackup = onCreateBackup,
                    onRestoreBackup = onRestoreBackup,
                    onDeleteBackup = onDeleteBackup,
                    onImportExternalBackup = onImportExternalBackup
                )
                ServerPanelTab.MODS_PLUGINS -> ModsAndPluginsTab(
                    plugins = plugins,
                    onToggle = onTogglePlugin,
                    onInstall = onInstallPlugin,
                    onDelete = onDeletePlugin,
                    onUploadCustomMod = onUploadCustomMod
                )
                ServerPanelTab.ADDONS_PACKS -> AddonsAndPacksTab(
                    resourcePacks = resourcePacks,
                    addons = addons,
                    onAddResourcePack = onAddResourcePack,
                    onToggleResourcePack = onToggleResourcePack,
                    onDeleteResourcePack = onDeleteResourcePack,
                    onAddBedrockAddon = onAddBedrockAddon,
                    onToggleBedrockAddon = onToggleBedrockAddon,
                    onDeleteBedrockAddon = onDeleteBedrockAddon
                )
                ServerPanelTab.FILES -> FilesTab(files = files, onSaveFile = onSaveFile)
                ServerPanelTab.PLAYERS -> PlayersTab(
                    players = players,
                    onToggleOp = onTogglePlayerOp,
                    onBan = onBanPlayer,
                    onUnban = onUnbanPlayer
                )
                ServerPanelTab.VOICE -> VoiceTab(
                    server = server,
                    participants = voiceParticipants,
                    listenerPos = listenerPos,
                    isMuted = isMuted,
                    isPushToTalkActive = isPushToTalkActive,
                    onToggleMute = onToggleMute,
                    onPushToTalkChange = onPushToTalkChange,
                    onUpdateRange = onUpdateVoiceRange,
                    onOpenVoiceSettings = onOpenVoiceSettings
                )
                ServerPanelTab.SETTINGS -> SettingsTab(server = server)
                ServerPanelTab.METRICS -> MetricsTab(server = server)
            }
        }
    }
}

@Composable
private fun ServerHeader(
    server: MinecraftServer,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRestart: () -> Unit,
    onToggleAlwaysOnline: (Boolean) -> Unit,
    onTriggerCrash: () -> Unit,
    onBack: () -> Unit,
    onCopyAddress: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateDarkBorder, RoundedCornerShape(0.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Back + Name + Badges + Crash Test
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDarkPrimary)
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(server.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            EditionBadge(edition = server.edition)
                        }
                        Text(
                            text = "${server.address}:${server.port}",
                            color = CyanLight,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.clickable { onCopyAddress() }
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ServerStatusBadge(status = server.status)

                    // Crash Watchdog Test Action
                    IconButton(
                        onClick = onTriggerCrash,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF3B1515))
                            .border(1.dp, RedstoneRed.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.BugReport, contentDescription = "Simulate Crash (Supervisor Watchdog Test)", tint = RedstoneRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Row 2: Always Online toggle & Start/Stop/Restart Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 24/7 Always Online Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = server.isAlwaysOnline,
                        onCheckedChange = onToggleAlwaysOnline,
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldLight, checkedTrackColor = EmeraldContainerDark)
                    )
                    Column {
                        Text(
                            text = if (server.isAlwaysOnline) "24/7 Always Online: ACTIVE" else "Always Online: OFF",
                            color = if (server.isAlwaysOnline) EmeraldLight else TextDarkSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Runs continuously on node",
                            color = TextDarkMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Power Controls
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (server.status == ServerStatus.ONLINE) {
                        Button(
                            onClick = onRestart,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDarkCard, contentColor = AmberAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onStop,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RedstoneRed, contentColor = Color.White),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onStart,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start Server", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(server: MinecraftServer, onCopyAddress: () -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("QUICK CONNECTION DETAILS", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Server Address (IP:Port)", color = TextDarkSecondary, fontSize = 12.sp)
                            Text("${server.address}:${server.port}", color = CyanLight, fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onCopyAddress,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanContainerDark, contentColor = CyanLight)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Address", fontSize = 11.sp)
                        }
                    }

                    if (server.edition == MinecraftEdition.BEDROCK || server.software == ServerSoftware.GEYSER_STANDALONE) {
                        Text(
                            text = "Bedrock Port: 19132 • Java Port: 25565 (Crossplay Enabled via Geyser)",
                            color = EmeraldLight,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("LIVE HARDWARE TELEMETRY", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    ResourceLinearBar(
                        label = "CPU Load",
                        valueText = "${server.cpuUsagePercent}% (${server.resources.vCpu} Cores)",
                        progress = server.cpuUsagePercent / 100f,
                        color = EmeraldLight
                    )

                    ResourceLinearBar(
                        label = "RAM Usage",
                        valueText = "${(server.ramUsageMb / 1024f).toInt()} / ${server.resources.ramGb} GB DDR5",
                        progress = server.ramUsageMb / (server.resources.ramGb * 1024f),
                        color = CyanLight
                    )

                    ResourceLinearBar(
                        label = "NVMe Storage",
                        valueText = "${server.diskUsageGb} / ${server.resources.storageGb} GB",
                        progress = server.diskUsageGb / server.resources.storageGb,
                        color = AmberAccent
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("PROPERTIES SUMMARY", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SummaryRow("Software Core", "${server.software.displayName} (${server.version})")
                    SummaryRow("Compute Node", server.assignedNodeId)
                    SummaryRow("Region", "${server.region.flag} ${server.region.displayName}")
                    SummaryRow("TPS", "${server.tps} (Optimal)")
                    SummaryRow("Uptime", "${server.uptimeSeconds / 3600} hours continuous")
                    SummaryRow("Proximity Voice Chat", if (server.voiceEnabled) "Active (${server.voiceRangeBlocks} blocks)" else "Disabled")
                }
            }
        }
    }
}

@Composable
private fun PlayersTab(
    players: List<MinecraftPlayer>,
    onToggleOp: (String, Boolean) -> Unit,
    onBan: (String) -> Unit,
    onUnban: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("PLAYERS ON SERVER (${players.count { it.isOnline }})", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Whitelist: OFF", color = TextDarkSecondary, fontSize = 11.sp)
            }
        }

        items(players) { p ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (p.isOnline) EmeraldLight else if (p.isBanned) RedstoneRed else Color.Gray)
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(p.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (p.isOp) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF451A03))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("OP", color = AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                EditionBadge(edition = p.edition)
                            }
                            Text(
                                text = if (p.isBanned) "BANNED" else if (p.isOnline) "Ping: ${p.pingMs}ms • Voice: ${if (p.voiceConnected) "Connected" else "Off"}" else "Offline",
                                color = if (p.isBanned) RedstoneRed else TextDarkSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onToggleOp(p.username, !p.isOp) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (p.isOp) Color(0xFF3F3F46) else Color(0xFF1E293B),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(if (p.isOp) "De-OP" else "OP", fontSize = 10.sp)
                        }

                        if (p.isBanned) {
                            Button(
                                onClick = { onUnban(p.username) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Unban", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onBan(p.username) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RedstoneRed.copy(alpha = 0.2f), contentColor = RedstoneRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RedstoneRed),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Ban", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilesTab(
    files: List<ServerFileItem>,
    onSaveFile: (String, String) -> Unit
) {
    var selectedFile by remember { mutableStateOf<ServerFileItem?>(files.firstOrNull { it.path == "/server.properties" }) }
    var editedContent by remember { mutableStateOf(selectedFile?.content ?: "") }

    LaunchedEffect(selectedFile) {
        editedContent = selectedFile?.content ?: ""
    }

    if (selectedFile != null && selectedFile?.type != FileType.FOLDER) {
        // Text File Editor View
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = { selectedFile = null }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to files", tint = TextDarkPrimary)
                    }
                    Text(selectedFile!!.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        onSaveFile(selectedFile!!.path, editedContent)
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save File", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = editedContent,
                onValueChange = { editedContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TerminalBg,
                    unfocusedContainerColor = TerminalBg,
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = SlateDarkBorder,
                    focusedTextColor = Color(0xFFA7F3D0),
                    unfocusedTextColor = Color(0xFFA7F3D0)
                )
            )
        }
    } else {
        // File Directory Browser
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Text("SERVER ROOT DIRECTORY (/data/servers)", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            items(files) { file ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedFile = file }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                imageVector = if (file.type == FileType.FOLDER) Icons.Default.Folder else Icons.Default.Description,
                                contentDescription = null,
                                tint = if (file.type == FileType.FOLDER) AmberAccent else CyanLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(file.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("${file.sizeBytes} bytes • ${file.modifiedDate}", color = TextDarkMuted, fontSize = 10.sp)
                            }
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextDarkMuted)
                    }
                }
            }
        }
    }
}



@Composable
private fun VoiceTab(
    server: MinecraftServer,
    participants: List<VoiceParticipant>,
    listenerPos: VoicePosition,
    isMuted: Boolean,
    isPushToTalkActive: Boolean,
    onToggleMute: () -> Unit,
    onPushToTalkChange: (Boolean) -> Unit,
    onUpdateRange: (Int) -> Unit,
    onOpenVoiceSettings: () -> Unit = {}
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
                        Column {
                            Text("PROXIMITY PARAMETERS", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Attenuation: ${server.voiceRangeBlocks} Blocks", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onOpenVoiceSettings,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Audio Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            VoiceRadarView(
                listenerPos = listenerPos,
                participants = participants,
                maxVoiceRangeBlocks = server.voiceRangeBlocks,
                isMuted = isMuted,
                isPushToTalkActive = isPushToTalkActive,
                onToggleMute = onToggleMute,
                onPushToTalkChange = onPushToTalkChange
            )
        }
    }
}

@Composable
private fun SettingsTab(server: MinecraftServer) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("GAMEPLAY CONFIGURATION", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SummaryRow("Gamemode", server.gamemode.replaceFirstChar { it.uppercase() })
                    SummaryRow("Difficulty", server.difficulty.replaceFirstChar { it.uppercase() })
                    SummaryRow("Player vs Player (PvP)", if (server.pvpEnabled) "Enabled" else "Disabled")
                    SummaryRow("Max Players", "${server.maxPlayers}")
                    SummaryRow("View Distance", "${server.viewDistance} chunks")
                    SummaryRow("Simulation Distance", "${server.simulationDistance} chunks")
                    SummaryRow("Mojang Online Mode", if (server.onlineModeMojang) "Enforced (Mojang Auth)" else "Offline/Cracked")
                }
            }
        }
    }
}

@Composable
private fun MetricsTab(server: MinecraftServer) {
    val cpuSamples = remember { listOf(24f, 26f, 28f, 25f, 32f, 38f, 29f, 28f, 31f, 28.4f) }
    val ramSamples = remember { listOf(2100f, 2250f, 2400f, 2350f, 2500f, 2620f, 2680f) }
    val tpsSamples = remember { listOf(20f, 20f, 19.98f, 20f, 20f, 20f, 20f) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("CPU HISTORY (Last 1 Hour)", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            SparklineChart(dataPoints = cpuSamples, lineColor = EmeraldLight, modifier = Modifier.fillMaxWidth().height(120.dp))
        }

        item {
            Text("RAM USAGE (Last 1 Hour)", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            SparklineChart(dataPoints = ramSamples, lineColor = CyanLight, modifier = Modifier.fillMaxWidth().height(120.dp))
        }

        item {
            Text("SERVER TPS STABILITY (Target: 20.0)", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            SparklineChart(dataPoints = tpsSamples, lineColor = AmberAccent, modifier = Modifier.fillMaxWidth().height(120.dp))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextDarkSecondary, fontSize = 12.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}
