package com.example.data.local

import androidx.room.*
import com.example.model.MinecraftEdition
import com.example.model.ServerStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing persistent Minecraft server states in Room database.
 */
@Dao
interface MinecraftServerDao {

    @Query("SELECT * FROM minecraft_servers ORDER BY createdAt DESC")
    fun getAllServers(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM minecraft_servers WHERE id = :id LIMIT 1")
    fun getServerByIdFlow(id: String): Flow<ServerEntity?>

    @Query("SELECT * FROM minecraft_servers WHERE id = :id LIMIT 1")
    suspend fun getServerById(id: String): ServerEntity?

    @Query("SELECT * FROM minecraft_servers WHERE status = :status ORDER BY name ASC")
    fun getServersByStatus(status: ServerStatus): Flow<List<ServerEntity>>

    @Query("SELECT * FROM minecraft_servers WHERE edition = :edition ORDER BY name ASC")
    fun getServersByEdition(edition: MinecraftEdition): Flow<List<ServerEntity>>

    @Query("SELECT * FROM minecraft_servers WHERE isAlwaysOnline = 1 ORDER BY name ASC")
    fun getAlwaysOnlineServers(): Flow<List<ServerEntity>>

    @Query("SELECT COUNT(*) FROM minecraft_servers")
    fun getServerCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<ServerEntity>)

    @Update
    suspend fun updateServer(server: ServerEntity)

    @Query("UPDATE minecraft_servers SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateServerStatus(
        id: String,
        status: ServerStatus,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE minecraft_servers SET isAlwaysOnline = :isAlwaysOnline, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateAlwaysOnline(
        id: String,
        isAlwaysOnline: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE minecraft_servers SET ramGb = :ramGb, vCpu = :vCpu, storageGb = :storageGb, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateResourceConfig(
        id: String,
        ramGb: Int,
        vCpu: Int,
        storageGb: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE minecraft_servers SET cpuUsagePercent = :cpu, ramUsageMb = :ram, tps = :tps, uptimeSeconds = :uptime, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTelemetry(
        id: String,
        cpu: Float,
        ram: Float,
        tps: Float,
        uptime: Long,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE minecraft_servers SET voiceEnabled = :enabled, voiceRangeBlocks = :rangeBlocks, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateVoiceConfig(
        id: String,
        enabled: Boolean,
        rangeBlocks: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Delete
    suspend fun deleteServer(server: ServerEntity)

    @Query("DELETE FROM minecraft_servers WHERE id = :id")
    suspend fun deleteServerById(id: String)

    @Query("DELETE FROM minecraft_servers")
    suspend fun deleteAllServers()
}
