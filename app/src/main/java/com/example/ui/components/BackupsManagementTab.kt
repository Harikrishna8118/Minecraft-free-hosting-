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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackupSnapshot
import com.example.ui.theme.*

@Composable
fun BackupsManagementTab(
    backups: List<BackupSnapshot>,
    onCreateBackup: (String) -> Unit,
    onRestoreBackup: (String) -> Unit,
    onDeleteBackup: (String) -> Unit,
    onImportExternalBackup: (label: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportExternalDialog by remember { mutableStateOf(false) }
    var restoringBackup by remember { mutableStateOf<BackupSnapshot?>(null) }
    val clipboardManager = LocalClipboardManager.current
    var copiedUrlMsg by remember { mutableStateOf<String?>(null) }

    if (showCreateDialog) {
        CreateSnapshotDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                onCreateBackup(name)
                showCreateDialog = false
            }
        )
    }

    if (showImportExternalDialog) {
        ImportExternalBackupDialog(
            onDismiss = { showImportExternalDialog = false },
            onConfirm = { label, url ->
                onImportExternalBackup(label, url)
                showImportExternalDialog = false
            }
        )
    }

    restoringBackup?.let { bk ->
        AlertDialog(
            onDismissRequest = { restoringBackup = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = AmberAccent)
                    Text("Restore Snapshot?", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Restoring '${bk.name}' will overwrite existing server world data and configuration with the state from ${bk.createdAt}.",
                        color = TextDarkSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Text(
                        "The server container will automatically reload chunks upon restore.",
                        color = CyanLight,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRestoreBackup(bk.id)
                        restoringBackup = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = Color.Black)
                ) {
                    Text("Confirm Restore", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { restoringBackup = null }) { Text("Cancel", color = TextDarkSecondary) }
            },
            containerColor = SlateDarkCard
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Multi-AZ S3 Header Info
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                Icon(Icons.Default.Backup, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("SNAPSHOT ARCHIVES", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Amazon S3 Multi-AZ Redundancy", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight)
                        ) {
                            Text(
                                "Auto-Daily: Active",
                                color = EmeraldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        "Every snapshot preserves your world chunks, player inventories, server.properties, plugins, and custom datapacks.",
                        color = TextDarkSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showCreateDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Take Snapshot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showImportExternalDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = CyanLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import External URL", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Copied toast banner
        copiedUrlMsg?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldContainerDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(msg, color = EmeraldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { copiedUrlMsg = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Snapshots list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("STORED SNAPSHOTS (${backups.size})", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Max retention: 30 days", color = TextDarkMuted, fontSize = 10.sp)
            }
        }

        if (backups.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CloudQueue, contentDescription = null, tint = TextDarkMuted, modifier = Modifier.size(36.dp))
                        Text("No snapshots saved yet", color = TextDarkSecondary, fontSize = 13.sp)
                        Text("Create a snapshot or import one from your previous host.", color = TextDarkMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        items(backups) { bk ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(bk.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (bk.isAutoDaily) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CyanLight.copy(alpha = 0.2f)
                                    ) {
                                        Text("AUTO", color = CyanLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                            }
                            Text(
                                "%.1f MB • %s • %s".format(bk.sizeMb, bk.createdAt, bk.storageTier),
                                color = TextDarkSecondary,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { onDeleteBackup(bk.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextDarkMuted, modifier = Modifier.size(18.dp))
                        }
                    }

                    HorizontalDivider(color = SlateDarkBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { restoringBackup = bk },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface, contentColor = AmberAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.7f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(bk.downloadUrl))
                                copiedUrlMsg = "✓ Download link for '${bk.name}' copied!"
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download ZIP", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateSnapshotDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit
) {
    var backupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Take Cloud Snapshot", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Takes a full atomic snapshot of world chunks, player data, and configurations while server runs.",
                    color = TextDarkSecondary,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = backupName,
                    onValueChange = { backupName = it },
                    label = { Text("Snapshot Label (Optional)") },
                    placeholder = { Text("e.g. Pre-EnderDragon Fight", color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(backupName) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Create Snapshot", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}

@Composable
fun ImportExternalBackupDialog(
    onDismiss: () -> Unit,
    onConfirm: (label: String, url: String) -> Unit
) {
    var label by remember { mutableStateOf("Aternos / External Backup") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Backup from URL", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Enter a direct download link from Aternos, Google Drive, Dropbox, or custom CDN:",
                    color = TextDarkSecondary,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Backup Label") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Backup ZIP URL") },
                    placeholder = { Text("https://example.com/backup.zip", color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(label, url) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanLight, contentColor = Color.Black)
            ) {
                Text("Import Backup", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}
