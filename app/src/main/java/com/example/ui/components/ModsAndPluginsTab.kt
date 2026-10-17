package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.model.MinecraftEdition
import com.example.model.PluginModItem
import com.example.ui.theme.*

@Composable
fun ModsAndPluginsTab(
    plugins: List<PluginModItem>,
    onToggle: (String, Boolean) -> Unit,
    onInstall: (String) -> Unit,
    onDelete: (String) -> Unit,
    onUploadCustomMod: (name: String, version: String, category: String, modLoader: String, fileName: String, author: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var showUploadDialog by remember { mutableStateOf(false) }

    if (showUploadDialog) {
        UploadCustomModDialog(
            onDismiss = { showUploadDialog = false },
            onUpload = { name, ver, cat, loader, file, author ->
                onUploadCustomMod(name, ver, cat, loader, file, author)
                showUploadDialog = false
            }
        )
    }

    val categories = listOf("ALL", "VOICE", "PERFORMANCE", "ADMIN", "CROSSPLAY", "WORLD")

    val filteredList = remember(plugins, selectedCategory, searchQuery) {
        plugins.filter { item ->
            val matchesCategory = selectedCategory == "ALL" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.modLoader.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("MODS & PLUGINS MANAGER", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Extend server with .jar mods & plugins", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showUploadDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload .jar Mod", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search mods, plugins, loaders (e.g. Voice, Sodium, Fabric)...", color = TextDarkMuted, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextDarkMuted, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextDarkMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = EmeraldLight,
                    unfocusedBorderColor = SlateDarkBorder
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) EmeraldPrimary else SlateDarkCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) EmeraldLight else SlateDarkBorder),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSel) Color.Black else TextDarkSecondary,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // List of items
        items(filteredList) { plug ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateDarkSurface)
                                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                val icon = when (plug.category) {
                                    "VOICE" -> Icons.Default.RecordVoiceOver
                                    "PERFORMANCE" -> Icons.Default.Speed
                                    "ADMIN" -> Icons.Default.Security
                                    "CROSSPLAY" -> Icons.Default.SyncAlt
                                    "WORLD" -> Icons.Default.Public
                                    else -> Icons.Default.Extension
                                }
                                Icon(icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(plug.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CyanLight.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            "v${plug.version}",
                                            color = CyanLight,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text("by ${plug.author} • ${plug.downloadsCount} downloads • ${plug.modLoader}", color = TextDarkSecondary, fontSize = 10.sp)
                            }
                        }

                        if (plug.isInstalled) {
                            Switch(
                                checked = plug.isEnabled,
                                onCheckedChange = { onToggle(plug.id, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldLight,
                                    checkedTrackColor = EmeraldContainerDark
                                )
                            )
                        } else {
                            Button(
                                onClick = { onInstall(plug.id) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanLight, contentColor = Color.Black),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Install", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text(plug.description, color = TextDarkMuted, fontSize = 11.sp, lineHeight = 15.sp)

                    if (plug.id.startsWith("mod-")) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { onDelete(plug.id) },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = RedstoneRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove Mod", color = RedstoneRed, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadCustomModDialog(
    onDismiss: () -> Unit,
    onUpload: (name: String, version: String, category: String, modLoader: String, fileName: String, author: String) -> Unit
) {
    var modName by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.0.0") }
    var author by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var selectedLoader by remember { mutableStateOf("Paper / Spigot") }
    var selectedCategory by remember { mutableStateOf("ADMIN") }

    val loaders = listOf("Paper / Spigot", "Fabric", "Forge", "NeoForge")
    val categories = listOf("ADMIN", "PERFORMANCE", "VOICE", "CROSSPLAY", "WORLD", "MAGIC")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.UploadFile, contentDescription = null, tint = EmeraldLight)
                Text("Upload Custom Mod (.jar)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = modName,
                    onValueChange = {
                        modName = it
                        if (fileName.isEmpty() && it.isNotEmpty()) {
                            fileName = "${it.lowercase().replace(" ", "-")}-$version.jar"
                        }
                    },
                    label = { Text("Mod / Plugin Name") },
                    placeholder = { Text("e.g. Lithium, Chunky, Towny", color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        label = { Text("Version") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name (.jar)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Target Mod Loader / Engine:", color = TextDarkSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    loaders.forEach { loader ->
                        val isSel = selectedLoader == loader
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) EmeraldPrimary else SlateDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) EmeraldLight else SlateDarkBorder),
                            modifier = Modifier.clickable { selectedLoader = loader }
                        ) {
                            Text(
                                loader,
                                color = if (isSel) Color.Black else Color.White,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (modName.isNotBlank()) {
                        onUpload(modName, version, selectedCategory, selectedLoader, fileName.ifEmpty { "$modName.jar" }, author)
                    }
                },
                enabled = modName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Install .jar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}
