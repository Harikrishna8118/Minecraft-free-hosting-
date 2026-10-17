package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.MinecraftEdition
import com.example.model.ServerRegion
import com.example.model.ServerSoftware
import com.example.model.ServerStatus

/**
 * TypeConverter specifically for the Minecraft server edition enum.
 */
class ServerEditionConverter {

    @TypeConverter
    fun fromEdition(edition: MinecraftEdition?): String {
        return edition?.name ?: MinecraftEdition.JAVA.name
    }

    @TypeConverter
    fun toEdition(value: String?): MinecraftEdition {
        return try {
            if (value != null) MinecraftEdition.valueOf(value) else MinecraftEdition.JAVA
        } catch (_: Exception) {
            MinecraftEdition.JAVA
        }
    }
}

/**
 * TypeConverters for status, software core, and node region enums.
 */
class RoomConverters {

    @TypeConverter
    fun fromStatus(status: ServerStatus?): String {
        return status?.name ?: ServerStatus.OFFLINE.name
    }

    @TypeConverter
    fun toStatus(value: String?): ServerStatus {
        return try {
            if (value != null) ServerStatus.valueOf(value) else ServerStatus.OFFLINE
        } catch (_: Exception) {
            ServerStatus.OFFLINE
        }
    }

    @TypeConverter
    fun fromSoftware(software: ServerSoftware?): String {
        return software?.name ?: ServerSoftware.PAPER.name
    }

    @TypeConverter
    fun toSoftware(value: String?): ServerSoftware {
        return try {
            if (value != null) ServerSoftware.valueOf(value) else ServerSoftware.PAPER
        } catch (_: Exception) {
            ServerSoftware.PAPER
        }
    }

    @TypeConverter
    fun fromRegion(region: ServerRegion?): String {
        return region?.name ?: ServerRegion.US_EAST.name
    }

    @TypeConverter
    fun toRegion(value: String?): ServerRegion {
        return try {
            if (value != null) ServerRegion.valueOf(value) else ServerRegion.US_EAST
        } catch (_: Exception) {
            ServerRegion.US_EAST
        }
    }
}
