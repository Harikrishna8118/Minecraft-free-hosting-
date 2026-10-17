package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WorldManagementTab(
    server: MinecraftServer,
    worldMetadata: ServerWorldMetadata?,
    onImportAternosWorld: (worldName: String, archiveName: String, isBedrock: Boolean) -> Unit,
    onImportWorldZip: (fileName: String, source: WorldImportSource) -> Unit,
    onResetWorld: (newSeed: String, worldName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val meta = worldMetadata ?: ServerWorldMetadata()
    val clipboardManager = LocalClipboardManager.current
    var showAternosDialog by remember { mutableStateOf(false) }
    var showUploadZipDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showDownloadToast by remember { mutableStateOf(false) }

    if (showAternosDialog) {
        AternosImportDialog(
            currentServerName = server.name,
            onDismiss = { showAternosDialog = false },
            onImport = { worldName, archiveName, isBedrock ->
                onImportAternosWorld(worldName, archiveName, isBedrock)
                showAternosDialog = false
            }
        )
    }

    if (showUploadZipDialog) {
        CustomWorldUploadDialog(
            onDismiss = { showUploadZipDialog = false },
            onUpload = { fileName, source ->
                onImportWorldZip(fileName, source)
                showUploadZipDialog = false
            }
        )
    }

    if (showResetDialog) {
        ResetWorldDialog(
            currentSeed = meta.seed,
            onDismiss = { showResetDialog = false },
            onConfirmReset = { seed, name ->
                onResetWorld(seed, name)
                showResetDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Aternos Special Callout Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(CyanLight.copy(alpha = 0.6f), EmeraldLight.copy(alpha = 0.6f))),
                        RoundedCornerShape(12.dp)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CyanLight.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = CyanLight, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("MIGRATE OLD WORLD", color = CyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                Text("Import from Aternos, Minehut or ZIP", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = EmeraldPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight)
                        ) {
                            Text(
                                "Auto-Convert",
                                color = EmeraldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        "Easily transfer your existing world from Aternos without losing builds, inventory, player coordinates, or Nether/End dimensions.",
                        color = TextDarkSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showAternosDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Aternos World", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showUploadZipDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FolderZip, contentDescription = null, tint = CyanLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Other Server / ZIP", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Active World Details Card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
                            Text("ACTIVE WORLD STATUS", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (meta.importedFromSource != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CyanLight.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanLight)
                            ) {
                                Text(
                                    "From ${meta.importedFromSource.badge}",
                                    color = CyanLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("World Name", color = TextDarkSecondary, fontSize = 11.sp)
                            Text(meta.worldName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("World Seed", color = TextDarkSecondary, fontSize = 11.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(meta.seed, color = AmberAccent, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy seed",
                                    tint = TextDarkMuted,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(meta.seed))
                                        }
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = SlateDarkBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WorldMetricPill("Total Chunks", "%,d".format(meta.chunkCount), Icons.Default.GridOn)
                        WorldMetricPill("Disk Size", "%.1f MB".format(meta.sizeMb), Icons.Default.Storage)
                        WorldMetricPill("Generator", meta.generatorType.take(14), Icons.Default.Terrain)
                    }

                    // Dimension Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DimensionBadge("Overworld", true, EmeraldLight)
                        DimensionBadge("Nether (DIM-1)", meta.hasNether, RedstoneRed)
                        DimensionBadge("The End (DIM1)", meta.hasTheEnd, PurpleAccent)
                    }

                    HorizontalDivider(color = SlateDarkBorder)

                    // Secondary actions: Download archive, Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString("https://cdn.crafthost.gg/worlds/${server.id}-backup.zip"))
                                showDownloadToast = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download ZIP", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedstoneRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RedstoneRed.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = RedstoneRed, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset World", fontSize = 11.sp)
                        }
                    }

                    AnimatedVisibility(visible = showDownloadToast) {
                        Text(
                            "✓ Direct CDN download URL copied to clipboard!",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorldMetricPill(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SlateDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = CyanLight, modifier = Modifier.size(14.dp))
            Column {
                Text(label, color = TextDarkMuted, fontSize = 9.sp)
                Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun DimensionBadge(name: String, active: Boolean, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (active) color.copy(alpha = 0.15f) else SlateDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) color else SlateDarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (active) color else TextDarkMuted)
            )
            Text(name, color = if (active) color else TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AternosImportDialog(
    currentServerName: String,
    onDismiss: () -> Unit,
    onImport: (worldName: String, archiveName: String, isBedrock: Boolean) -> Unit
) {
    var worldName by remember { mutableStateOf("Aternos_Survival_World") }
    var selectedArchive by remember { mutableStateOf("world.zip (from Aternos)") }
    var isBedrockSource by remember { mutableStateOf(false) }
    var aternosShareUrl by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }
    var progressStep by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isImporting) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CyanLight.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = CyanLight, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text("Import from Aternos", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Zero-downtime world migration", color = TextDarkSecondary, fontSize = 11.sp)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isImporting) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progressStep / 4f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = EmeraldPrimary,
                            trackColor = SlateDarkBorder
                        )

                        val statusText = when (progressStep) {
                            1 -> "Unpacking Aternos world.zip archive..."
                            2 -> "Checking level.dat integrity & player inventories..."
                            3 -> if (isBedrockSource) "Converting Bedrock LevelDB chunks to Java Anvil..." else "Verifying 21,420 chunks in region/..."
                            4 -> "Binding world to server container... Ready!"
                            else -> "Preparing Aternos migration..."
                        }
                        Text(statusText, color = EmeraldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Step-by-step instructions
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SlateDarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("HOW TO EXPORT FROM ATERNOS:", color = CyanLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("1. In Aternos -> click 'Worlds' in the sidebar.", color = TextDarkSecondary, fontSize = 11.sp)
                            Text("2. Click the 'Download' button (exports world.zip).", color = TextDarkSecondary, fontSize = 11.sp)
                            Text("3. Upload the downloaded world.zip here below.", color = TextDarkSecondary, fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = worldName,
                        onValueChange = { worldName = it },
                        label = { Text("World Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanLight,
                            unfocusedBorderColor = SlateDarkBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = aternosShareUrl,
                        onValueChange = { aternosShareUrl = it },
                        label = { Text("Aternos Direct Link (Optional)") },
                        placeholder = { Text("https://aternos.org/worlds/...", color = TextDarkMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanLight,
                            unfocusedBorderColor = SlateDarkBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Archive Selector
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SlateDarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Selected Archive:", color = TextDarkMuted, fontSize = 10.sp)
                                    Text(selectedArchive, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Text("612 MB", color = CyanLight, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isBedrockSource = !isBedrockSource }
                    ) {
                        Checkbox(
                            checked = isBedrockSource,
                            onCheckedChange = { isBedrockSource = it },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Aternos server was Bedrock Edition (Convert LevelDB -> Anvil)", color = TextDarkSecondary, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            if (!isImporting) {
                Button(
                    onClick = {
                        isImporting = true
                        coroutineScope.launch {
                            progressStep = 1
                            delay(700)
                            progressStep = 2
                            delay(700)
                            progressStep = 3
                            delay(800)
                            progressStep = 4
                            delay(600)
                            onImport(worldName, selectedArchive, isBedrockSource)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                ) {
                    Text("Start Migration & Mount", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isImporting) {
                TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
            }
        },
        containerColor = SlateDarkCard
    )
}

@Composable
fun CustomWorldUploadDialog(
    onDismiss: () -> Unit,
    onUpload: (fileName: String, source: WorldImportSource) -> Unit
) {
    var selectedSource by remember { mutableStateOf(WorldImportSource.CUSTOM_ZIP) }
    var fileName by remember { mutableStateOf("custom-world-save.zip") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload World Package", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select the origin of your world folder or archive:", color = TextDarkSecondary, fontSize = 12.sp)

                WorldImportSource.values().forEach { source ->
                    val isSel = selectedSource == source
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) SlateDarkSurface else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanLight else SlateDarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSource = source }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = isSel,
                                onClick = { selectedSource = source },
                                colors = RadioButtonDefaults.colors(selectedColor = CyanLight)
                            )
                            Column {
                                Text(source.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(source.description, color = TextDarkSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Archive File Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanLight,
                        unfocusedBorderColor = SlateDarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpload(fileName, selectedSource) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanLight, contentColor = Color.Black)
            ) {
                Text("Upload & Mount", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}

@Composable
fun ResetWorldDialog(
    currentSeed: String,
    onDismiss: () -> Unit,
    onConfirmReset: (newSeed: String, worldName: String) -> Unit
) {
    var newSeed by remember { mutableStateOf("") }
    var worldName by remember { mutableStateOf("world_fresh") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = RedstoneRed)
                Text("Reset Server World", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Warning: Generating a new world will unmount the current world directory. Your current world will be automatically backed up first.",
                    color = RedstoneRed,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = worldName,
                    onValueChange = { worldName = it },
                    label = { Text("New World Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newSeed,
                    onValueChange = { newSeed = it },
                    label = { Text("Seed (Leave blank for random)") },
                    placeholder = { Text("e.g. -4920491823901", color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmReset(newSeed, worldName) },
                colors = ButtonDefaults.buttonColors(containerColor = RedstoneRed, contentColor = Color.White)
            ) {
                Text("Generate Fresh World", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}
