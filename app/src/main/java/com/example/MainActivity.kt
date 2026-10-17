package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CraftHostRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class AppDestination(val label: String, val icon: ImageVector) {
    SERVERS("Servers", Icons.Default.Dns),
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    VOICE_HUB("Voice Chat", Icons.Default.RecordVoiceOver),
    INFRASTRUCTURE("Nodes", Icons.Default.Hub),
    ADMIN_BILLING("Admin", Icons.Default.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {

    private val database by lazy {
        com.example.data.local.AppDatabase.getDatabase(applicationContext)
    }

    private val repository by lazy {
        CraftHostRepository(serverDao = database.serverDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CraftHostApp(
                    repository = repository,
                    serverDao = database.serverDao()
                )
            }
        }
    }
}

@Composable
fun CraftHostApp(
    repository: CraftHostRepository,
    serverDao: com.example.data.local.ServerDao
) {
    var currentDestination by remember { mutableStateOf(AppDestination.SERVERS) }
    var isInCreationWizard by remember { mutableStateOf(false) }
    var viewingServerPanel by remember { mutableStateOf(false) }
    var viewingVoiceSettings by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showApkDownloadDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Observe Repository States
    val servers by repository.servers.collectAsStateWithLifecycle()
    val selectedServerId by repository.selectedServerId.collectAsStateWithLifecycle()
    val consoleLogs by repository.consoleLogs.collectAsStateWithLifecycle()
    val nodes by repository.nodes.collectAsStateWithLifecycle()
    val crashLogs by repository.crashLogs.collectAsStateWithLifecycle()
    val voiceParticipants by repository.voiceParticipants.collectAsStateWithLifecycle()
    val listenerPos by repository.localListenerPos.collectAsStateWithLifecycle()
    val isMuted by repository.isMicrophoneMuted.collectAsStateWithLifecycle()
    val isPttActive by repository.isPushToTalkActive.collectAsStateWithLifecycle()
    val voiceHealthy by repository.voiceServiceHealthy.collectAsStateWithLifecycle()
    val voiceChatSettings by repository.voiceChatSettings.collectAsStateWithLifecycle()
    val serverFiles by repository.serverFiles.collectAsStateWithLifecycle()
    val serverBackups by repository.serverBackups.collectAsStateWithLifecycle()
    val serverPlugins by repository.serverPlugins.collectAsStateWithLifecycle()
    val serverPlayers by repository.serverPlayers.collectAsStateWithLifecycle()
    val serverWorlds by repository.serverWorldMetadata.collectAsStateWithLifecycle()
    val serverResourcePacks by repository.serverResourcePacks.collectAsStateWithLifecycle()
    val serverAddons by repository.serverAddons.collectAsStateWithLifecycle()
    val activityLogs by repository.activityLogs.collectAsStateWithLifecycle()
    val isDemoMode by repository.isDemoMode.collectAsStateWithLifecycle()

    val currentServer = remember(servers, selectedServerId) {
        servers.find { it.id == selectedServerId } ?: servers.firstOrNull()
    }

    // Back button handling
    BackHandler(enabled = isInCreationWizard || viewingServerPanel || viewingVoiceSettings || currentDestination != AppDestination.SERVERS) {
        when {
            viewingVoiceSettings -> viewingVoiceSettings = false
            isInCreationWizard -> isInCreationWizard = false
            viewingServerPanel -> viewingServerPanel = false
            else -> currentDestination = AppDestination.SERVERS
        }
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = EmeraldLight)
                    Text("CraftHost Notifications", color = Color.White, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NotificationItem(
                        title = "24/7 Hosting Guard Active",
                        msg = "Node atlas-us-east-01 confirmed 0 container dropouts in the last 24 hours.",
                        time = "10 mins ago",
                        color = EmeraldLight
                    )
                    NotificationItem(
                        title = "Voice Gateway v2.5 Synchronized",
                        msg = "Simple Voice Chat protocol successfully connected with 4 players in proximity.",
                        time = "1 hour ago",
                        color = CyanLight
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                ) {
                    Text("Dismiss")
                }
            },
            containerColor = SlateDarkCard
        )
    }

    if (showApkDownloadDialog) {
        DirectApkDownloadDialog(onDismiss = { showApkDownloadDialog = false })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = ObsidianDarkBg,
                drawerContentColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Drawer Brand
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldContainerDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = EmeraldLight)
                    }
                    Column {
                        Text("CraftHost Cloud", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                        Text("24/7 Minecraft & Proximity Voice", fontSize = 11.sp, color = TextDarkSecondary)
                    }
                }

                HorizontalDivider(color = SlateDarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                AppDestination.values().forEach { dest ->
                    NavigationDrawerItem(
                        icon = { Icon(dest.icon, contentDescription = null) },
                        label = { Text(dest.label, fontWeight = FontWeight.SemiBold) },
                        selected = currentDestination == dest && !isInCreationWizard && !viewingServerPanel,
                        onClick = {
                            currentDestination = dest
                            isInCreationWizard = false
                            viewingServerPanel = false
                            coroutineScope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = EmeraldContainerDark,
                            selectedTextColor = EmeraldLight,
                            selectedIconColor = EmeraldLight,
                            unselectedTextColor = TextDarkPrimary,
                            unselectedIconColor = TextDarkSecondary
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                HorizontalDivider(color = SlateDarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Fast Creation Shortcut
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = EmeraldLight) },
                    label = { Text("Create New Server", color = EmeraldLight, fontWeight = FontWeight.Bold) },
                    selected = isInCreationWizard,
                    onClick = {
                        isInCreationWizard = true
                        viewingServerPanel = false
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                // Direct Download APK
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Android, contentDescription = null, tint = CyanLight) },
                    label = { Text("Direct Download APK", color = CyanLight, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        showApkDownloadDialog = true
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                // Voice Chat Settings
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Tune, contentDescription = null, tint = EmeraldLight) },
                    label = { Text("Voice Chat Settings", color = EmeraldLight, fontWeight = FontWeight.SemiBold) },
                    selected = viewingVoiceSettings,
                    onClick = {
                        viewingVoiceSettings = true
                        viewingServerPanel = false
                        isInCreationWizard = false
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CraftHostTopBar(
                    isDemoMode = isDemoMode,
                    onToggleDemoMode = { repository.toggleDemoMode() },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onOpenNotifications = { showNotificationsDialog = true },
                    onOpenApkDownload = { showApkDownloadDialog = true }
                )
            },
            bottomBar = {
                if (!isInCreationWizard && !viewingServerPanel && !viewingVoiceSettings) {
                    NavigationBar(
                        containerColor = ObsidianDarkBg,
                        contentColor = Color.White,
                        modifier = Modifier
                            .border(1.dp, SlateDarkBorder.copy(alpha = 0.5f))
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        AppDestination.values().forEach { dest ->
                            val selected = currentDestination == dest
                            NavigationBarItem(
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = { Text(dest.label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                selected = selected,
                                onClick = {
                                    currentDestination = dest
                                    isInCreationWizard = false
                                    viewingServerPanel = false
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldLight,
                                    selectedTextColor = EmeraldLight,
                                    indicatorColor = EmeraldContainerDark,
                                    unselectedIconColor = TextDarkSecondary,
                                    unselectedTextColor = TextDarkSecondary
                                )
                            )
                        }
                    }
                }
            },
            containerColor = ObsidianDarkBg,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    viewingVoiceSettings -> {
                        VoiceSettingsScreen(
                            currentSettings = voiceChatSettings,
                            onSaveSettings = { repository.updateVoiceChatSettings(it) },
                            onResetSettings = { repository.resetVoiceChatSettings() },
                            onBack = { viewingVoiceSettings = false }
                        )
                    }

                    isInCreationWizard -> {
                        ServerCreationWizardScreen(
                            onServerCreated = { newServer ->
                                repository.createNewServer(newServer)
                                isInCreationWizard = false
                                viewingServerPanel = true
                            },
                            onCancel = { isInCreationWizard = false }
                        )
                    }

                    viewingServerPanel && currentServer != null -> {
                        ServerControlPanelScreen(
                            server = currentServer,
                            logs = consoleLogs[currentServer.id] ?: emptyList(),
                            files = serverFiles[currentServer.id] ?: emptyList(),
                            backups = serverBackups[currentServer.id] ?: emptyList(),
                            plugins = serverPlugins[currentServer.id] ?: emptyList(),
                            players = serverPlayers[currentServer.id] ?: emptyList(),
                            voiceParticipants = voiceParticipants,
                            listenerPos = listenerPos,
                            isMuted = isMuted,
                            isPushToTalkActive = isPttActive,
                            worldMetadata = serverWorlds[currentServer.id],
                            resourcePacks = serverResourcePacks[currentServer.id] ?: emptyList(),
                            addons = serverAddons[currentServer.id] ?: emptyList(),
                            onStartServer = { repository.startServer(currentServer.id) },
                            onStopServer = { repository.stopServer(currentServer.id) },
                            onRestartServer = { repository.restartServer(currentServer.id) },
                            onToggleAlwaysOnline = { repository.toggleAlwaysOnline(currentServer.id, it) },
                            onTriggerCrash = { repository.triggerSimulatedCrash(currentServer.id) },
                            onSendCommand = { repository.sendConsoleCommand(currentServer.id, it) },
                            onClearConsole = { repository.clearConsole(currentServer.id) },
                            onSaveFile = { path, content -> repository.saveFileContent(currentServer.id, path, content) },
                            onCreateBackup = { name -> repository.createManualBackup(currentServer.id, name) },
                            onRestoreBackup = { bkId -> repository.restoreBackup(currentServer.id, bkId) },
                            onDeleteBackup = { bkId -> repository.deleteBackup(currentServer.id, bkId) },
                            onImportExternalBackup = { label, url -> repository.importExternalBackup(currentServer.id, label, url) },
                            onImportAternosWorld = { wName, archName, isBedrock -> repository.importWorldFromAternos(currentServer.id, wName, archName, isBedrock) },
                            onImportWorldZip = { fName, src -> repository.importWorldZip(currentServer.id, fName, src) },
                            onResetWorld = { seed, name -> repository.resetWorld(currentServer.id, seed, name) },
                            onTogglePlugin = { plugId, en -> repository.togglePlugin(currentServer.id, plugId, en) },
                            onInstallPlugin = { plugId -> repository.installPlugin(currentServer.id, plugId) },
                            onDeletePlugin = { plugId -> repository.deletePlugin(currentServer.id, plugId) },
                            onUploadCustomMod = { name, ver, cat, loader, file, author -> repository.installCustomMod(currentServer.id, name, ver, cat, loader, file, author) },
                            onAddResourcePack = { pack -> repository.addResourcePack(currentServer.id, pack) },
                            onToggleResourcePack = { pId, en -> repository.toggleResourcePack(currentServer.id, pId, en) },
                            onDeleteResourcePack = { pId -> repository.deleteResourcePack(currentServer.id, pId) },
                            onAddBedrockAddon = { addon -> repository.addBedrockAddon(currentServer.id, addon) },
                            onToggleBedrockAddon = { aId, en -> repository.toggleBedrockAddon(currentServer.id, aId, en) },
                            onDeleteBedrockAddon = { aId -> repository.deleteBedrockAddon(currentServer.id, aId) },
                            onTogglePlayerOp = { user, isOp -> repository.togglePlayerOp(currentServer.id, user, isOp) },
                            onBanPlayer = { user -> repository.banPlayer(currentServer.id, user) },
                            onUnbanPlayer = { user -> repository.unbanPlayer(currentServer.id, user) },
                            onToggleMute = { repository.toggleMicrophoneMute() },
                            onPushToTalkChange = { repository.setPushToTalkActive(it) },
                            onUpdateVoiceRange = { repository.updateServerVoiceRange(currentServer.id, it) },
                            onOpenVoiceSettings = { viewingVoiceSettings = true },
                            onBack = { viewingServerPanel = false }
                        )
                    }

                    else -> {
                        when (currentDestination) {
                            AppDestination.SERVERS -> {
                                ServerListScreen(
                                    serverDao = serverDao,
                                    onServerClick = { serverId ->
                                        repository.selectServer(serverId)
                                        viewingServerPanel = true
                                    },
                                    onCreateServerClick = { isInCreationWizard = true }
                                )
                            }
                            AppDestination.DASHBOARD -> {
                                DashboardScreen(
                                    servers = servers,
                                    nodes = nodes,
                                    activityLogs = activityLogs,
                                    onSelectServer = { serverId ->
                                        repository.selectServer(serverId)
                                        viewingServerPanel = true
                                    },
                                    onStartServer = { repository.startServer(it) },
                                    onStopServer = { repository.stopServer(it) },
                                    onRestartServer = { repository.restartServer(it) },
                                    onCreateServerClick = { isInCreationWizard = true },
                                    onViewInfrastructureClick = { currentDestination = AppDestination.INFRASTRUCTURE }
                                )
                            }
                            AppDestination.VOICE_HUB -> {
                                VoiceHubScreen(
                                    listenerPos = listenerPos,
                                    participants = voiceParticipants,
                                    isMuted = isMuted,
                                    isPushToTalkActive = isPttActive,
                                    voiceServiceHealthy = voiceHealthy,
                                    voiceSettings = voiceChatSettings,
                                    onToggleMute = { repository.toggleMicrophoneMute() },
                                    onPushToTalkChange = { repository.setPushToTalkActive(it) },
                                    onUpdateListenerCoords = { x, y, z, yaw ->
                                        repository.setLocalListenerPosition(x, y, z, yaw)
                                    },
                                    onToggleServiceOutage = { repository.toggleVoiceServiceSimulatedOutage() },
                                    onOpenVoiceSettings = { viewingVoiceSettings = true }
                                )
                            }
                            AppDestination.INFRASTRUCTURE -> {
                                InfrastructureScreen(
                                    nodes = nodes,
                                    crashLogs = crashLogs,
                                    onTriggerTestCrash = {
                                        if (currentServer != null) {
                                            repository.triggerSimulatedCrash(currentServer.id)
                                        }
                                    }
                                )
                            }
                            AppDestination.ADMIN_BILLING -> {
                                AdminAndBillingScreen(
                                    isDemoMode = isDemoMode,
                                    onToggleDemoMode = { repository.toggleDemoMode() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(title: String, msg: String, time: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDarkSurface)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(time, color = TextDarkMuted, fontSize = 10.sp)
        }
        Text(msg, color = TextDarkSecondary, fontSize = 11.sp, lineHeight = 15.sp)
    }
}
