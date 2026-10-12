package com.example

import com.example.model.SpatialAudioEngine
import com.example.model.VoicePosition
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun proximityVoice_withinRange_isHeard() {
        val listener = VoicePosition(x = 100.0, y = 64.0, z = 100.0, yawDegrees = 0f)
        val speaker = VoicePosition(x = 150.0, y = 64.0, z = 100.0, yawDegrees = 0f)

        val result = SpatialAudioEngine.calculate(listener, speaker, maxRangeBlocks = 400)

        assertEquals(50.0, result.distanceBlocks, 0.01)
        assertTrue("Speaker within 400 blocks must be in range", result.inRange)
        assertTrue("Volume gain must be greater than 0", result.volumeGain > 0.8f)
    }

    @Test
    fun proximityVoice_beyond400Blocks_isMuted() {
        val listener = VoicePosition(x = 100.0, y = 64.0, z = 100.0, yawDegrees = 0f)
        val speaker = VoicePosition(x = 550.0, y = 64.0, z = 100.0, yawDegrees = 0f) // 450 blocks away

        val result = SpatialAudioEngine.calculate(listener, speaker, maxRangeBlocks = 400)

        assertEquals(450.0, result.distanceBlocks, 0.01)
        assertFalse("Speaker beyond 400 blocks must be muted / out of range", result.inRange)
        assertEquals(0.0f, result.volumeGain, 0.001f)
    }

    @Test
    fun proximityVoice_differentDimension_isMuted() {
        val listener = VoicePosition(x = 100.0, y = 64.0, z = 100.0, dimension = "minecraft:overworld")
        val speaker = VoicePosition(x = 100.0, y = 64.0, z = 100.0, dimension = "minecraft:the_nether")

        val result = SpatialAudioEngine.calculate(listener, speaker, maxRangeBlocks = 400)

        assertFalse("Cross-dimension speaker must not be heard", result.inRange)
        assertEquals(0.0f, result.volumeGain, 0.001f)
    }

    @Test
    fun proximityVoice_stereoDirectionalPanning() {
        val listener = VoicePosition(x = 100.0, y = 64.0, z = 100.0, yawDegrees = 0f)
        // Speaker directly to listener's right (+X when facing +Z)
        val speakerRight = VoicePosition(x = 140.0, y = 64.0, z = 100.0)
        val resultRight = SpatialAudioEngine.calculate(listener, speakerRight, maxRangeBlocks = 400)

        assertTrue("Stereo pan must bias towards right channel", resultRight.panStereo > 0f)

        // Speaker directly to listener's left (-X)
        val speakerLeft = VoicePosition(x = 60.0, y = 64.0, z = 100.0)
        val resultLeft = SpatialAudioEngine.calculate(listener, speakerLeft, maxRangeBlocks = 400)

        assertTrue("Stereo pan must bias towards left channel", resultLeft.panStereo < 0f)
    }
}
