package com.example.model

enum class FileType {
    FOLDER,
    CONFIG,
    PROPERTIES,
    JSON,
    JAR,
    LOG,
    WORLD_DATA,
    RESOURCE_PACK,
    ADDON,
    GENERIC
}

data class ServerFileItem(
    val name: String,
    val path: String,
    val type: FileType,
    val sizeBytes: Long = 0,
    val modifiedDate: String = "2026-10-14 14:20",
    val content: String = ""
)

data class BackupSnapshot(
    val id: String,
    val serverId: String,
    val name: String,
    val sizeMb: Float,
    val createdAt: String,
    val isAutoDaily: Boolean = true,
    val storageTier: String = "Cloud S3 Multi-AZ",
    val worldSeed: String = "-4920491823901",
    val includesWorld: Boolean = true,
    val includesPluginsMods: Boolean = true,
    val includesConfigs: Boolean = true,
    val downloadUrl: String = "https://cdn.crafthost.gg/backups/snapshot.zip"
)

enum class WorldImportSource(val label: String, val description: String, val badge: String) {
    ATERNOS_ZIP("Aternos World Export", "Directly import world.zip downloaded from Aternos backup menu", "Aternos"),
    MINEHUT("Minehut World Backup", "Import Minehut server world.zip or save folders", "Minehut"),
    SINGLEPLAYER_ZIP("Singleplayer World", "Upload saves folder from %appdata%/.minecraft/saves", "Singleplayer"),
    MCWORLD_BEDROCK("Bedrock .mcworld", "Direct Bedrock world package with leveldb chunks", "Bedrock"),
    CUSTOM_ZIP("Custom ZIP / TAR.GZ", "Standard Minecraft world folder containing level.dat and region/", "ZIP")
}

data class ServerWorldMetadata(
    val worldName: String = "world",
    val seed: String = "-4920491823901",
    val generatorType: String = "Normal (Default)",
    val chunkCount: Int = 18450,
    val sizeMb: Float = 542.8f,
    val hasNether: Boolean = true,
    val hasTheEnd: Boolean = true,
    val importedFromSource: WorldImportSource? = null,
    val importDate: String? = null,
    val lastBackupDate: String = "Today 04:00 AM"
)

data class ResourcePackItem(
    val id: String,
    val name: String,
    val fileName: String,
    val sizeMb: Float,
    val packFormat: Int = 34,
    val description: String,
    val isServerForced: Boolean = false,
    val isEnabled: Boolean = true,
    val sha1Hash: String = "da39a3ee5e6b4b0d3255bfef95601890afd80709"
)

enum class AddonType(val label: String) {
    BEHAVIOR_PACK("Behavior Pack (BP)"),
    RESOURCE_PACK("Resource Pack (RP)"),
    FULL_ADDON("Combined Add-On (.mcaddon)")
}

data class BedrockAddonItem(
    val id: String,
    val name: String,
    val type: AddonType,
    val version: String,
    val minEngineVersion: String = "1.21.0",
    val description: String,
    val fileName: String,
    val sizeMb: Float,
    val isEnabled: Boolean = true
)

data class PluginModItem(
    val id: String,
    val name: String,
    val version: String,
    val category: String, // "VOICE", "PERFORMANCE", "ADMIN", "WORLD", "CROSSPLAY", "MAGIC", "TECH"
    val author: String,
    val description: String,
    val isInstalled: Boolean,
    val isEnabled: Boolean = true,
    val supportedEditions: List<MinecraftEdition>,
    val downloadsCount: String = "1.2M",
    val officialUrl: String = "https://modrinth.com",
    val modLoader: String = "Fabric / Paper" // "Fabric", "Forge", "NeoForge", "Paper", "Spigot"
)

data class MinecraftPlayer(
    val uuid: String,
    val username: String,
    val isOnline: Boolean = true,
    val isOp: Boolean = false,
    val isWhitelisted: Boolean = true,
    val isBanned: Boolean = false,
    val pingMs: Int = 24,
    val edition: MinecraftEdition = MinecraftEdition.JAVA,
    val lastSeen: String = "Now",
    val voiceConnected: Boolean = true
)
