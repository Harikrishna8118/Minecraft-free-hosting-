package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VoiceRadarView(
    listenerPos: VoicePosition,
    participants: List<VoiceParticipant>,
    maxVoiceRangeBlocks: Int = 400,
    isMuted: Boolean,
    isPushToTalkActive: Boolean,
    onToggleMute: () -> Unit,
    onPushToTalkChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for active radar scan
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAngle"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SlateDarkCard)
            .border(1.dp, SlateDarkBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Radar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyanLight))
                    Text(
                        text = "PROXIMITY RADAR (400b Boundary)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Listener at (${listenerPos.x.toInt()}, ${listenerPos.y.toInt()}, ${listenerPos.z.toInt()}) in ${listenerPos.dimension}",
                    color = TextDarkMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyanContainerDark)
                    .border(1.dp, CyanLight.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Opus 48kHz Stereo",
                    color = CyanLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Radar Canvas (Concentric circles representing 100b, 200b, 400b voice border)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF070B0E))
                .border(1.dp, Color(0xFF131D24), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = (size.minDimension / 2f) - 16.dp.toPx()

                // Radar scale: maxRadius = 500 blocks
                val scale = maxRadius / 500f

                // Distance ring: 100 blocks
                drawCircle(
                    color = Color(0xFF1A2633),
                    radius = 100f * scale,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Distance ring: 200 blocks
                drawCircle(
                    color = Color(0xFF1E2F3F),
                    radius = 200f * scale,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Proximity Voice Boundary Ring: 400 blocks (Highlighted Cyan)
                drawCircle(
                    color = CyanSecondary.copy(alpha = 0.6f),
                    radius = 400f * scale,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
                // Crosshair axes
                drawLine(
                    color = Color(0xFF1A2633),
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF1A2633),
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1.dp.toPx()
                )

                // Sweep line
                val rad = Math.toRadians(sweepAngle.toDouble())
                val sweepEnd = Offset(
                    (center.x + (maxRadius * cos(rad))).toFloat(),
                    (center.y + (maxRadius * sin(rad))).toFloat()
                )
                drawLine(
                    color = CyanLight.copy(alpha = 0.35f),
                    start = center,
                    end = sweepEnd,
                    strokeWidth = 2.dp.toPx()
                )

                // Draw Listener (Center player)
                drawCircle(
                    color = EmeraldPrimary,
                    radius = 7.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = center
                )

                // Draw Participants
                participants.forEach { p ->
                    val dx = (p.position.x - listenerPos.x).toFloat()
                    val dz = (p.position.z - listenerPos.z).toFloat()

                    val pX = center.x + (dx * scale)
                    val pZ = center.y + (dz * scale)
                    val targetOffset = Offset(pX, pZ)

                    val audio = SpatialAudioEngine.calculate(listenerPos, p.position, maxVoiceRangeBlocks)

                    if (audio.inRange) {
                        // In Range Player
                        if (p.isSpeaking) {
                            // Speaking wave circle
                            drawCircle(
                                color = CyanLight.copy(alpha = 0.4f),
                                radius = 12.dp.toPx(),
                                center = targetOffset,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                        drawCircle(
                            color = if (p.isSpeaking) CyanLight else EmeraldLight,
                            radius = 6.dp.toPx(),
                            center = targetOffset
                        )
                    } else {
                        // Out of Range Player (Dimmed red/gray)
                        drawCircle(
                            color = Color(0xFF475569),
                            radius = 5.dp.toPx(),
                            center = targetOffset
                        )
                    }
                }
            }

            // Legend Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldPrimary))
                    Text("You", color = TextDarkSecondary, fontSize = 10.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyanLight))
                    Text("In Range (<400b)", color = TextDarkSecondary, fontSize = 10.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF475569)))
                    Text("Out of Range", color = TextDarkSecondary, fontSize = 10.sp)
                }
            }
        }

        // Voice Controls: Mute, PTT Button, Mode Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onToggleMute,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMuted) RedstoneRed else SlateDarkSurface,
                    contentColor = if (isMuted) Color.White else TextDarkPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .border(1.dp, if (isMuted) RedstoneRed else SlateDarkBorder, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute Microphone",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isMuted) "Unmute" else "Mute Mic", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }

            // Push-to-Talk (Hold to talk)
            Button(
                onClick = {
                    onPushToTalkChange(!isPushToTalkActive)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPushToTalkActive) CyanSecondary else EmeraldPrimary,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1.4f)
                    .height(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Push To Talk",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPushToTalkActive) "TRANSMITTING..." else "PUSH TO TALK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Nearby Players Audio Calculations Table
        Text(
            text = "SPATIAL AUDIO TELEMETRY",
            color = TextDarkMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        participants.forEach { p ->
            val audio = SpatialAudioEngine.calculate(listenerPos, p.position, maxVoiceRangeBlocks)
            ParticipantAudioCard(participant = p, audio = audio)
        }
    }
}

@Composable
private fun ParticipantAudioCard(participant: VoiceParticipant, audio: SpatialAudioResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDarkSurface)
            .border(1.dp, if (audio.inRange) SlateDarkBorder else Color(0xFF1F242B), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = participant.username,
                    color = if (audio.inRange) TextDarkPrimary else TextDarkMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                EditionBadge(edition = participant.edition)
                if (participant.isSpeaking && audio.inRange) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF064E3B))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text("SPEAKING", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "${audio.distanceBlocks.toInt()} blocks away • ${audio.attenuationDescription}",
                color = if (audio.inRange) CyanLight else TextDarkMuted,
                fontSize = 11.sp
            )
        }

        // Panning / Volume telemetry
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = if (audio.inRange) "Vol: ${(audio.volumeGain * 100).toInt()}%" else "MUTED (0%)",
                color = if (audio.inRange) EmeraldLight else Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            val panText = when {
                audio.panStereo < -0.3f -> "Pan: LEFT"
                audio.panStereo > 0.3f -> "Pan: RIGHT"
                else -> "Pan: CENTER"
            }
            Text(
                text = if (audio.inRange) panText else "Out of 400b",
                color = TextDarkSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
