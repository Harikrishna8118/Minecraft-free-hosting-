package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.VoiceRadarView
import com.example.ui.theme.*

@Composable
fun VoiceHubScreen(
    listenerPos: VoicePosition,
    participants: List<VoiceParticipant>,
    isMuted: Boolean,
    isPushToTalkActive: Boolean,
    voiceServiceHealthy: Boolean,
    voiceSettings: VoiceChatSettings = VoiceChatSettings(),
    onToggleMute: () -> Unit,
    onPushToTalkChange: (Boolean) -> Unit,
    onUpdateListenerCoords: (Double, Double, Double, Float) -> Unit,
    onToggleServiceOutage: () -> Unit,
    onOpenVoiceSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var listenerX by remember { mutableStateOf(listenerPos.x.toFloat()) }
    var listenerZ by remember { mutableStateOf(listenerPos.z.toFloat()) }
    var listenerYaw by remember { mutableStateOf(listenerPos.yawDegrees) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Voice Gateway Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Proximity Voice Service",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Opus Low-Latency Gateway • ${voiceSettings.attenuationDistanceBlocks}b Spatial Attenuation",
                        color = CyanLight,
                        fontSize = 11.sp
                    )
                }

                // Voice Service Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (voiceServiceHealthy) Color(0xFF064E3B) else Color(0xFF451A03))
                        .border(
                            1.dp,
                            if (voiceServiceHealthy) EmeraldLight.copy(alpha = 0.5f) else AmberAccent,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (voiceServiceHealthy) EmeraldLight else AmberAccent)
                        )
                        Text(
                            text = if (voiceServiceHealthy) "GATEWAY ONLINE" else "RECONNECTING",
                            color = if (voiceServiceHealthy) Color(0xFFA7F3D0) else AmberAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Voice Parameters Quick Settings Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(CyanLight.copy(alpha = 0.5f), EmeraldLight.copy(alpha = 0.5f))),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldContainerDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                            }
                            Column {
                                Text("VOICE PARAMETERS", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Proximity Distance & Audio Gate", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = onOpenVoiceSettings,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Configure", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        VoiceParamPill("Attenuation", "${voiceSettings.attenuationDistanceBlocks} Blocks", Icons.Default.Hearing)
                        VoiceParamPill("Mic Gate", "${voiceSettings.micSensitivityPercent.toInt()}%", Icons.Default.Mic)
                        VoiceParamPill("Output Vol", "${voiceSettings.outputVolumePercent.toInt()}%", Icons.Default.VolumeUp)
                    }
                }
            }
        }

        // Radar Canvas & Spatial Calculations
        item {
            VoiceRadarView(
                listenerPos = listenerPos,
                participants = participants,
                maxVoiceRangeBlocks = voiceSettings.attenuationDistanceBlocks,
                isMuted = isMuted,
                isPushToTalkActive = isPushToTalkActive,
                onToggleMute = onToggleMute,
                onPushToTalkChange = onPushToTalkChange
            )
        }

        // Interactive Spatial Audio Test Bench (Move Listener Coordinates)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SPATIAL POSITION TEST BENCH",
                        color = TextDarkMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Drag coordinates to test 3D distance falloff and left/right stereo panning relative to nearby players.",
                        color = TextDarkSecondary,
                        fontSize = 12.sp
                    )

                    // X coordinate
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Listener X Coordinate", color = TextDarkPrimary, fontSize = 12.sp)
                        Text("${listenerX.toInt()} blocks", color = CyanLight, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = listenerX,
                        onValueChange = {
                            listenerX = it
                            onUpdateListenerCoords(listenerX.toDouble(), 64.0, listenerZ.toDouble(), listenerYaw)
                        },
                        valueRange = 0f..500f,
                        colors = SliderDefaults.colors(thumbColor = CyanLight, activeTrackColor = CyanSecondary)
                    )

                    // Z coordinate
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Listener Z Coordinate", color = TextDarkPrimary, fontSize = 12.sp)
                        Text("${listenerZ.toInt()} blocks", color = CyanLight, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = listenerZ,
                        onValueChange = {
                            listenerZ = it
                            onUpdateListenerCoords(listenerX.toDouble(), 64.0, listenerZ.toDouble(), listenerYaw)
                        },
                        valueRange = 0f..500f,
                        colors = SliderDefaults.colors(thumbColor = CyanLight, activeTrackColor = CyanSecondary)
                    )
                }
            }
        }

        // Bridges: Java Mod Bridge & Bedrock Companion Status
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "CLIENT INTEGRATION BRIDGES",
                        color = TextDarkMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IntegrationStatusRow(
                        title = "Java Edition Bridge",
                        desc = "Fabric / Forge / Paper Simple Voice Chat v2.5 (UDP 24454)",
                        status = "SYNCED",
                        isOk = true
                    )

                    IntegrationStatusRow(
                        title = "Bedrock Edition Bridge",
                        desc = "Bedrock Script API Gateway + WebRTC Mobile Companion",
                        status = "SYNCED",
                        isOk = true
                    )
                }
            }
        }

        // Fault Isolation Demonstration (Requirement 28)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161F26)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Voice Fault Isolation Guarantee", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "Requirement 28: If the voice gateway experiences a transient outage, the Minecraft server process is completely isolated and gameplay never stops.",
                                color = TextDarkSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onToggleServiceOutage,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (voiceServiceHealthy) Color(0xFF3F3F46) else EmeraldPrimary,
                            contentColor = if (voiceServiceHealthy) Color.White else Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (voiceServiceHealthy) "Simulate Voice Outage" else "Restore Voice Gateway", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun IntegrationStatusRow(title: String, desc: String, status: String, isOk: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(desc, color = TextDarkSecondary, fontSize = 11.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isOk) Color(0xFF064E3B) else Color(0xFF451A03))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(status, color = if (isOk) EmeraldLight else AmberAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun VoiceParamPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SlateDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = CyanLight, modifier = Modifier.size(15.dp))
            Column {
                Text(label, color = TextDarkMuted, fontSize = 9.sp)
                Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}
