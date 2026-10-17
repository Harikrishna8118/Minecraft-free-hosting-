package com.example.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class VoiceChannelMode(val label: String) {
    PROXIMITY("Proximity (Default 400 Blocks)"),
    PARTY("Party Private"),
    TEAM("Team Tactical")
}

data class VoicePosition(
    val x: Double,
    val y: Double,
    val z: Double,
    val yawDegrees: Float = 0f,
    val dimension: String = "minecraft:overworld"
)

data class VoiceParticipant(
    val id: String,
    val username: String,
    val edition: MinecraftEdition,
    val position: VoicePosition,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isDeafened: Boolean = false,
    val audioVolume: Float = 1.0f,
    val pingMs: Int = 18,
    val clientType: String = "Java VoiceMod v2.5" // or "Bedrock WebRTC Companion"
)

data class SpatialAudioResult(
    val distanceBlocks: Double,
    val inRange: Boolean,
    val volumeGain: Float, // 0.0f to 1.0f
    val panStereo: Float, // -1.0f (full left) to +1.0f (full right)
    val attenuationDescription: String
)

object SpatialAudioEngine {
    /**
     * Requirement C & 23: Default voice range 400 blocks.
     * Computes distance attenuation and directional left/right stereo positioning
     */
    fun calculate(
        listenerPos: VoicePosition,
        speakerPos: VoicePosition,
        maxRangeBlocks: Int = 400
    ): SpatialAudioResult {
        // Must be in same dimension
        if (listenerPos.dimension != speakerPos.dimension) {
            return SpatialAudioResult(
                distanceBlocks = 9999.0,
                inRange = false,
                volumeGain = 0f,
                panStereo = 0f,
                attenuationDescription = "Different Dimension"
            )
        }

        val dx = speakerPos.x - listenerPos.x
        val dy = speakerPos.y - listenerPos.y
        val dz = speakerPos.z - listenerPos.z
        val distance = sqrt(dx * dx + dy * dy + dz * dz)

        if (distance > maxRangeBlocks) {
            return SpatialAudioResult(
                distanceBlocks = distance,
                inRange = false,
                volumeGain = 0f,
                panStereo = 0f,
                attenuationDescription = "Out of Range (> ${maxRangeBlocks}b)"
            )
        }

        // Distance attenuation: linear dropoff after close radius (15 blocks full volume)
        val closeRadius = 15.0
        val gain = if (distance <= closeRadius) {
            1.0f
        } else {
            val factor = 1.0 - ((distance - closeRadius) / (maxRangeBlocks - closeRadius))
            factor.coerceIn(0.0, 1.0).toFloat()
        }

        // Directional audio relative to listener yaw
        val listenerYawRad = Math.toRadians(listenerPos.yawDegrees.toDouble())
        // Rotate vector into listener coordinate frame
        val localX = dx * cos(listenerYawRad) - dz * sin(listenerYawRad)
        val localZ = dx * sin(listenerYawRad) + dz * cos(listenerYawRad)
        val angleToSpeaker = atan2(localX, localZ)
        val stereoPan = sin(angleToSpeaker).coerceIn(-1.0, 1.0).toFloat()

        val description = when {
            distance < 20 -> "Whisper / Immediate proximity"
            distance < 100 -> "Clear proximity"
            distance < 250 -> "Distant echo"
            else -> "Faint horizon voice"
        }

        return SpatialAudioResult(
            distanceBlocks = distance,
            inRange = true,
            volumeGain = gain,
            panStereo = stereoPan,
            attenuationDescription = description
        )
    }
}

enum class VoiceActivationType(val label: String, val description: String) {
    VOICE_ACTIVITY("Voice Activity (VAD)", "Automatically transmits audio when speaking above the threshold"),
    PUSH_TO_TALK("Push-to-Talk (PTT)", "Transmits audio only while holding the talk key/button")
}

enum class DistanceAttenuationModel(val label: String, val formula: String) {
    LINEAR("Linear Falloff", "Uniform sound reduction proportional to block distance"),
    LOGARITHMIC("Logarithmic (Natural)", "Realistic acoustic dropoff mimicking real-world sound physics"),
    EXPONENTIAL("Exponential (Rapid)", "Steep decay outside immediate vicinity")
}

data class VoiceChatSettings(
    val attenuationDistanceBlocks: Int = 400,
    val fullVolumeRadiusBlocks: Int = 15,
    val micSensitivityPercent: Float = 60f,
    val micGainPercent: Float = 100f,
    val outputVolumePercent: Float = 100f,
    val voiceActivationType: VoiceActivationType = VoiceActivationType.VOICE_ACTIVITY,
    val attenuationModel: DistanceAttenuationModel = DistanceAttenuationModel.LOGARITHMIC,
    val whisperEnabled: Boolean = true,
    val shoutEnabled: Boolean = true,
    val blockObstructionMuffling: Boolean = true,
    val noiseSuppression: Boolean = true,
    val echoCancellation: Boolean = true,
    val audioBitrateKbps: Int = 48,
    val defaultChannel: VoiceChannelMode = VoiceChannelMode.PROXIMITY
)

