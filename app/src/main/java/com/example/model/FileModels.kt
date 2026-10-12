package com.example.model

enum class FileType {
    FOLDER,
    CONFIG,
    PROPERTIES,
    JSON,
    JAR,
    LOG,
    WORLD_DATA,
    GENERIC
}

data class ServerFileItem(
    val name: String,
    val path: String,
    val type: FileType,
    val sizeBytes: Long = 0,
    val modifiedDate: String = "2026-10-12 11:24",
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
    val worldSeed: String = "-4920491823901"
)

data class PluginModItem(
    val id: String,
    val name: String,
    val version: String,
    val category: String, // "VOICE", "PERFORMANCE", "ADMIN", "WORLD", "CROSSPLAY"
    val author: String,
    val description: String,
    val isInstalled: Boolean,
    val isEnabled: Boolean = true,
    val supportedEditions: List<MinecraftEdition>,
    val downloadsCount: String = "1.2M",
    val officialUrl: String = "https://modrinth.com"
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
