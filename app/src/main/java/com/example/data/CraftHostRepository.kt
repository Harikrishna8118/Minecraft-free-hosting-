package com.example.data

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
import com.example.data.local.MinecraftServerDao
import com.example.data.local.ServerEntity

class CraftHostRepository(
    private val serverDao: MinecraftServerDao? = null
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // --- State Flows ---
    private val _servers = MutableStateFlow<List<MinecraftServer>>(emptyList())
    val servers: StateFlow<List<MinecraftServer>> = _servers.asStateFlow()

    private val _selectedServerId = MutableStateFlow<String>("")
    val selectedServerId: StateFlow<String> = _selectedServerId.asStateFlow()

    private val _consoleLogs = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val consoleLogs: StateFlow<Map<String, List<String>>> = _consoleLogs.asStateFlow()

    private val _nodes = MutableStateFlow<List<ServerNode>>(emptyList())
    val nodes: StateFlow<List<ServerNode>> = _nodes.asStateFlow()

    private val _crashLogs = MutableStateFlow<List<SupervisorCrashRecoveryLog>>(emptyList())
    val crashLogs: StateFlow<List<SupervisorCrashRecoveryLog>> = _crashLogs.asStateFlow()

    private val _voiceParticipants = MutableStateFlow<List<VoiceParticipant>>(emptyList())
    val voiceParticipants: StateFlow<List<VoiceParticipant>> = _voiceParticipants.asStateFlow()

    private val _localListenerPos = MutableStateFlow(VoicePosition(x = 100.0, y = 64.0, z = 100.0, yawDegrees = 0f))
    val localListenerPos: StateFlow<VoicePosition> = _localListenerPos.asStateFlow()

    private val _isMicrophoneMuted = MutableStateFlow(false)
    val isMicrophoneMuted: StateFlow<Boolean> = _isMicrophoneMuted.asStateFlow()

    private val _isPushToTalkActive = MutableStateFlow(false)
    val isPushToTalkActive: StateFlow<Boolean> = _isPushToTalkActive.asStateFlow()

    private val _voiceServiceHealthy = MutableStateFlow(true)
    val voiceServiceHealthy: StateFlow<Boolean> = _voiceServiceHealthy.asStateFlow()

    private val _serverFiles = MutableStateFlow<Map<String, List<ServerFileItem>>>(emptyMap())
    val serverFiles: StateFlow<Map<String, List<ServerFileItem>>> = _serverFiles.asStateFlow()

    private val _serverBackups = MutableStateFlow<Map<String, List<BackupSnapshot>>>(emptyMap())
    val serverBackups: StateFlow<Map<String, List<BackupSnapshot>>> = _serverBackups.asStateFlow()

    private val _serverPlugins = MutableStateFlow<Map<String, List<PluginModItem>>>(emptyMap())
    val serverPlugins: StateFlow<Map<String, List<PluginModItem>>> = _serverPlugins.asStateFlow()

    private val _serverPlayers = MutableStateFlow<Map<String, List<MinecraftPlayer>>>(emptyMap())
    val serverPlayers: StateFlow<Map<String, List<MinecraftPlayer>>> = _serverPlayers.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLogItem>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLogItem>> = _activityLogs.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    init {
        bootstrapInitialData()
        startTelemetryLoop()
    }

    private fun bootstrapInitialData() {
        val server1 = MinecraftServer(
            id = "srv-smp-java-01",
            name = "Survival SMP 1.21",
            motd = "§aCraftHost §7| §6Official Survival World §b[24/7 Always Online]",
            edition = MinecraftEdition.JAVA,
            software = ServerSoftware.PAPER,
            version = "1.21.4",
            region = ServerRegion.US_EAST,
            address = "smp.crafthost.gg",
            port = 25565,
            status = ServerStatus.ONLINE,
            isAlwaysOnline = true,
            resources = ServerResourceConfig(ramGb = 4, vCpu = 2, storageGb = 30),
            assignedNodeId = "node-us-east-01",
            onlinePlayers = 5,
            maxPlayers = 24,
            cpuUsagePercent = 28.4f,
            ramUsageMb = 2680f,
            diskUsageGb = 8.4f,
            tps = 20.0f,
            uptimeSeconds = 345600L,
            voiceEnabled = true,
            voiceRangeBlocks = 400
        )

        val server2 = MinecraftServer(
            id = "srv-bedrock-bds-02",
            name = "Bedrock Crossplay Realm",
            motd = "§bBedrock + Geyser Crossplay §7- §aProximity Audio Enabled",
            edition = MinecraftEdition.BEDROCK,
            software = ServerSoftware.BEDROCK_DEDICATED,
            version = "1.21.50",
            region = ServerRegion.EU_CENTRAL,
            address = "bedrock.crafthost.gg",
            port = 19132,
            status = ServerStatus.ONLINE,
            isAlwaysOnline = true,
            resources = ServerResourceConfig(ramGb = 6, vCpu = 3, storageGb = 40),
            assignedNodeId = "node-eu-central-01",
            onlinePlayers = 8,
            maxPlayers = 30,
            cpuUsagePercent = 34.1f,
            ramUsageMb = 3420f,
            diskUsageGb = 12.1f,
            tps = 19.98f,
            uptimeSeconds = 518400L,
            voiceEnabled = true,
            voiceRangeBlocks = 400
        )

        val server3 = MinecraftServer(
            id = "srv-fabric-mods-03",
            name = "Adventure RPG Fabric",
            motd = "§dFabric RPG §7- §cCurrently Maintenance",
            edition = MinecraftEdition.JAVA,
            software = ServerSoftware.FABRIC,
            version = "1.21.1",
            region = ServerRegion.AP_EAST,
            address = "fabric.crafthost.gg",
            port = 25567,
            status = ServerStatus.OFFLINE,
            isAlwaysOnline = false,
            resources = ServerResourceConfig(ramGb = 8, vCpu = 4, storageGb = 50),
            assignedNodeId = "node-ap-east-01",
            onlinePlayers = 0,
            maxPlayers = 16,
            cpuUsagePercent = 0.0f,
            ramUsageMb = 0.0f,
            diskUsageGb = 14.8f,
            tps = 0.0f,
            uptimeSeconds = 0L,
            voiceEnabled = true,
            voiceRangeBlocks = 400
        )

        val initialServers = listOf(server1, server2, server3)
        _servers.value = initialServers
        _selectedServerId.value = server1.id

        // Sync with persistent Room Database when DAO is supplied
        serverDao?.let { dao ->
            scope.launch {
                dao.insertServers(initialServers.map { ServerEntity.fromDomainModel(it) })
                dao.getAllServers().collect { entities ->
                    if (entities.isNotEmpty()) {
                        _servers.value = entities.map { it.toDomainModel() }
                    }
                }
            }
        }

        // Nodes
        _nodes.value = listOf(
            ServerNode(
                id = "node-us-east-01",
                name = "Atlas-US-East (N. Virginia)",
                region = ServerRegion.US_EAST,
                ipAddress = "198.51.100.14",
                health = NodeHealth.HEALTHY,
                totalRamGb = 128,
                usedRamGb = 78,
                totalCores = 32,
                cpuUsagePercent = 31.8f,
                totalStorageGb = 2000,
                usedStorageGb = 940,
                runningContainers = 19,
                uptimeDays = 214
            ),
            ServerNode(
                id = "node-eu-central-01",
                name = "Helios-EU-Central (Frankfurt)",
                region = ServerRegion.EU_CENTRAL,
                ipAddress = "198.51.100.58",
                health = NodeHealth.HEALTHY,
                totalRamGb = 128,
                usedRamGb = 64,
                totalCores = 32,
                cpuUsagePercent = 27.5f,
                totalStorageGb = 2000,
                usedStorageGb = 820,
                runningContainers = 16,
                uptimeDays = 188
            ),
            ServerNode(
                id = "node-ap-east-01",
                name = "Titan-AP-East (Tokyo)",
                region = ServerRegion.AP_EAST,
                ipAddress = "198.51.100.92",
                health = NodeHealth.HEALTHY,
                totalRamGb = 64,
                usedRamGb = 32,
                totalCores = 16,
                cpuUsagePercent = 21.0f,
                totalStorageGb = 1000,
                usedStorageGb = 390,
                runningContainers = 9,
                uptimeDays = 94
            )
        )

        // Initial console logs
        val s1Logs = listOf(
            "[11:00:01 INFO] [CraftHost Supervisor] Container srv-smp-java-01 initialized on node-us-east-01",
            "[11:00:02 INFO] [CraftHost Supervisor] 24/7 Always-Online watchdog: ACTIVE",
            "[11:00:03 INFO] [Server] Loading Paper 1.21.4 (build 142) on Java 21 LTS",
            "[11:00:05 INFO] [Server] [SimpleVoiceChat] Proximity Voice Chat protocol bound to UDP port 24454",
            "[11:00:06 INFO] [Server] [SimpleVoiceChat] Configured voice distance: 400.0 blocks",
            "[11:00:07 INFO] [Server] Preparing spawn area: 100%",
            "[11:00:08 INFO] [Server] Done (6.142s)! For help, type \"help\"",
            "[11:02:14 INFO] Alex_Crafter[/192.168.1.12:54890] logged in with entity id 142 at ([world]100.0, 64.0, 100.0)",
            "[11:02:15 INFO] [SimpleVoiceChat] Player Alex_Crafter authenticated with voice session 9e24-af8b",
            "[11:05:42 INFO] Steve_Miner joined the proximity voice session (distance: 48 blocks)",
            "[11:10:00 INFO] Auto-save complete for world 'world', 'world_nether', 'world_the_end'"
        )

        val s2Logs = listOf(
            "[10:50:00 INFO] [CraftHost Supervisor] Starting Bedrock Dedicated Server 1.21.50",
            "[10:50:01 INFO] IPv4 supported, port: 19132",
            "[10:50:03 INFO] Server started.",
            "[10:50:04 INFO] [Geyser-Bridge] Crossplay bridge connected to CraftHost Voice Gateway",
            "[10:50:05 INFO] [Voice Gateway] Bedrock Script API & Companion WebRTC synced with range 400 blocks"
        )

        _consoleLogs.value = mapOf(
            server1.id to s1Logs,
            server2.id to s2Logs,
            server3.id to listOf("[CraftHost Supervisor] Server is currently OFFLINE. Click START to allocate container.")
        )

        // Voice Participants around listener (at 100, 64, 100)
        _voiceParticipants.value = listOf(
            VoiceParticipant(
                id = "p1",
                username = "Steve_Miner",
                edition = MinecraftEdition.JAVA,
                position = VoicePosition(x = 135.0, y = 64.0, z = 110.0, yawDegrees = 45f),
                isSpeaking = true,
                audioVolume = 0.95f,
                pingMs = 21,
                clientType = "Paper / Fabric Mod v2.5"
            ),
            VoiceParticipant(
                id = "p2",
                username = "Alex_Crafter",
                edition = MinecraftEdition.JAVA,
                position = VoicePosition(x = 180.0, y = 70.0, z = 140.0, yawDegrees = 110f),
                isSpeaking = false,
                audioVolume = 0.78f,
                pingMs = 18,
                clientType = "Paper / Fabric Mod v2.5"
            ),
            VoiceParticipant(
                id = "p3",
                username = "RedstoneKing_PE",
                edition = MinecraftEdition.BEDROCK,
                position = VoicePosition(x = 320.0, y = 62.0, z = 280.0, yawDegrees = 200f),
                isSpeaking = true,
                audioVolume = 0.35f,
                pingMs = 42,
                clientType = "Bedrock WebRTC Companion"
            ),
            VoiceParticipant(
                id = "p4",
                username = "EnderFarAway",
                edition = MinecraftEdition.JAVA,
                position = VoicePosition(x = 650.0, y = 64.0, z = 700.0, yawDegrees = 0f),
                isSpeaking = false,
                audioVolume = 0.0f,
                pingMs = 35,
                clientType = "Fabric Mod v2.5"
            )
        )

        // Files
        val defaultProps = """# Minecraft server properties
# Auto-managed by CraftHost Control Plane
server-port=25565
gamemode=survival
difficulty=normal
pvp=true
max-players=24
view-distance=12
simulation-distance=8
motd=§aCraftHost §7| §6Official Survival World §b[24/7 Always Online]
white-list=false
spawn-protection=16
online-mode=true
enable-command-block=true
level-name=world
"""

        val s1Files = listOf(
            ServerFileItem("server.properties", "/server.properties", FileType.PROPERTIES, 482, content = defaultProps),
            ServerFileItem("whitelist.json", "/whitelist.json", FileType.JSON, 128, content = "[]"),
            ServerFileItem("ops.json", "/ops.json", FileType.JSON, 240, content = "[{\"uuid\":\"a1b2c3d4-e5f6\",\"name\":\"Alex_Crafter\",\"level\":4}]"),
            ServerFileItem("eula.txt", "/eula.txt", FileType.CONFIG, 38, content = "eula=true\n"),
            ServerFileItem("spigot.yml", "/spigot.yml", FileType.CONFIG, 3420),
            ServerFileItem("paper-global.yml", "/config/paper-global.yml", FileType.CONFIG, 5120),
            ServerFileItem("plugins", "/plugins", FileType.FOLDER, 0),
            ServerFileItem("world", "/world", FileType.WORLD_DATA, 0),
            ServerFileItem("logs", "/logs", FileType.FOLDER, 0)
        )
        _serverFiles.value = mapOf(server1.id to s1Files, server2.id to s1Files, server3.id to s1Files)

        // Backups
        val s1Backups = listOf(
            BackupSnapshot("bk-01", server1.id, "Daily Automatic Snapshot - 2026-10-12", 482.4f, "Today 04:00 AM", true),
            BackupSnapshot("bk-02", server1.id, "Pre-Plugin Install Backup", 468.1f, "Yesterday 08:30 PM", false),
            BackupSnapshot("bk-03", server1.id, "Weekly Cloud Archive #41", 450.9f, "Oct 05, 2026", true)
        )
        _serverBackups.value = mapOf(server1.id to s1Backups)

        // Plugins
        val pluginsList = listOf(
            PluginModItem("p-svc", "Simple Voice Chat", "2.5.18", "VOICE", "henkelmax", "Proximity voice chat system with 3D audio & opus compression", true, true, listOf(MinecraftEdition.JAVA, MinecraftEdition.BEDROCK), "4.8M"),
            PluginModItem("p-ess", "EssentialsX", "2.20.1", "ADMIN", "EssentialsX Team", "Core server management, teleportation, kits, and economy", true, true, listOf(MinecraftEdition.JAVA), "12.4M"),
            PluginModItem("p-lp", "LuckPerms", "5.4.102", "ADMIN", "Luck", "An advanced permissions plugin with fast web editor", true, true, listOf(MinecraftEdition.JAVA, MinecraftEdition.BEDROCK), "8.1M"),
            PluginModItem("p-geyser", "GeyserMC Crossplay", "2.4.0", "CROSSPLAY", "GeyserMC", "Enables Bedrock players to join Minecraft Java servers", true, true, listOf(MinecraftEdition.JAVA, MinecraftEdition.BEDROCK), "6.2M"),
            PluginModItem("p-we", "WorldEdit", "7.3.2", "WORLD", "EngineHub", "In-game Minecraft map editor and fast builder", false, false, listOf(MinecraftEdition.JAVA), "15.0M"),
            PluginModItem("p-via", "ViaVersion", "5.0.3", "CROSSPLAY", "ViaVersion", "Allows newer client versions to connect to your server", true, true, listOf(MinecraftEdition.JAVA), "9.5M"),
            PluginModItem("p-chunky", "Chunky Pre-generator", "1.4.10", "PERFORMANCE", "pop4959", "Pre-generates chunks rapidly to eliminate exploration lag", false, false, listOf(MinecraftEdition.JAVA), "3.1M")
        )
        _serverPlugins.value = mapOf(server1.id to pluginsList, server2.id to pluginsList, server3.id to pluginsList)

        // Players
        val s1Players = listOf(
            MinecraftPlayer("uuid-01", "Alex_Crafter", isOnline = true, isOp = true, isWhitelisted = true, pingMs = 18, edition = MinecraftEdition.JAVA, voiceConnected = true),
            MinecraftPlayer("uuid-02", "Steve_Miner", isOnline = true, isOp = false, isWhitelisted = true, pingMs = 24, edition = MinecraftEdition.JAVA, voiceConnected = true),
            MinecraftPlayer("uuid-03", "DiamondSeeker", isOnline = true, isOp = false, isWhitelisted = true, pingMs = 38, edition = MinecraftEdition.JAVA, voiceConnected = true),
            MinecraftPlayer("uuid-04", "RedstoneKing_PE", isOnline = true, isOp = false, isWhitelisted = true, pingMs = 42, edition = MinecraftEdition.BEDROCK, voiceConnected = true),
            MinecraftPlayer("uuid-05", "CreeperDodger", isOnline = true, isOp = false, isWhitelisted = true, pingMs = 55, edition = MinecraftEdition.JAVA, voiceConnected = false),
            MinecraftPlayer("uuid-06", "GrieferTroll", isOnline = false, isOp = false, isWhitelisted = false, isBanned = true, pingMs = 0, edition = MinecraftEdition.JAVA, lastSeen = "2 days ago")
        )
        _serverPlayers.value = mapOf(server1.id to s1Players)

        // Activity Logs
        _activityLogs.value = listOf(
            ActivityLogItem("act-01", "2 mins ago", "Admin", "Server smp.crafthost.gg health check passed (20.0 TPS)", "SERVER", "SUCCESS"),
            ActivityLogItem("act-02", "15 mins ago", "Supervisor", "24/7 Always-Online heartbeats verified on node-us-east-01", "SERVER", "INFO"),
            ActivityLogItem("act-03", "45 mins ago", "User", "Created backup 'Daily Automatic Snapshot - 2026-10-12'", "BACKUP", "INFO"),
            ActivityLogItem("act-04", "2 hours ago", "Voice Gateway", "Spatial audio routing session established for 4 players", "VOICE", "SUCCESS")
        )

        // Initial Crash recovery record
        _crashLogs.value = listOf(
            SupervisorCrashRecoveryLog(
                id = "cr-01",
                serverId = server1.id,
                serverName = server1.name,
                timestampMs = System.currentTimeMillis() - 86400000L,
                cause = "java.lang.OutOfMemoryError: Java heap space (Metaspace leak)",
                exitCode = 137,
                recoveryStep = "Auto-Restart #1 -> Heap auto-tune (+512MB) -> Health check passed (20.0 TPS)",
                statusSuccess = true,
                memoryDumpPath = "/crash-reports/crash-2026-10-11_server.txt"
            )
        )
    }

    private fun startTelemetryLoop() {
        scope.launch {
            while (isActive) {
                delay(3000)
                // Small simulated live fluctuation
                _servers.value = _servers.value.map { srv ->
                    if (srv.status == ServerStatus.ONLINE) {
                        val cpuDelta = Random.nextFloat() * 4f - 2f
                        val newCpu = (srv.cpuUsagePercent + cpuDelta).coerceIn(12f, 85f)
                        val ramDelta = Random.nextFloat() * 30f - 15f
                        val newRam = (srv.ramUsageMb + ramDelta).coerceIn(1800f, srv.resources.ramGb * 1024f)
                        srv.copy(
                            cpuUsagePercent = String.format(Locale.US, "%.1f", newCpu).toFloat(),
                            ramUsageMb = String.format(Locale.US, "%.0f", newRam).toFloat(),
                            uptimeSeconds = srv.uptimeSeconds + 3
                        )
                    } else srv
                }
            }
        }
    }

    // --- Actions ---

    fun selectServer(id: String) {
        _selectedServerId.value = id
    }

    fun startServer(serverId: String) {
        scope.launch {
            updateServerStatus(serverId, ServerStatus.STARTING)
            appendLog(serverId, "[CraftHost Supervisor] Spawning container on node agent...")
            appendLog(serverId, "[CraftHost Supervisor] Persistent storage mounted: /data/servers/$serverId")
            appendLog(serverId, "[CraftHost Supervisor] Starting Minecraft JVM with flags: -Xms2G -Xmx${getRamGb(serverId)}G -XX:+UseG1GC")
            delay(1500)
            appendLog(serverId, "[Server] Loading properties and world chunks...")
            appendLog(serverId, "[Server] Proximity Voice Chat protocol bound to UDP port 24454 (Range: 400 blocks)")
            delay(1000)
            appendLog(serverId, "[Server] Done! Server marked ONLINE.")
            updateServerStatus(serverId, ServerStatus.ONLINE)
            addActivity("User", "Started server $serverId", "SERVER", "SUCCESS")
        }
    }

    fun stopServer(serverId: String) {
        scope.launch {
            updateServerStatus(serverId, ServerStatus.STOPPING)
            appendLog(serverId, "[Server] Stopping server... saving worlds and player data")
            delay(1200)
            appendLog(serverId, "[Server] World saved. Container stopped cleanly.")
            updateServerStatus(serverId, ServerStatus.OFFLINE)
            addActivity("User", "Stopped server $serverId", "SERVER", "INFO")
        }
    }

    fun restartServer(serverId: String) {
        scope.launch {
            updateServerStatus(serverId, ServerStatus.STOPPING)
            appendLog(serverId, "[CraftHost Supervisor] Initiating graceful restart sequence...")
            delay(1000)
            updateServerStatus(serverId, ServerStatus.STARTING)
            appendLog(serverId, "[CraftHost Supervisor] Re-allocating JVM container...")
            delay(1500)
            appendLog(serverId, "[Server] Done! Server restarted successfully.")
            updateServerStatus(serverId, ServerStatus.ONLINE)
            addActivity("User", "Restarted server $serverId", "SERVER", "SUCCESS")
        }
    }

    /**
     * Requirement A: 24/7 Always-Online Mode toggle
     */
    fun toggleAlwaysOnline(serverId: String, enabled: Boolean) {
        _servers.value = _servers.value.map { srv ->
            if (srv.id == serverId) srv.copy(isAlwaysOnline = enabled) else srv
        }
        val statusText = if (enabled) "ENABLED (24/7 Hosting Active)" else "DISABLED (Server sleeps when empty)"
        appendLog(serverId, "[CraftHost Supervisor] 24/7 Always-Online policy changed: $statusText")
        addActivity("User", "Toggled 24/7 Always Online: $statusText", "SERVER", "INFO")
        scope.launch {
            serverDao?.updateAlwaysOnline(serverId, enabled)
        }
    }

    /**
     * Requirement 10: Server Supervisor Crash Watchdog
     * Simulates crash detection, log capture, mark recovering, restart, health check.
     */
    fun triggerSimulatedCrash(serverId: String) {
        scope.launch {
            val server = _servers.value.find { it.id == serverId } ?: return@launch
            updateServerStatus(serverId, ServerStatus.CRASHED)
            appendLog(serverId, "[CRASH DETECTED] Server process terminated abnormally (Exit code: 137 SIGKILL)")
            appendLog(serverId, "[CraftHost Supervisor] Capturing memory dump & crash report to /crash-reports/...")
            delay(1200)

            updateServerStatus(serverId, ServerStatus.RECOVERING)
            appendLog(serverId, "[CraftHost Supervisor] Step 3/8: Analyzing crash cause: Out-of-memory detected in entity tick")
            appendLog(serverId, "[CraftHost Supervisor] Step 4/8: Tuning JVM GC parameters and clearing temporary locks")
            delay(1500)

            appendLog(serverId, "[CraftHost Supervisor] Step 5/8: Restarting container instance...")
            delay(1000)
            appendLog(serverId, "[Server] Spawn area verified. Health check passed (TPS: 20.0).")
            updateServerStatus(serverId, ServerStatus.ONLINE)

            val log = SupervisorCrashRecoveryLog(
                id = "cr-${System.currentTimeMillis()}",
                serverId = server.id,
                serverName = server.name,
                timestampMs = System.currentTimeMillis(),
                cause = "Process OOM Kill (Exit 137)",
                exitCode = 137,
                recoveryStep = "Supervisor Watchdog -> Logs preserved -> Auto-restarted -> Health check PASS",
                statusSuccess = true,
                memoryDumpPath = "/crash-reports/crash_${System.currentTimeMillis()}.txt"
            )
            _crashLogs.value = listOf(log) + _crashLogs.value
            addActivity("Supervisor", "Auto-recovered crashed server ${server.name}", "SERVER", "WARN")
        }
    }

    /**
     * Requirement 14: Console Command Execution
     */
    fun sendConsoleCommand(serverId: String, command: String) {
        val cleanCmd = command.trim()
        if (cleanCmd.isEmpty()) return

        appendLog(serverId, "> $cleanCmd")
        val lower = cleanCmd.lowercase()

        when {
            lower.startsWith("say ") -> {
                val msg = cleanCmd.substringAfter("say ")
                appendLog(serverId, "[Server] [Broadcast] $msg")
            }
            lower.startsWith("op ") -> {
                val player = cleanCmd.substringAfter("op ")
                appendLog(serverId, "[Server] Made $player a server operator")
                togglePlayerOp(serverId, player, true)
            }
            lower.startsWith("deop ") -> {
                val player = cleanCmd.substringAfter("deop ")
                appendLog(serverId, "[Server] Made $player no longer a server operator")
                togglePlayerOp(serverId, player, false)
            }
            lower.startsWith("kick ") -> {
                val player = cleanCmd.substringAfter("kick ")
                appendLog(serverId, "[Server] Kicked $player from the server")
            }
            lower.startsWith("ban ") -> {
                val player = cleanCmd.substringAfter("ban ")
                appendLog(serverId, "[Server] Banned player $player from the server")
                banPlayer(serverId, player)
            }
            lower == "tps" -> {
                appendLog(serverId, "[Server] TPS from last 1m, 5m, 15m: 20.0, 20.0, 19.98")
            }
            lower == "voice status" -> {
                appendLog(serverId, "[SimpleVoiceChat] Voice Gateway: ONLINE | Connected: 4 players | Default Range: 400 blocks | Spatial: TRUE")
            }
            lower.startsWith("time set ") -> {
                appendLog(serverId, "[Server] Set the time to ${cleanCmd.substringAfter("time set ")}")
            }
            lower.startsWith("weather ") -> {
                appendLog(serverId, "[Server] Set the weather to ${cleanCmd.substringAfter("weather ")}")
            }
            else -> {
                appendLog(serverId, "[Server] Unknown command or executed: $cleanCmd. Type 'help' or '/say'.")
            }
        }
    }

    fun clearConsole(serverId: String) {
        _consoleLogs.value = _consoleLogs.value.toMutableMap().apply {
            put(serverId, listOf("[Console cleared by administrator]"))
        }
    }

    // --- Voice Controls ---

    fun setLocalListenerPosition(x: Double, y: Double, z: Double, yaw: Float) {
        _localListenerPos.value = VoicePosition(x, y, z, yaw)
    }

    fun toggleMicrophoneMute() {
        _isMicrophoneMuted.value = !_isMicrophoneMuted.value
    }

    fun setPushToTalkActive(active: Boolean) {
        _isPushToTalkActive.value = active
    }

    fun toggleVoiceServiceSimulatedOutage() {
        // Requirement 28: Voice service failure must NOT stop Minecraft gameplay!
        _voiceServiceHealthy.value = !_voiceServiceHealthy.value
        val state = if (_voiceServiceHealthy.value) "RECONNECTED (ONLINE)" else "TEMPORARILY RECONNECTING (Gameplay Unaffected)"
        appendLog(_selectedServerId.value, "[Voice Gateway] Service status: $state")
    }

    fun updateServerVoiceRange(serverId: String, rangeBlocks: Int) {
        _servers.value = _servers.value.map { srv ->
            if (srv.id == serverId) srv.copy(voiceRangeBlocks = rangeBlocks) else srv
        }
        appendLog(serverId, "[SimpleVoiceChat] Administrator updated voice range to $rangeBlocks blocks")
    }

    // --- File Manager ---

    fun saveFileContent(serverId: String, filePath: String, newContent: String) {
        val files = _serverFiles.value[serverId] ?: emptyList()
        val updated = files.map { file ->
            if (file.path == filePath) file.copy(content = newContent, sizeBytes = newContent.toByteArray().size.toLong())
            else file
        }
        _serverFiles.value = _serverFiles.value.toMutableMap().apply { put(serverId, updated) }
        appendLog(serverId, "[File Manager] Saved file: $filePath")
        addActivity("User", "Edited $filePath", "FILE", "INFO")
    }

    // --- Backups ---

    fun createManualBackup(serverId: String, name: String) {
        val currentBackups = _serverBackups.value[serverId] ?: emptyList()
        val timeStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
        val newBackup = BackupSnapshot(
            id = "bk-${System.currentTimeMillis()}",
            serverId = serverId,
            name = name.ifEmpty { "Manual Snapshot - $timeStr" },
            sizeMb = 478.2f,
            createdAt = timeStr,
            isAutoDaily = false
        )
        _serverBackups.value = _serverBackups.value.toMutableMap().apply {
            put(serverId, listOf(newBackup) + currentBackups)
        }
        appendLog(serverId, "[Backup Service] Created snapshot '${newBackup.name}' and archived to Cloud Storage.")
        addActivity("User", "Created backup: ${newBackup.name}", "BACKUP", "SUCCESS")
    }

    fun restoreBackup(serverId: String, backupId: String) {
        appendLog(serverId, "[Backup Service] Restoring snapshot $backupId... World chunks updated.")
        addActivity("User", "Restored snapshot $backupId", "BACKUP", "WARN")
    }

    fun deleteBackup(serverId: String, backupId: String) {
        val currentBackups = _serverBackups.value[serverId] ?: emptyList()
        _serverBackups.value = _serverBackups.value.toMutableMap().apply {
            put(serverId, currentBackups.filterNot { it.id == backupId })
        }
    }

    // --- Plugins & Mods ---

    fun togglePlugin(serverId: String, pluginId: String, enabled: Boolean) {
        val plugins = _serverPlugins.value[serverId] ?: emptyList()
        val updated = plugins.map { if (it.id == pluginId) it.copy(isEnabled = enabled) else it }
        _serverPlugins.value = _serverPlugins.value.toMutableMap().apply { put(serverId, updated) }
        appendLog(serverId, "[Plugin Manager] Plugin $pluginId toggled: $enabled (Requires restart to apply)")
    }

    fun installPlugin(serverId: String, pluginId: String) {
        val plugins = _serverPlugins.value[serverId] ?: emptyList()
        val updated = plugins.map { if (it.id == pluginId) it.copy(isInstalled = true, isEnabled = true) else it }
        _serverPlugins.value = _serverPlugins.value.toMutableMap().apply { put(serverId, updated) }
        appendLog(serverId, "[Plugin Manager] Downloaded and installed $pluginId.jar")
    }

    // --- Players ---

    fun togglePlayerOp(serverId: String, username: String, isOp: Boolean) {
        val players = _serverPlayers.value[serverId] ?: emptyList()
        val updated = players.map { if (it.username.equals(username, ignoreCase = true)) it.copy(isOp = isOp) else it }
        _serverPlayers.value = _serverPlayers.value.toMutableMap().apply { put(serverId, updated) }
    }

    fun banPlayer(serverId: String, username: String) {
        val players = _serverPlayers.value[serverId] ?: emptyList()
        val updated = players.map { if (it.username.equals(username, ignoreCase = true)) it.copy(isBanned = true, isOnline = false) else it }
        _serverPlayers.value = _serverPlayers.value.toMutableMap().apply { put(serverId, updated) }
    }

    fun unbanPlayer(serverId: String, username: String) {
        val players = _serverPlayers.value[serverId] ?: emptyList()
        val updated = players.map { if (it.username.equals(username, ignoreCase = true)) it.copy(isBanned = false) else it }
        _serverPlayers.value = _serverPlayers.value.toMutableMap().apply { put(serverId, updated) }
        appendLog(serverId, "[Server] Unbanned player $username")
    }

    // --- Server Creation Wizard ---

    fun createNewServer(server: MinecraftServer) {
        _servers.value = _servers.value + server
        _selectedServerId.value = server.id
        scope.launch {
            serverDao?.insertServer(ServerEntity.fromDomainModel(server))
        }
        _consoleLogs.value = _consoleLogs.value.toMutableMap().apply {
            put(server.id, listOf(
                "[CraftHost Supervisor] Server initialized on node ${server.assignedNodeId}",
                "[CraftHost Supervisor] 24/7 Always-Online: ${if (server.isAlwaysOnline) "ENABLED" else "DISABLED"}",
                "[CraftHost Supervisor] Ready to start. Click START."
            ))
        }
        addActivity("User", "Created new server: ${server.name} (${server.edition.displayName})", "SERVER", "SUCCESS")
    }

    fun toggleDemoMode() {
        _isDemoMode.value = !_isDemoMode.value
    }

    // --- Helpers ---

    private fun updateServerStatus(serverId: String, status: ServerStatus) {
        _servers.value = _servers.value.map { if (it.id == serverId) it.copy(status = status) else it }
        scope.launch {
            serverDao?.updateServerStatus(serverId, status)
        }
    }

    private fun appendLog(serverId: String, line: String) {
        val current = _consoleLogs.value[serverId] ?: emptyList()
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val formatted = if (line.startsWith("[") || line.startsWith(">")) line else "[$time INFO] $line"
        _consoleLogs.value = _consoleLogs.value.toMutableMap().apply {
            put(serverId, current + formatted)
        }
    }

    private fun addActivity(user: String, action: String, category: String, level: String) {
        val item = ActivityLogItem(
            id = "act-${System.currentTimeMillis()}",
            timestamp = "Just now",
            user = user,
            action = action,
            category = category,
            level = level
        )
        _activityLogs.value = listOf(item) + _activityLogs.value
    }

    private fun getRamGb(serverId: String): Int {
        return _servers.value.find { it.id == serverId }?.resources?.ramGb ?: 4
    }
}
