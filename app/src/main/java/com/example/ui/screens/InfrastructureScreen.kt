package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NodeHealth
import com.example.model.ServerNode
import com.example.model.SupervisorCrashRecoveryLog
import com.example.ui.components.ResourceLinearBar
import com.example.ui.theme.*

@Composable
fun InfrastructureScreen(
    nodes: List<ServerNode>,
    crashLogs: List<SupervisorCrashRecoveryLog>,
    onTriggerTestCrash: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Control Plane Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Cluster Infrastructure & Supervisor",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Control Plane • Isolated Docker Containers • Node Supervisor Watchdog",
                    color = EmeraldLight,
                    fontSize = 11.sp
                )
            }
        }

        // Architecture Pipeline Diagram Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("INFRASTRUCTURE ARCHITECTURE PIPELINE", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Text(
                        text = "User Client → Web App / Mobile → Backend API → Control Plane → Node Agent → Isolated Container → Minecraft Server",
                        color = CyanLight,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Compute Nodes
        item {
            Text("COMPUTE WORKER NODES (${nodes.size})", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        items(nodes) { node ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (node.health == NodeHealth.HEALTHY) EmeraldLight else AmberAccent)
                            )
                            Column {
                                Text(node.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${node.ipAddress} • ${node.nodeAgentVersion}", color = TextDarkMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF064E3B))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("${node.runningContainers} Containers", color = EmeraldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    ResourceLinearBar(
                        label = "RAM Capacity",
                        valueText = "${node.usedRamGb} / ${node.totalRamGb} GB",
                        progress = node.usedRamGb.toFloat() / node.totalRamGb,
                        color = CyanLight
                    )

                    ResourceLinearBar(
                        label = "CPU Aggregate Load",
                        valueText = "${node.cpuUsagePercent}% (${node.totalCores} Cores)",
                        progress = node.cpuUsagePercent / 100f,
                        color = EmeraldLight
                    )

                    ResourceLinearBar(
                        label = "Storage Pool",
                        valueText = "${node.usedStorageGb} / ${node.totalStorageGb} GB NVMe",
                        progress = node.usedStorageGb.toFloat() / node.totalStorageGb,
                        color = AmberAccent
                    )
                }
            }
        }

        // Supervisor Auto-Recovery Watchdog Logs
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SUPERVISOR CRASH WATCHDOG LOGS", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = onTriggerTestCrash,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F1A1A), contentColor = RedstoneRed),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(RedstoneRed)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Test Crash Watchdog", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (crashLogs.isEmpty()) {
                        Text("No crash events recorded. Cluster operating normally.", color = TextDarkSecondary, fontSize = 12.sp)
                    } else {
                        crashLogs.forEach { cl ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateDarkSurface)
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cl.serverName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Exit Code: ${cl.exitCode}", color = AmberAccent, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                                Text(cl.cause, color = RedstoneRed, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text(cl.recoveryStep, color = EmeraldLight, fontSize = 11.sp)
                                Text("Dump: ${cl.memoryDumpPath}", color = TextDarkMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
