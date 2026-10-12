package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MinecraftEdition
import com.example.model.ServerStatus
import com.example.ui.components.EditionBadge
import com.example.ui.components.ServerStatusBadge
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    onCreateServerClick: () -> Unit,
    onExploreDashboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F261D),
                                ObsidianDarkBg
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Tagline Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF064E3B))
                            .border(1.dp, EmeraldLight.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldLight))
                            Text(
                                text = "NEXT-GEN 24/7 MINECRAFT CLOUD",
                                color = EmeraldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Main Title
                    Text(
                        text = "Your Minecraft Server.\nAlways Online.",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp
                    )

                    // Subtitle
                    Text(
                        text = "Host, manage, monitor, and play on your Minecraft server with built-in 400-block proximity voice chat. Never sleeps when you leave.",
                        color = TextDarkSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onCreateServerClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Server", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onExploreDashboardClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(SlateDarkBorder, CyanLight))),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dashboard", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Live Interactive Server Preview Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "LIVE PLATFORM PREVIEW",
                    color = TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(14.dp))
                        .clickable { onExploreDashboardClick() }
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldContainerDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Dns, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("Survival SMP 1.21", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("smp.crafthost.gg:25565", color = CyanLight, fontSize = 12.sp)
                                }
                            }
                            ServerStatusBadge(status = ServerStatus.ONLINE)
                        }

                        // Badges Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditionBadge(edition = MinecraftEdition.JAVA)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF064E3B))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("24/7 ALWAYS ONLINE", color = Color(0xFFA7F3D0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanContainerDark)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("400b VOICE CHAT", color = CyanLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        HorizontalDivider(color = SlateDarkBorder)

                        // Live Spec Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PreviewMetricItem(label = "TPS", value = "20.0", color = EmeraldLight)
                            PreviewMetricItem(label = "PLAYERS", value = "5 / 24", color = TextDarkPrimary)
                            PreviewMetricItem(label = "CPU", value = "28.4%", color = TextDarkPrimary)
                            PreviewMetricItem(label = "UPTIME", value = "96h 24m", color = CyanLight)
                        }
                    }
                }
            }
        }

        // Core Pillars Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "WHY CRAFTHOST?",
                    color = TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                FeatureCard(
                    icon = Icons.Default.AllInclusive,
                    title = "Continuous 24/7 Hosting",
                    description = "Unlike Aternos, your server never shuts down because of player inactivity or closed browser tabs. Runs on dedicated node agents.",
                    highlightColor = EmeraldLight
                )

                FeatureCard(
                    icon = Icons.Default.RecordVoiceOver,
                    title = "Integrated Proximity Voice Chat",
                    description = "Spatial 3D Opus audio with 400-block range for Java & Bedrock. Hear players naturally in stereo based on in-game coordinates.",
                    highlightColor = CyanLight
                )

                FeatureCard(
                    icon = Icons.Default.Devices,
                    title = "Full Java + Bedrock Crossplay",
                    description = "Deploy PaperMC, Fabric, Forge, or Official Bedrock Dedicated Server (BDS) with built-in Geyser crossplay support.",
                    highlightColor = AmberAccent
                )

                FeatureCard(
                    icon = Icons.Default.SecurityUpdateGood,
                    title = "Self-Healing Supervisor Watchdog",
                    description = "Automated crash detection, memory dump capture, and zero-downtime container auto-restart with exponential backoff.",
                    highlightColor = EmeraldPrimary
                )

                FeatureCard(
                    icon = Icons.Default.CloudSync,
                    title = "Automated S3 Backups & File Manager",
                    description = "Daily snapshots, instant ZIP world downloads, full syntax editor for server.properties, whitelist.json, and ops.",
                    highlightColor = CyanSecondary
                )
            }
        }

        // Pricing Tiers Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "TRANSPARENT PLANS",
                    color = TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                PlanCard(
                    planName = "Community Free",
                    price = "$0 / mo",
                    specs = listOf("2 GB DDR5 RAM", "1 vCPU Core", "15 GB Fast Storage", "Always Online (Queue-free)", "200b Voice Chat Range"),
                    isPopular = false,
                    onSelect = onCreateServerClick
                )

                PlanCard(
                    planName = "Survival Pro (Recommended)",
                    price = "$4.99 / mo",
                    specs = listOf("6 GB DDR5 RAM", "3 vCPU Cores", "45 GB NVMe Storage", "24/7 Always Online Guaranteed", "400b Proximity Voice Chat", "Daily Automated Backups", "Crossplay Geyser Pre-configured"),
                    isPopular = true,
                    onSelect = onCreateServerClick
                )

                PlanCard(
                    planName = "Network Ultra",
                    price = "$14.99 / mo",
                    specs = listOf("16 GB DDR5 RAM", "8 vCPU Cores", "120 GB NVMe Storage", "Priority Node Placement", "Unlimited Voice Users", "Sub-15ms Voice Gateway", "Hourly Backups + Dedicated IP"),
                    isPopular = false,
                    onSelect = onCreateServerClick
                )
            }
        }

        // FAQ Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "FREQUENTLY ASKED QUESTIONS",
                    color = TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                FaqAccordion(
                    question = "Does the server stop when nobody is playing?",
                    answer = "No! With CraftHost Always-Online mode active, your server remains online 24/7 on our cluster nodes. It does not shut down when players disconnect or when your browser/app closes."
                )

                FaqAccordion(
                    question = "How does the proximity voice chat work?",
                    answer = "We provide a lightweight Fabric/Forge mod or Paper plugin for Java, and a companion WebRTC bridge for Bedrock. Our Voice Service calculates player 3D coordinates and delivers low-latency stereo audio within 400 blocks."
                )

                FaqAccordion(
                    question = "Can Bedrock and Java players talk to each other?",
                    answer = "Yes! Our voice gateway bridges both Java and Bedrock players into the same spatial audio coordinate grid, so crossplay servers have seamless voice chat."
                )
            }
        }
    }
}

@Composable
private fun PreviewMetricItem(label: String, value: String, color: Color) {
    Column {
        Text(text = label, color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    highlightColor: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(highlightColor.copy(alpha = 0.15f))
                    .border(1.dp, highlightColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = highlightColor, modifier = Modifier.size(22.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = description, color = TextDarkSecondary, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

@Composable
private fun PlanCard(
    planName: String,
    price: String,
    specs: List<String>,
    isPopular: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) Color(0xFF14241E) else SlateDarkCard
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isPopular) EmeraldPrimary else SlateDarkBorder,
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = planName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = price, color = if (isPopular) EmeraldLight else CyanLight, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
                if (isPopular) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(EmeraldPrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("MOST POPULAR", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            specs.forEach { spec ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                    Text(text = spec, color = TextDarkPrimary, fontSize = 12.sp)
                }
            }

            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) EmeraldPrimary else SlateDarkSurface,
                    contentColor = if (isPopular) Color.Black else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text("Select $planName", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun FaqAccordion(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = question, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextDarkSecondary
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(text = answer, color = TextDarkSecondary, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
