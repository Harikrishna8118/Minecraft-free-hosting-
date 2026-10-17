package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.theme.*

@Composable
fun AddonsAndPacksTab(
    resourcePacks: List<ResourcePackItem>,
    addons: List<BedrockAddonItem>,
    onAddResourcePack: (ResourcePackItem) -> Unit,
    onToggleResourcePack: (String, Boolean) -> Unit,
    onDeleteResourcePack: (String) -> Unit,
    onAddBedrockAddon: (BedrockAddonItem) -> Unit,
    onToggleBedrockAddon: (String, Boolean) -> Unit,
    onDeleteBedrockAddon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(0) } // 0 = Resource Packs, 1 = Bedrock Add-ons
    var showUploadPackDialog by remember { mutableStateOf(false) }
    var showUploadAddonDialog by remember { mutableStateOf(false) }
    var requireServerPack by remember { mutableStateOf(true) }

    if (showUploadPackDialog) {
        UploadResourcePackDialog(
            onDismiss = { showUploadPackDialog = false },
            onUpload = { pack ->
                onAddResourcePack(pack)
                showUploadPackDialog = false
            }
        )
    }

    if (showUploadAddonDialog) {
        UploadBedrockAddonDialog(
            onDismiss = { showUploadAddonDialog = false },
            onUpload = { addon ->
                onAddBedrockAddon(addon)
                showUploadAddonDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Toggle (Resource Packs vs Bedrock Add-ons)
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SlateDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // Resource Packs tab
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedSection == 0) EmeraldPrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSection = 0 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = if (selectedSection == 0) Color.Black else TextDarkSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Resource Packs (${resourcePacks.size})",
                                color = if (selectedSection == 0) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Bedrock Add-ons tab
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedSection == 1) CyanLight else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSection = 1 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Layers,
                                contentDescription = null,
                                tint = if (selectedSection == 1) Color.Black else TextDarkSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Bedrock Add-ons (${addons.size})",
                                color = if (selectedSection == 1) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        if (selectedSection == 0) {
            // Resource Pack Header & Server-Forced Toggle
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ENFORCE RESOURCE PACK IN SERVER.PROPERTIES", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Require players to download pack on join", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Switch(
                                checked = requireServerPack,
                                onCheckedChange = { requireServerPack = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldLight, checkedTrackColor = EmeraldContainerDark)
                            )
                        }

                        Text(
                            "Automatically delivers the server texture pack to players connecting via Java or Bedrock with SHA-1 hash validation.",
                            color = TextDarkSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Button(
                            onClick = { showUploadPackDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Resource Pack (.zip)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(resourcePacks) { pack ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(pack.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (pack.isServerForced) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.2f)
                                    ) {
                                        Text("FORCED", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                            }
                            Text("${pack.fileName} • %.1f MB • Format %d".format(pack.sizeMb, pack.packFormat), color = CyanLight, fontSize = 11.sp)
                            Text(pack.description, color = TextDarkSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = pack.isEnabled,
                                onCheckedChange = { onToggleResourcePack(pack.id, it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldLight, checkedTrackColor = EmeraldContainerDark)
                            )
                            if (pack.id.startsWith("rp-custom-")) {
                                IconButton(onClick = { onDeleteResourcePack(pack.id) }, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedstoneRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Bedrock Add-ons Section
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("BEDROCK ADD-ONS & BEHAVIOR PACKS", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("GeyserMC & Bedrock Dedicated Engine", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CyanLight.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanLight)
                            ) {
                                Text("Crossplay Ready", color = CyanLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }

                        Text(
                            "Install Behavior Packs (BP), Resource Packs (RP), or complete .mcaddon packages for custom entities, animations, and gameplay mechanics.",
                            color = TextDarkSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Button(
                            onClick = { showUploadAddonDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanLight, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Install Add-on (.mcaddon / .mcpack)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(addons) { addon ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(addon.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SlateDarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanLight.copy(alpha = 0.5f))
                                ) {
                                    Text(addon.type.label.take(18), color = CyanLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("v${addon.version} • Min Engine ${addon.minEngineVersion} • %.1f MB".format(addon.sizeMb), color = AmberAccent, fontSize = 11.sp)
                            Text(addon.description, color = TextDarkSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = addon.isEnabled,
                                onCheckedChange = { onToggleBedrockAddon(addon.id, it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = CyanLight, checkedTrackColor = SlateDarkBorder)
                            )
                            if (addon.id.startsWith("addon-custom-")) {
                                IconButton(onClick = { onDeleteBedrockAddon(addon.id) }, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedstoneRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadResourcePackDialog(
    onDismiss: () -> Unit,
    onUpload: (ResourcePackItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isServerForced by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Resource Pack (.zip)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (fileName.isEmpty() && it.isNotEmpty()) {
                            fileName = "${it.lowercase().replace(" ", "-")}.zip"
                        }
                    },
                    label = { Text("Pack Name") },
                    placeholder = { Text("e.g. Bare Bones, Faithful", color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name (.zip / .mcpack)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isServerForced = !isServerForced }
                ) {
                    Checkbox(
                        checked = isServerForced,
                        onCheckedChange = { isServerForced = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Require players to download upon join", color = TextDarkSecondary, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val pack = ResourcePackItem(
                            id = "rp-custom-${System.currentTimeMillis()}",
                            name = name,
                            fileName = fileName.ifEmpty { "$name.zip" },
                            sizeMb = 18.5f,
                            description = description.ifEmpty { "Custom uploaded server resource pack" },
                            isServerForced = isServerForced,
                            isEnabled = true
                        )
                        onUpload(pack)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Install Pack", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}

@Composable
fun UploadBedrockAddonDialog(
    onDismiss: () -> Unit,
    onUpload: (BedrockAddonItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.0.0") }
    var selectedType by remember { mutableStateOf(AddonType.FULL_ADDON) }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Install Bedrock Add-on", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Add-on Name") },
                    placeholder = { Text("e.g. Actions & Stuff, Gun Mod", color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = version,
                    onValueChange = { version = it },
                    label = { Text("Version") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Add-on Package Type:", color = TextDarkSecondary, fontSize = 11.sp)
                AddonType.values().forEach { type ->
                    val isSel = selectedType == type
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) SlateDarkSurface else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanLight else SlateDarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSel,
                                onClick = { selectedType = type },
                                colors = RadioButtonDefaults.colors(selectedColor = CyanLight)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(type.label, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val addon = BedrockAddonItem(
                            id = "addon-custom-${System.currentTimeMillis()}",
                            name = name,
                            type = selectedType,
                            version = version,
                            description = description.ifEmpty { "Custom Bedrock add-on package" },
                            fileName = "${name.lowercase().replace(" ", "_")}.mcaddon",
                            sizeMb = 24.2f,
                            isEnabled = true
                        )
                        onUpload(addon)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanLight, contentColor = Color.Black)
            ) {
                Text("Install Add-on", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}
