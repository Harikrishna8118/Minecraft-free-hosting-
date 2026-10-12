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
import com.example.ui.theme.*

@Composable
fun AdminAndBillingScreen(
    isDemoMode: Boolean,
    onToggleDemoMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var maintenanceMode by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf("Survival Pro") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Admin & Plan Management",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Control Plane Administration • Subscriptions • Account",
                    color = EmeraldLight,
                    fontSize = 11.sp
                )
            }
        }

        // Demo Mode / Live Mode Switch Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Demonstration Sandbox Mode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "Requirement 40: Clearly labels simulated data and permits rapid testing without live cloud billing.",
                                color = TextDarkSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = isDemoMode,
                            onCheckedChange = { onToggleDemoMode() },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberAccent, checkedTrackColor = Color(0xFF451A03))
                        )
                    }
                }
            }
        }

        // Admin Controls (Requirement 35)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("PLATFORM ADMINISTRATOR CONTROLS", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Maintenance Drain Mode", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Prevent new container allocations during upgrades", color = TextDarkSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = maintenanceMode,
                            onCheckedChange = { maintenanceMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = RedstoneRed, checkedTrackColor = Color(0xFF3F1A1A))
                        )
                    }

                    HorizontalDivider(color = SlateDarkBorder)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Active User Accounts", color = TextDarkSecondary, fontSize = 12.sp)
                        Text("1,420 users", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Allocated Memory Pool", color = TextDarkSecondary, fontSize = 12.sp)
                        Text("174 / 320 GB RAM", color = CyanLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Voice Gateway Bandwidth", color = TextDarkSecondary, fontSize = 12.sp)
                        Text("48.2 Mbps (Opus)", color = EmeraldLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Account Profile Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("USER ACCOUNT PROFILE", color = TextDarkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainerDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("LS", color = EmeraldLight, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Column {
                            Text("learningsource77@gmail.com", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("CraftHost Pro Subscriber • Active 24/7", color = EmeraldLight, fontSize = 11.sp)
                        }
                    }

                    HorizontalDivider(color = SlateDarkBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("REST API Key", color = TextDarkSecondary, fontSize = 11.sp)
                            Text("ch_live_9f82...38a1", color = CyanLight, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        Button(
                            onClick = { /* regenerate */ },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface, contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Roll Key", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
