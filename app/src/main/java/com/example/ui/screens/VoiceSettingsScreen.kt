package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.testTag
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.ln
import kotlin.math.pow
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsScreen(
    currentSettings: VoiceChatSettings,
    onSaveSettings: (VoiceChatSettings) -> Unit,
    onResetSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    var attenuationDistance by remember(currentSettings) { mutableFloatStateOf(currentSettings.attenuationDistanceBlocks.toFloat()) }
    var fullVolumeRadius by remember(currentSettings) { mutableFloatStateOf(currentSettings.fullVolumeRadiusBlocks.toFloat()) }
    var micSensitivity by remember(currentSettings) { mutableFloatStateOf(currentSettings.micSensitivityPercent) }
    var micGain by remember(currentSettings) { mutableFloatStateOf(currentSettings.micGainPercent) }
    var outputVolume by remember(currentSettings) { mutableFloatStateOf(currentSettings.outputVolumePercent) }
    var activationType by remember(currentSettings) { mutableStateOf(currentSettings.voiceActivationType) }
    var attenuationModel by remember(currentSettings) { mutableStateOf(currentSettings.attenuationModel) }
    var whisperEnabled by remember(currentSettings) { mutableStateOf(currentSettings.whisperEnabled) }
    var shoutEnabled by remember(currentSettings) { mutableStateOf(currentSettings.shoutEnabled) }
    var blockMuffling by remember(currentSettings) { mutableStateOf(currentSettings.blockObstructionMuffling) }
    var noiseSuppression by remember(currentSettings) { mutableStateOf(currentSettings.noiseSuppression) }
    var echoCancellation by remember(currentSettings) { mutableStateOf(currentSettings.echoCancellation) }
    var audioBitrate by remember(currentSettings) { mutableIntStateOf(currentSettings.audioBitrateKbps) }
    var defaultChannel by remember(currentSettings) { mutableStateOf(currentSettings.defaultChannel) }

    var isMicTesting by remember { mutableStateOf(false) }
    var simulatedMicLevel by remember { mutableFloatStateOf(0f) }
    var showSavedToast by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Simulated live mic meter fluctuating when test is active
    LaunchedEffect(isMicTesting) {
        if (isMicTesting) {
            while (isActive) {
                delay(75)
                // Generate natural voice burst fluctuations between 20% and 90%
                val base = 40f + Random.nextFloat() * 45f
                simulatedMicLevel = (base * (micGain / 100f)).coerceIn(5f, 100f)
            }
        } else {
            simulatedMicLevel = 0f
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, tint = AmberAccent)
                    Text("Reset Voice Settings?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    "This will restore all proximity voice parameters, attenuation distance (400 blocks), input sensitivity, and output volume back to official defaults.",
                    color = TextDarkSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetSettings()
                        showResetDialog = false
                        attenuationDistance = 400f
                        fullVolumeRadius = 15f
                        micSensitivity = 60f
                        micGain = 100f
                        outputVolume = 100f
                        activationType = VoiceActivationType.VOICE_ACTIVITY
                        attenuationModel = DistanceAttenuationModel.LOGARITHMIC
                        whisperEnabled = true
                        shoutEnabled = true
                        blockMuffling = true
                        noiseSuppression = true
                        echoCancellation = true
                        audioBitrate = 48
                        defaultChannel = VoiceChannelMode.PROXIMITY
                        showSavedToast = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = Color.Black)
                ) {
                    Text("Reset to Defaults", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel", color = TextDarkSecondary) }
            },
            containerColor = SlateDarkCard
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Voice Chat Settings", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Proximity & Spatial Audio Parameters", color = CyanLight, fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("voice_settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("voice_settings_reset_button")
                    ) {
                        Text("Reset Defaults", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDarkBg)
            )
        },
        containerColor = ObsidianDarkBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Save confirmation banner
            if (showSavedToast) {
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldContainerDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                                Text("Voice parameters updated and synced to server!", color = EmeraldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { showSavedToast = false }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // 1. Proximity & Attenuation Distance Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CyanLight.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Hearing, contentDescription = null, tint = CyanLight, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text("PROXIMITY ATTENUATION", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("Distance Falloff & Acoustical Curve", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SlateDarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanLight)
                            ) {
                                Text(
                                    "${attenuationDistance.toInt()} Blocks",
                                    color = CyanLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            "Maximum block distance before a player's voice fades completely to silent. Standard servers use 400 blocks.",
                            color = TextDarkSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        // Attenuation Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Attenuation Max Distance", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("${attenuationDistance.toInt()}b", color = CyanLight, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = attenuationDistance,
                                onValueChange = { attenuationDistance = it },
                                valueRange = 50f..800f,
                                steps = 14,
                                colors = SliderDefaults.colors(thumbColor = CyanLight, activeTrackColor = CyanSecondary),
                                modifier = Modifier.testTag("attenuation_distance_slider")
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("50b (Whisper Tavern)", color = TextDarkMuted, fontSize = 9.sp)
                                Text("400b (Standard)", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("800b (Grand Open Realm)", color = TextDarkMuted, fontSize = 9.sp)
                            }
                        }

                        // Presets
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Tavern (100b)" to 100f,
                                "Standard (400b)" to 400f,
                                "Kingdoms (600b)" to 600f,
                                "Massive (800b)" to 800f
                            ).forEach { (label, dist) ->
                                val isSel = attenuationDistance == dist
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) CyanLight else SlateDarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanLight else SlateDarkBorder),
                                    modifier = Modifier.clickable { attenuationDistance = dist }
                                ) {
                                    Text(
                                        label,
                                        color = if (isSel) Color.Black else Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = SlateDarkBorder)

                        // Full Volume Core Radius
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Full-Volume Core Radius", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("Distance where volume remains 100% before falloff", color = TextDarkMuted, fontSize = 10.sp)
                                }
                                Text("${fullVolumeRadius.toInt()}b", color = EmeraldLight, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = fullVolumeRadius,
                                onValueChange = { fullVolumeRadius = it },
                                valueRange = 5f..50f,
                                colors = SliderDefaults.colors(thumbColor = EmeraldLight, activeTrackColor = EmeraldPrimary),
                                modifier = Modifier.testTag("full_volume_radius_slider")
                            )
                        }

                        HorizontalDivider(color = SlateDarkBorder)

                        // Attenuation Formula Model
                        Text("Distance Attenuation Physics Curve:", color = TextDarkSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        DistanceAttenuationModel.values().forEach { model ->
                            val isSel = attenuationModel == model
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) SlateDarkSurface else Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanLight else SlateDarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { attenuationModel = model }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSel,
                                        onClick = { attenuationModel = model },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyanLight)
                                    )
                                    Column {
                                        Text(model.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(model.formula, color = TextDarkSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        // Live Falloff Curve Visualization Canvas
                        Text("LIVE FALLOFF CURVE VISUALIZER:", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        AttenuationCurveCanvas(
                            maxRange = attenuationDistance,
                            coreRadius = fullVolumeRadius,
                            model = attenuationModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TerminalBg)
                                .border(1.dp, SlateDarkBorder, RoundedCornerShape(8.dp))
                        )

                        // Extra environmental switches
                        HorizontalDivider(color = SlateDarkBorder)

                        SettingsSwitchRow(
                            title = "Solid Block Obstruction Muffling",
                            desc = "Dampen frequencies and reduce volume when stone or terrain blocks separate players",
                            checked = blockMuffling,
                            onCheckedChange = { blockMuffling = it }
                        )

                        SettingsSwitchRow(
                            title = "Crouch Whisper Mechanics",
                            desc = "Sneaking drops voice range to 15 blocks for stealth conversations",
                            checked = whisperEnabled,
                            onCheckedChange = { whisperEnabled = it }
                        )

                        SettingsSwitchRow(
                            title = "Sprint Shouting Mechanics",
                            desc = "Sprinting amplifies voice range by 1.5x for long-distance rallying calls",
                            checked = shoutEnabled,
                            onCheckedChange = { shoutEnabled = it }
                        )
                    }
                }
            }

            // 2. Microphone Input Sensitivity & Activation Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldLight.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text("INPUT SENSITIVITY & MIC", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("Noise Gate & Activation Threshold", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (simulatedMicLevel >= micSensitivity) EmeraldContainerDark else SlateDarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (simulatedMicLevel >= micSensitivity) EmeraldLight else SlateDarkBorder)
                            ) {
                                Text(
                                    if (isMicTesting) {
                                        if (simulatedMicLevel >= micSensitivity) "GATE OPEN" else "GATE CLOSED"
                                    } else "READY",
                                    color = if (simulatedMicLevel >= micSensitivity) EmeraldLight else TextDarkSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Activation Mode Selection
                        Text("Voice Transmission Method:", color = TextDarkSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            VoiceActivationType.values().forEach { type ->
                                val isSel = activationType == type
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) EmeraldPrimary else SlateDarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) EmeraldLight else SlateDarkBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { activationType = type }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            type.label,
                                            color = if (isSel) Color.Black else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            if (type == VoiceActivationType.VOICE_ACTIVITY) "Automatic VAD" else "Hold key to talk",
                                            color = if (isSel) Color(0xFF064E3B) else TextDarkMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = SlateDarkBorder)

                        // Input Sensitivity Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Microphone Input Sensitivity", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("Audio level threshold to open the transmission gate", color = TextDarkMuted, fontSize = 10.sp)
                                }
                                Text("${micSensitivity.toInt()}%", color = EmeraldLight, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = micSensitivity,
                                onValueChange = { micSensitivity = it },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = EmeraldLight, activeTrackColor = EmeraldPrimary),
                                modifier = Modifier.testTag("mic_sensitivity_slider")
                            )
                        }

                        // Live Audio Level Meter
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("LIVE INPUT LEVEL METER", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { isMicTesting = !isMicTesting },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isMicTesting) RedstoneRed.copy(alpha = 0.2f) else SlateDarkSurface,
                                        contentColor = if (isMicTesting) RedstoneRed else CyanLight
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isMicTesting) RedstoneRed else CyanLight),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("test_mic_button").height(28.dp)
                                ) {
                                    Icon(
                                        if (isMicTesting) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isMicTesting) "Stop Test" else "Test Mic", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Visual VU Meter bar with Gate Threshold Line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TerminalBg)
                                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(6.dp))
                            ) {
                                // Active signal fill
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(simulatedMicLevel / 100f)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    TerminalGreen,
                                                    TerminalYellow,
                                                    if (simulatedMicLevel > 85f) TerminalRed else TerminalYellow
                                                )
                                            )
                                        )
                                )

                                // Sensitivity Gate Marker
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(micSensitivity / 100f),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .fillMaxHeight()
                                            .background(Color.White)
                                    )
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("-60 dB (Quiet)", color = TextDarkMuted, fontSize = 9.sp)
                                Text("Threshold Marker (${micSensitivity.toInt()}%)", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("0 dB (Peaking)", color = TextDarkMuted, fontSize = 9.sp)
                            }
                        }

                        HorizontalDivider(color = SlateDarkBorder)

                        // Mic Gain Boost
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Microphone Amplification / Gain", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("Digital boost for quiet microphone hardware", color = TextDarkMuted, fontSize = 10.sp)
                                }
                                Text("${micGain.toInt()}%", color = CyanLight, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = micGain,
                                onValueChange = { micGain = it },
                                valueRange = 50f..200f,
                                colors = SliderDefaults.colors(thumbColor = CyanLight, activeTrackColor = CyanSecondary),
                                modifier = Modifier.testTag("mic_gain_slider")
                            )
                        }

                        HorizontalDivider(color = SlateDarkBorder)

                        // Voice Pre-processing Toggles
                        SettingsSwitchRow(
                            title = "AI Noise Suppression (RNNoise)",
                            desc = "Real-time background noise filtering for mechanical keyboards, fans, and clicks",
                            checked = noiseSuppression,
                            onCheckedChange = { noiseSuppression = it }
                        )

                        SettingsSwitchRow(
                            title = "Acoustic Echo Cancellation (AEC)",
                            desc = "Prevents speaker audio feedback loop when playing without headphones",
                            checked = echoCancellation,
                            onCheckedChange = { echoCancellation = it }
                        )
                    }
                }
            }

            // 3. Output Volume & Audio Codec Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(AmberAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text("OUTPUT VOLUME & CODEC", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("Master Playback & Codec Quality", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SlateDarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent)
                            ) {
                                Text(
                                    "${outputVolume.toInt()}%",
                                    color = AmberAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Master Output Volume Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Master Proximity Output Volume", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("${outputVolume.toInt()}%", color = AmberAccent, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    if (outputVolume > 100f) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = RedstoneRed.copy(alpha = 0.2f)) {
                                            Text("BOOST", color = RedstoneRed, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                            }
                            Slider(
                                value = outputVolume,
                                onValueChange = { outputVolume = it },
                                valueRange = 0f..200f,
                                colors = SliderDefaults.colors(thumbColor = AmberAccent, activeTrackColor = AmberAccent),
                                modifier = Modifier.testTag("output_volume_slider")
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mute (0%)", color = TextDarkMuted, fontSize = 9.sp)
                                Text("100% (Normal)", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("200% (High Boost)", color = RedstoneRed, fontSize = 9.sp)
                            }
                        }

                        // Quick volume buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(50f, 100f, 150f, 200f).forEach { vol ->
                                val isSel = outputVolume == vol
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSel) AmberAccent else SlateDarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AmberAccent else SlateDarkBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { outputVolume = vol }
                                ) {
                                    Text(
                                        "${vol.toInt()}%",
                                        color = if (isSel) Color.Black else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = SlateDarkBorder)

                        // Opus Bitrate Selection
                        Text("Opus Codec Audio Bitrate:", color = TextDarkSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                16 to "16 kbps (Economy)",
                                24 to "24 kbps (Balanced)",
                                48 to "48 kbps (Crisp Default)",
                                64 to "64 kbps (High Fidelity)",
                                96 to "96 kbps (Studio)"
                            ).forEach { (kbps, label) ->
                                val isSel = audioBitrate == kbps
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) CyanLight else SlateDarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanLight else SlateDarkBorder),
                                    modifier = Modifier.clickable { audioBitrate = kbps }
                                ) {
                                    Text(
                                        label,
                                        color = if (isSel) Color.Black else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Save & Apply Actions Button
            item {
                Button(
                    onClick = {
                        val updated = VoiceChatSettings(
                            attenuationDistanceBlocks = attenuationDistance.toInt(),
                            fullVolumeRadiusBlocks = fullVolumeRadius.toInt(),
                            micSensitivityPercent = micSensitivity,
                            micGainPercent = micGain,
                            outputVolumePercent = outputVolume,
                            voiceActivationType = activationType,
                            attenuationModel = attenuationModel,
                            whisperEnabled = whisperEnabled,
                            shoutEnabled = shoutEnabled,
                            blockObstructionMuffling = blockMuffling,
                            noiseSuppression = noiseSuppression,
                            echoCancellation = echoCancellation,
                            audioBitrateKbps = audioBitrate,
                            defaultChannel = defaultChannel
                        )
                        onSaveSettings(updated)
                        showSavedToast = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                    modifier = Modifier
                        .testTag("save_voice_settings_button")
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Apply Parameters", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(desc, color = TextDarkSecondary, fontSize = 10.sp, lineHeight = 14.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EmeraldLight,
                checkedTrackColor = EmeraldContainerDark
            )
        )
    }
}

/**
 * Custom Canvas drawing the mathematical distance attenuation curve
 */
@Composable
private fun AttenuationCurveCanvas(
    maxRange: Float,
    coreRadius: Float,
    model: DistanceAttenuationModel,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 16f

        val graphWidth = width - (padding * 2)
        val graphHeight = height - (padding * 2)

        // Draw grid lines
        drawLine(
            color = Color(0xFF2A3A47),
            start = Offset(padding, padding + graphHeight / 2),
            end = Offset(width - padding, padding + graphHeight / 2),
            strokeWidth = 1f
        )

        // Draw core radius vertical line
        val coreX = padding + (coreRadius / maxRange) * graphWidth
        drawLine(
            color = Color(0xFF10B981).copy(alpha = 0.5f),
            start = Offset(coreX, padding),
            end = Offset(coreX, padding + graphHeight),
            strokeWidth = 2f
        )

        // Draw attenuation curve path
        val curvePath = Path()
        val steps = 60
        for (i in 0..steps) {
            val fraction = i / steps.toFloat()
            val currentDist = fraction * maxRange

            // Calculate gain based on model
            val gain = if (currentDist <= coreRadius) {
                1.0f
            } else {
                when (model) {
                    DistanceAttenuationModel.LINEAR -> {
                        (1f - ((currentDist - coreRadius) / (maxRange - coreRadius))).coerceIn(0f, 1f)
                    }
                    DistanceAttenuationModel.LOGARITHMIC -> {
                        val factor = 1.0 - (ln((currentDist - coreRadius + 1.0)) / ln((maxRange - coreRadius + 1.0)))
                        factor.coerceIn(0.0, 1.0).toFloat()
                    }
                    DistanceAttenuationModel.EXPONENTIAL -> {
                        val norm = (currentDist - coreRadius) / (maxRange - coreRadius)
                        (1.0 - norm.toDouble().pow(0.5)).coerceIn(0.0, 1.0).toFloat()
                    }
                }
            }

            val x = padding + fraction * graphWidth
            val y = padding + (1f - gain) * graphHeight

            if (i == 0) {
                curvePath.moveTo(x, y)
            } else {
                curvePath.lineTo(x, y)
            }
        }

        drawPath(
            path = curvePath,
            color = Color(0xFF38BDF8),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
