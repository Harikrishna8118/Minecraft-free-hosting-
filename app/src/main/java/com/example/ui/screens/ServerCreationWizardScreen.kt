package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.AlwaysOnlineTag
import com.example.ui.components.EditionBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ServerCreationWizardScreen(
    onServerCreated: (MinecraftServer) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }

    // Wizard Form State
    var serverName by remember { mutableStateOf("My Minecraft Realm") }
    var selectedEdition by remember { mutableStateOf(MinecraftEdition.JAVA) }
    var selectedVersion by remember { mutableStateOf("1.21.4") }
    var selectedSoftware by remember { mutableStateOf(ServerSoftware.PAPER) }
    var ramGb by remember { mutableIntStateOf(4) }
    var vCpu by remember { mutableIntStateOf(2) }
    var storageGb by remember { mutableIntStateOf(25) }
    var selectedRegion by remember { mutableStateOf(ServerRegion.US_EAST) }
    var voiceEnabled by remember { mutableStateOf(true) }
    var voiceRangeBlocks by remember { mutableIntStateOf(400) } // Default 400 blocks
    var alwaysOnline by remember { mutableStateOf(true) } // 24/7 Hosting active

    // Step 9 Progress State
    var progressStep by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Wizard Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Server Creation Wizard",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Step $currentStep of 9 • ${getStepTitle(currentStep)}",
                    color = EmeraldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextDarkSecondary)
            }
        }

        // Stepper Progress Line
        LinearProgressIndicator(
            progress = { currentStep / 9f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = EmeraldPrimary,
            trackColor = SlateDarkBorder
        )

        // Step Content Body
        Box(modifier = Modifier.weight(1f)) {
            when (currentStep) {
                1 -> Step1ServerName(name = serverName, onNameChange = { serverName = it })
                2 -> Step2Edition(
                    selected = selectedEdition,
                    onSelect = {
                        selectedEdition = it
                        selectedSoftware = if (it == MinecraftEdition.JAVA) ServerSoftware.PAPER else ServerSoftware.BEDROCK_DEDICATED
                    }
                )
                3 -> Step3Version(selected = selectedVersion, edition = selectedEdition, onSelect = { selectedVersion = it })
                4 -> Step4Software(selected = selectedSoftware, edition = selectedEdition, onSelect = { selectedSoftware = it })
                5 -> Step5Resources(
                    ram = ramGb,
                    cpu = vCpu,
                    storage = storageGb,
                    onRamChange = { ramGb = it },
                    onCpuChange = { vCpu = it },
                    onStorageChange = { storageGb = it }
                )
                6 -> Step6Region(selected = selectedRegion, onSelect = { selectedRegion = it })
                7 -> Step7VoiceChat(
                    voiceEnabled = voiceEnabled,
                    rangeBlocks = voiceRangeBlocks,
                    alwaysOnline = alwaysOnline,
                    onVoiceToggle = { voiceEnabled = it },
                    onRangeChange = { voiceRangeBlocks = it },
                    onAlwaysOnlineToggle = { alwaysOnline = it }
                )
                8 -> Step8Review(
                    name = serverName,
                    edition = selectedEdition,
                    version = selectedVersion,
                    software = selectedSoftware,
                    ram = ramGb,
                    cpu = vCpu,
                    storage = storageGb,
                    region = selectedRegion,
                    voiceEnabled = voiceEnabled,
                    voiceRange = voiceRangeBlocks,
                    alwaysOnline = alwaysOnline
                )
                9 -> Step9CreationProgress(step = progressStep)
            }
        }

        // Bottom Navigation Buttons
        if (currentStep < 9) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (currentStep == 8) {
                            currentStep = 9
                            // Run Step 9 Creation Sequence
                            coroutineScope.launch {
                                for (s in 1..4) {
                                    delay(900)
                                    progressStep = s
                                }
                                delay(600)
                                val newServer = MinecraftServer(
                                    id = "srv-${UUID.randomUUID().toString().take(8)}",
                                    name = serverName,
                                    edition = selectedEdition,
                                    software = selectedSoftware,
                                    version = selectedVersion,
                                    region = selectedRegion,
                                    address = "${serverName.lowercase().replace(" ", "-")}.crafthost.gg",
                                    port = selectedEdition.defaultPort,
                                    status = ServerStatus.ONLINE,
                                    isAlwaysOnline = alwaysOnline,
                                    resources = ServerResourceConfig(ramGb = ramGb, vCpu = vCpu, storageGb = storageGb),
                                    onlinePlayers = 0,
                                    maxPlayers = 20,
                                    tps = 20.0f,
                                    voiceEnabled = voiceEnabled,
                                    voiceRangeBlocks = voiceRangeBlocks
                                )
                                onServerCreated(newServer)
                            }
                        } else {
                            currentStep++
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                ) {
                    Text(if (currentStep == 8) "Launch Server 24/7" else "Continue", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun getStepTitle(step: Int) = when (step) {
    1 -> "Server Identity"
    2 -> "Minecraft Edition"
    3 -> "Game Version"
    4 -> "Server Software"
    5 -> "Resource Quotas"
    6 -> "Node Region"
    7 -> "Proximity Voice & 24/7"
    8 -> "Review & Deploy"
    9 -> "Allocating Compute Node"
    else -> ""
}

@Composable
private fun Step1ServerName(name: String, onNameChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("What would you like to call your server?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Pick a friendly name. You can change this at any time in server settings.", color = TextDarkSecondary, fontSize = 13.sp)

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Server Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateDarkCard,
                unfocusedContainerColor = SlateDarkCard,
                focusedBorderColor = EmeraldPrimary,
                unfocusedBorderColor = SlateDarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = TextDarkPrimary
            )
        )
    }
}

@Composable
private fun Step2Edition(selected: MinecraftEdition, onSelect: (MinecraftEdition) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Select Minecraft Edition", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        EditionOptionCard(
            title = "Java Edition",
            subtitle = "For PC, Mac, & Linux. Rich plugin & mod ecosystem (Paper, Fabric, Forge).",
            isSelected = selected == MinecraftEdition.JAVA,
            accentColor = EmeraldPrimary,
            onClick = { onSelect(MinecraftEdition.JAVA) }
        )

        EditionOptionCard(
            title = "Bedrock Edition",
            subtitle = "For Mobile (iOS/Android), Windows, Xbox, PlayStation, & Nintendo Switch.",
            isSelected = selected == MinecraftEdition.BEDROCK,
            accentColor = CyanSecondary,
            onClick = { onSelect(MinecraftEdition.BEDROCK) }
        )
    }
}

@Composable
private fun EditionOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SlateDarkCardHover else SlateDarkCard
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                if (isSelected) accentColor else SlateDarkBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = accentColor)
            )
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = subtitle, color = TextDarkSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun Step3Version(selected: String, edition: MinecraftEdition, onSelect: (String) -> Unit) {
    val versions = if (edition == MinecraftEdition.JAVA) {
        listOf("1.21.4 (Latest)", "1.21.1", "1.20.4", "1.19.4", "1.18.2", "1.16.5")
    } else {
        listOf("1.21.50 (Latest)", "1.21.40", "1.21.30", "1.20.80")
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Select Minecraft Version", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("We always recommend the latest release for security and performance.", color = TextDarkSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(versions.size) { idx ->
            val v = versions[idx].substringBefore(" ")
            val label = versions[idx]
            val isSelected = selected.startsWith(v)

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateDarkCardHover else SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isSelected) EmeraldPrimary else SlateDarkBorder, RoundedCornerShape(10.dp))
                    .clickable { onSelect(v) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, color = if (isSelected) EmeraldLight else Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Step4Software(selected: ServerSoftware, edition: MinecraftEdition, onSelect: (ServerSoftware) -> Unit) {
    val options = ServerSoftware.values().filter { it.edition == edition }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Select Server Software Core", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Optimized binaries tuned for high tickrates and proximity voice chat.", color = TextDarkSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(options.size) { idx ->
            val soft = options[idx]
            val isSelected = selected == soft

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateDarkCardHover else SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isSelected) EmeraldPrimary else SlateDarkBorder, RoundedCornerShape(10.dp))
                    .clickable { onSelect(soft) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = soft.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = soft.tag, color = EmeraldLight, fontSize = 11.sp)
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun Step5Resources(
    ram: Int,
    cpu: Int,
    storage: Int,
    onRamChange: (Int) -> Unit,
    onCpuChange: (Int) -> Unit,
    onStorageChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Allocate Node Resources", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        // RAM Slider
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
            modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Memory (RAM)", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("$ram GB DDR5", color = EmeraldLight, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = ram.toFloat(),
                    onValueChange = { onRamChange(it.toInt()) },
                    valueRange = 2f..16f,
                    steps = 6,
                    colors = SliderDefaults.colors(thumbColor = EmeraldLight, activeTrackColor = EmeraldPrimary)
                )
                Text("Recommended: 4GB for Survival, 8GB+ for Fabric/Forge modpacks.", color = TextDarkMuted, fontSize = 11.sp)
            }
        }

        // CPU Slider
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
            modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Compute Cores", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("$cpu vCPU Cores", color = CyanLight, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = cpu.toFloat(),
                    onValueChange = { onCpuChange(it.toInt()) },
                    valueRange = 1f..8f,
                    steps = 6,
                    colors = SliderDefaults.colors(thumbColor = CyanLight, activeTrackColor = CyanSecondary)
                )
            }
        }

        // Storage
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
            modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("NVMe Fast Storage", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("$storage GB", color = AmberAccent, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = storage.toFloat(),
                    onValueChange = { onStorageChange(it.toInt()) },
                    valueRange = 15f..100f,
                    steps = 16,
                    colors = SliderDefaults.colors(thumbColor = AmberAccent, activeTrackColor = AmberAccent)
                )
            }
        }
    }
}

@Composable
private fun Step6Region(selected: ServerRegion, onSelect: (ServerRegion) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Select Global Server Node Region", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        ServerRegion.values().forEach { reg ->
            val isSelected = selected == reg
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateDarkCardHover else SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isSelected) EmeraldPrimary else SlateDarkBorder, RoundedCornerShape(10.dp))
                    .clickable { onSelect(reg) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(reg.flag, fontSize = 20.sp)
                        Column {
                            Text(reg.displayName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Latency ~${reg.pingMs}ms", color = EmeraldLight, fontSize = 11.sp)
                        }
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun Step7VoiceChat(
    voiceEnabled: Boolean,
    rangeBlocks: Int,
    alwaysOnline: Boolean,
    onVoiceToggle: (Boolean) -> Unit,
    onRangeChange: (Int) -> Unit,
    onAlwaysOnlineToggle: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Proximity Voice & Always-Online", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        // 24/7 Hosting Toggle (Requirement A)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
            modifier = Modifier.fillMaxWidth().border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("24/7 Always Online", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            AlwaysOnlineTag(isAlwaysOnline = alwaysOnline)
                        }
                        Text(
                            "Keep this server running continuously, even when no players are online. Never stops when browser/app closes.",
                            color = TextDarkSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = alwaysOnline,
                        onCheckedChange = onAlwaysOnlineToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldLight, checkedTrackColor = EmeraldContainerDark)
                    )
                }
            }
        }

        // Proximity Voice Chat (Requirement C: 400 blocks)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
            modifier = Modifier.fillMaxWidth().border(1.dp, CyanLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Integrated Proximity Voice Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("3D spatial voice attenuation for Java and Bedrock players.", color = TextDarkSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = voiceEnabled,
                        onCheckedChange = onVoiceToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanLight, checkedTrackColor = CyanContainerDark)
                    )
                }

                if (voiceEnabled) {
                    HorizontalDivider(color = SlateDarkBorder)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Voice Range Radius", color = TextDarkPrimary, fontSize = 13.sp)
                        Text("$rangeBlocks Blocks (Default)", color = CyanLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Slider(
                        value = rangeBlocks.toFloat(),
                        onValueChange = { onRangeChange(it.toInt()) },
                        valueRange = 50f..800f,
                        steps = 14,
                        colors = SliderDefaults.colors(thumbColor = CyanLight, activeTrackColor = CyanSecondary)
                    )
                    Text("Players hear each other within $rangeBlocks blocks in 3D stereo sound.", color = TextDarkMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun Step8Review(
    name: String,
    edition: MinecraftEdition,
    version: String,
    software: ServerSoftware,
    ram: Int,
    cpu: Int,
    storage: Int,
    region: ServerRegion,
    voiceEnabled: Boolean,
    voiceRange: Int,
    alwaysOnline: Boolean
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Review Server Configuration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Check your settings before provisioning node container.", color = TextDarkSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReviewRow("Server Name", name)
                    ReviewRow("Edition", edition.displayName)
                    ReviewRow("Core Software", software.displayName)
                    ReviewRow("Minecraft Version", version)
                    ReviewRow("Memory", "$ram GB DDR5 RAM")
                    ReviewRow("vCPUs", "$cpu Cores")
                    ReviewRow("Disk Space", "$storage GB NVMe")
                    ReviewRow("Node Region", "${region.flag} ${region.displayName}")
                    ReviewRow("24/7 Hosting", if (alwaysOnline) "Always Online (Active)" else "On-demand")
                    ReviewRow("Proximity Voice", if (voiceEnabled) "Enabled ($voiceRange blocks)" else "Disabled")
                }
            }
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextDarkSecondary, fontSize = 13.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun Step9CreationProgress(step: Int) {
    val stepsList = listOf(
        "Allocating container on worker node...",
        "Formatting persistent NVMe storage volume...",
        "Downloading Minecraft server jar & dependencies...",
        "Binding proximity voice gateway & UDP ports...",
        "Verifying health check & starting server 24/7..."
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = EmeraldPrimary,
            modifier = Modifier.size(54.dp),
            strokeWidth = 4.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Deploying Your Minecraft Server...",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            stepsList.forEachIndexed { index, title ->
                val isDone = step > index
                val isCurrent = step == index

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isDone) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                    } else if (isCurrent) {
                        Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(strokeWidth = 2.dp, color = CyanLight, modifier = Modifier.size(12.dp))
                        }
                    } else {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SlateDarkBorder))
                    }

                    Text(
                        text = title,
                        color = if (isDone || isCurrent) Color.White else TextDarkMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
