package com.example.model

import java.util.UUID

enum class ServerStatus(val displayName: String) {
    ONLINE("Online"),
    STARTING("Starting..."),
    STOPPING("Stopping..."),
    OFFLINE("Offline"),
    CRASHED("Crashed"),
    RECOVERING("Auto-Recovering")
}

enum class MinecraftEdition(val displayName: String, val defaultPort: Int) {
    JAVA("Java Edition", 25565),
    BEDROCK("Bedrock Edition", 19132)
}

enum class ServerSoftware(val displayName: String, val edition: MinecraftEdition, val tag: String) {
    // Java
    PAPER("PaperMC", MinecraftEdition.JAVA, "High Performance"),
    PURPUR("Purpur", MinecraftEdition.JAVA, "Feature Rich"),
    FABRIC("Fabric", MinecraftEdition.JAVA, "Lightweight Mods"),
    FORGE("Forge", MinecraftEdition.JAVA, "Heavy Modpacks"),
    NEOFORGE("NeoForge", MinecraftEdition.JAVA, "Next-Gen Modding"),
    SPIGOT("Spigot", MinecraftEdition.JAVA, "Standard Plugins"),
    VANILLA_JAVA("Vanilla", MinecraftEdition.JAVA, "Official Mojang"),
    // Bedrock
    BEDROCK_DEDICATED("Bedrock Dedicated Server (BDS)", MinecraftEdition.BEDROCK, "Official Microsoft"),
    GEYSER_STANDALONE("Geyser Standalone", MinecraftEdition.BEDROCK, "Java Crossplay"),
    POCKETMINE("PocketMine-MP", MinecraftEdition.BEDROCK, "Fast PHP Core")
}

enum class ServerRegion(val displayName: String, val flag: String, val pingMs: Int) {
    US_EAST("North America (Virginia)", "🇺🇸", 24),
    US_WEST("North America (Oregon)", "🇺🇸", 45),
    EU_CENTRAL("Europe (Frankfurt)", "🇩🇪", 38),
    AP_EAST("Asia Pacific (Tokyo)", "🇯🇵", 62),
    SA_EAST("South America (São Paulo)", "🇧🇷", 88)
}

data class ServerResourceConfig(
    val ramGb: Int = 4,
    val vCpu: Int = 2,
    val storageGb: Int = 25,
    val maxBackups: Int = 5
)

data class MinecraftServer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val motd: String = "§aA CraftHost Server §7- §b24/7 Always Online",
    val edition: MinecraftEdition = MinecraftEdition.JAVA,
    val software: ServerSoftware = ServerSoftware.PAPER,
    val version: String = "1.21.4",
    val region: ServerRegion = ServerRegion.US_EAST,
    val address: String = "play.crafthost.gg",
    val port: Int = 25565,
    val status: ServerStatus = ServerStatus.ONLINE,
    val isAlwaysOnline: Boolean = true, // Requirement A: 24/7 hosting independent of app
    val resources: ServerResourceConfig = ServerResourceConfig(),
    val assignedNodeId: String = "node-us-east-01",
    val onlinePlayers: Int = 4,
    val maxPlayers: Int = 20,
    val cpuUsagePercent: Float = 24.5f,
    val ramUsageMb: Float = 2140f,
    val diskUsageGb: Float = 6.8f,
    val tps: Float = 20.0f,
    val uptimeSeconds: Long = 172800L, // 48 hours continuous
    val voiceEnabled: Boolean = true,
    val voiceRangeBlocks: Int = 400, // Requirement C: 400 blocks default
    val gamemode: String = "survival",
    val difficulty: String = "normal",
    val pvpEnabled: Boolean = true,
    val whitelistEnabled: Boolean = false,
    val spawnProtection: Int = 16,
    val viewDistance: Int = 12,
    val simulationDistance: Int = 8,
    val onlineModeMojang: Boolean = true,
    val lastCrashTimestamp: Long? = null,
    val crashCount: Int = 0
)
