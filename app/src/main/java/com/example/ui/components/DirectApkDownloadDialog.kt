package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DirectApkDownloadDialog(
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldLight.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text("Direct Download APK", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("CraftHost Android Application Package", color = TextDarkSecondary, fontSize = 11.sp)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // APK Status Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("BUILD STATUS", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.2f)
                            ) {
                                Text("SUCCESS", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Text("app-debug.apk", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Location: app/build/outputs/apk/debug/app-debug.apk",
                            color = CyanLight,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )

                        HorizontalDivider(color = SlateDarkBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Artifact Type", color = TextDarkMuted, fontSize = 10.sp)
                                Text("Signed Debug APK", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Target Platform", color = TextDarkMuted, fontSize = 10.sp)
                                Text("Android 8.0+ (API 26-35)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Instructions Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("HOW TO DOWNLOAD / EXPORT:", color = CyanLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("1. Click the 'Settings / More' menu in top-right of AI Studio.", color = TextDarkSecondary, fontSize = 11.sp)
                        Text("2. Select 'Export' or 'Download APK' to save directly to your phone or PC.", color = TextDarkSecondary, fontSize = 11.sp)
                        Text("3. You can also export as a ZIP or push to your GitHub repo.", color = TextDarkSecondary, fontSize = 11.sp)
                    }
                }

                AnimatedVisibility(visible = copiedMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldContainerDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight)
                    ) {
                        Text(
                            copiedMessage ?: "",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString("app/build/outputs/apk/debug/app-debug.apk"))
                    copiedMessage = "✓ APK path copied to clipboard!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy APK Path", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = TextDarkSecondary) }
        },
        containerColor = SlateDarkCard
    )
}
