package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.*

/**
 * Room database entity representing a persistent Minecraft server instance and its state.
 */
@Entity(tableName = "minecraft_servers")
data class ServerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "motd")
    val motd: String = "§aA CraftHost Server §7- §b24/7 Always Online",

    @ColumnInfo(name = "edition")
    val edition: MinecraftEdition = MinecraftEdition.JAVA,

    @ColumnInfo(name = "software")
    val software: ServerSoftware = ServerSoftware.PAPER,

    @ColumnInfo(name = "version")
    val version: String = "1.21.4",

    @ColumnInfo(name = "region")
    val region: ServerRegion = ServerRegion.US_EAST,

    @ColumnInfo(name = "address")
    val address: String = "play.crafthost.gg",

    @ColumnInfo(name = "port")
    val port: Int = 25565,

    @ColumnInfo(name = "status")
    val status: ServerStatus = ServerStatus.ONLINE,

    // Requirement A: 24/7 Hosting indicator
    @ColumnInfo(name = "isAlwaysOnline")
    val isAlwaysOnline: Boolean = true,

    // Resource Configuration
    @ColumnInfo(name = "ramGb")
    val ramGb: Int = 4,

    @ColumnInfo(name = "vCpu")
    val vCpu: Int = 2,

    @ColumnInfo(name = "storageGb")
    val storageGb: Int = 25,

    @ColumnInfo(name = "maxBackups")
    val maxBackups: Int = 5,

    // Cluster Node & Telemetry
    @ColumnInfo(name = "assignedNodeId")
    val assignedNodeId: String = "node-us-east-01",

    @ColumnInfo(name = "onlinePlayers")
    val onlinePlayers: Int = 0,

    @ColumnInfo(name = "maxPlayers")
    val maxPlayers: Int = 20,

    @ColumnInfo(name = "cpuUsagePercent")
    val cpuUsagePercent: Float = 0.0f,

    @ColumnInfo(name = "ramUsageMb")
    val ramUsageMb: Float = 0.0f,

    @ColumnInfo(name = "diskUsageGb")
    val diskUsageGb: Float = 0.0f,

    @ColumnInfo(name = "tps")
    val tps: Float = 20.0f,

    @ColumnInfo(name = "uptimeSeconds")
    val uptimeSeconds: Long = 0L,

    // Proximity Voice Configuration (Default: 400 blocks)
    @ColumnInfo(name = "voiceEnabled")
    val voiceEnabled: Boolean = true,

    @ColumnInfo(name = "voiceRangeBlocks")
    val voiceRangeBlocks: Int = 400,

    // Minecraft Gameplay Settings
    @ColumnInfo(name = "gamemode")
    val gamemode: String = "survival",

    @ColumnInfo(name = "difficulty")
    val difficulty: String = "normal",

    @ColumnInfo(name = "pvpEnabled")
    val pvpEnabled: Boolean = true,

    @ColumnInfo(name = "whitelistEnabled")
    val whitelistEnabled: Boolean = false,

    @ColumnInfo(name = "spawnProtection")
    val spawnProtection: Int = 16,

    @ColumnInfo(name = "viewDistance")
    val viewDistance: Int = 12,

    @ColumnInfo(name = "simulationDistance")
    val simulationDistance: Int = 8,

    @ColumnInfo(name = "onlineModeMojang")
    val onlineModeMojang: Boolean = true,

    @ColumnInfo(name = "crashCount")
    val crashCount: Int = 0,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Map Entity to UI/Domain Model
     */
    fun toDomainModel(): MinecraftServer {
        return MinecraftServer(
            id = id,
            name = name,
            motd = motd,
            edition = edition,
            software = software,
            version = version,
            region = region,
            address = address,
            port = port,
            status = status,
            isAlwaysOnline = isAlwaysOnline,
            resources = ServerResourceConfig(
                ramGb = ramGb,
                vCpu = vCpu,
                storageGb = storageGb,
                maxBackups = maxBackups
            ),
            assignedNodeId = assignedNodeId,
            onlinePlayers = onlinePlayers,
            maxPlayers = maxPlayers,
            cpuUsagePercent = cpuUsagePercent,
            ramUsageMb = ramUsageMb,
            diskUsageGb = diskUsageGb,
            tps = tps,
            uptimeSeconds = uptimeSeconds,
            voiceEnabled = voiceEnabled,
            voiceRangeBlocks = voiceRangeBlocks,
            gamemode = gamemode,
            difficulty = difficulty,
            pvpEnabled = pvpEnabled,
            whitelistEnabled = whitelistEnabled,
            spawnProtection = spawnProtection,
            viewDistance = viewDistance,
            simulationDistance = simulationDistance,
            onlineModeMojang = onlineModeMojang,
            crashCount = crashCount
        )
    }

    companion object {
        /**
         * Create Entity from UI/Domain Model
         */
        fun fromDomainModel(model: MinecraftServer): ServerEntity {
            return ServerEntity(
                id = model.id,
                name = model.name,
                motd = model.motd,
                edition = model.edition,
                software = model.software,
                version = model.version,
                region = model.region,
                address = model.address,
                port = model.port,
                status = model.status,
                isAlwaysOnline = model.isAlwaysOnline,
                ramGb = model.resources.ramGb,
                vCpu = model.resources.vCpu,
                storageGb = model.resources.storageGb,
                maxBackups = model.resources.maxBackups,
                assignedNodeId = model.assignedNodeId,
                onlinePlayers = model.onlinePlayers,
                maxPlayers = model.maxPlayers,
                cpuUsagePercent = model.cpuUsagePercent,
                ramUsageMb = model.ramUsageMb,
                diskUsageGb = model.diskUsageGb,
                tps = model.tps,
                uptimeSeconds = model.uptimeSeconds,
                voiceEnabled = model.voiceEnabled,
                voiceRangeBlocks = model.voiceRangeBlocks,
                gamemode = model.gamemode,
                difficulty = model.difficulty,
                pvpEnabled = model.pvpEnabled,
                whitelistEnabled = model.whitelistEnabled,
                spawnProtection = model.spawnProtection,
                viewDistance = model.viewDistance,
                simulationDistance = model.simulationDistance,
                onlineModeMojang = model.onlineModeMojang,
                crashCount = model.crashCount,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
