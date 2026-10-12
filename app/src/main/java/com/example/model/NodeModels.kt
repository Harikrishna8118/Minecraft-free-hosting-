package com.example.model

enum class NodeHealth(val label: String) {
    HEALTHY("Healthy / Optimal"),
    WARNING("High Memory / Warning"),
    MAINTENANCE("Scheduled Drain"),
    OFFLINE("Node Unreachable")
}

data class ServerNode(
    val id: String,
    val name: String,
    val region: ServerRegion,
    val ipAddress: String,
    val health: NodeHealth = NodeHealth.HEALTHY,
    val totalRamGb: Int = 128,
    val usedRamGb: Int = 74,
    val totalCores: Int = 32,
    val cpuUsagePercent: Float = 34.2f,
    val totalStorageGb: Int = 2000,
    val usedStorageGb: Int = 890,
    val runningContainers: Int = 18,
    val maxContainers: Int = 32,
    val nodeAgentVersion: String = "crafthost-agent-v3.4.1",
    val uptimeDays: Int = 142
)

data class SupervisorCrashRecoveryLog(
    val id: String,
    val serverId: String,
    val serverName: String,
    val timestampMs: Long,
    val cause: String,
    val exitCode: Int,
    val recoveryStep: String,
    val statusSuccess: Boolean,
    val memoryDumpPath: String
)

data class ActivityLogItem(
    val id: String,
    val timestamp: String,
    val user: String,
    val action: String,
    val category: String, // "SERVER", "VOICE", "BACKUP", "FILE"
    val level: String = "INFO" // "INFO", "WARN", "SUCCESS"
)
