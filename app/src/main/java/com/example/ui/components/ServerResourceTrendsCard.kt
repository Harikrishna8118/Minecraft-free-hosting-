package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MinecraftServer
import com.example.model.ServerStatus
import com.example.ui.theme.*
import kotlin.math.roundToInt

enum class TrendTimeWindow(val label: String, val pointsCount: Int, val intervalLabel: String) {
    LIVE_60S("Live 60s", 12, "5s intervals"),
    ONE_HOUR("1 Hour", 16, "4m intervals"),
    TWENTY_FOUR_HOURS("24 Hours", 24, "1h intervals")
}

enum class MetricViewMode(val label: String) {
    COMBINED("Combined"),
    CPU_ONLY("CPU (%)"),
    RAM_ONLY("RAM (%)")
}

data class MetricDataPoint(
    val timeLabel: String,
    val cpuPercent: Float,
    val ramMb: Float,
    val ramPercent: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerResourceTrendsCard(
    servers: List<MinecraftServer>,
    selectedServerId: String,
    onSelectServer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedServer = remember(servers, selectedServerId) {
        servers.find { it.id == selectedServerId } ?: servers.firstOrNull()
    }

    var selectedWindow by remember { mutableStateOf(TrendTimeWindow.LIVE_60S) }
    var metricMode by remember { mutableStateOf(MetricViewMode.COMBINED) }
    var scrubIndex by remember { mutableStateOf<Int?>(null) }
    var serverMenuExpanded by remember { mutableStateOf(false) }

    // Pulsing live indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Generate realistic historical data points based on the server's current state and selected time window
    val dataPoints = remember(selectedServer, selectedWindow) {
        if (selectedServer == null) emptyList()
        else {
            val totalRamMb = selectedServer.resources.ramGb * 1024f
            val baseCpu = if (selectedServer.status == ServerStatus.ONLINE) selectedServer.cpuUsagePercent else 0f
            val baseRam = if (selectedServer.status == ServerStatus.ONLINE) selectedServer.ramUsageMb else 0f

            val count = selectedWindow.pointsCount
            List(count) { i ->
                val progress = i.toFloat() / (count - 1).coerceAtLeast(1)
                val timeLabel = when (selectedWindow) {
                    TrendTimeWindow.LIVE_60S -> "-${(60 - (progress * 60)).roundToInt()}s"
                    TrendTimeWindow.ONE_HOUR -> "-${(60 - (progress * 60)).roundToInt()}m"
                    TrendTimeWindow.TWENTY_FOUR_HOURS -> "-${(24 - (progress * 24)).roundToInt()}h"
                }

                // Smooth fluctuation with determinism
                val cpuNoise = kotlin.math.sin(i * 1.3) * 6.0 + kotlin.math.cos(i * 0.7) * 4.0
                val ramNoise = kotlin.math.cos(i * 0.9) * 120.0 + kotlin.math.sin(i * 1.5) * 80.0

                val cpu = if (baseCpu > 0) (baseCpu + cpuNoise).coerceIn(5.0, 95.0).toFloat() else 0f
                val ram = if (baseRam > 0) (baseRam + ramNoise).coerceIn(512.0, totalRamMb.toDouble()).toFloat() else 0f
                val ramPct = if (totalRamMb > 0) (ram / totalRamMb * 100f).coerceIn(0f, 100f) else 0f

                MetricDataPoint(
                    timeLabel = if (i == count - 1) "Now" else timeLabel,
                    cpuPercent = cpu,
                    ramMb = ram,
                    ramPercent = ramPct
                )
            }
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SlateDarkBorder, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Title + Server Dropdown Selector + Live Pulse
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedServer?.status == ServerStatus.ONLINE) EmeraldLight.copy(alpha = pulseAlpha)
                                    else Color.Gray
                                )
                        )
                        Text(
                            text = "RESOURCE USAGE TRENDS",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Text(
                        text = "Canvas visualization for CPU and RAM telemetry",
                        color = TextDarkMuted,
                        fontSize = 10.sp
                    )
                }

                // Server Selector Dropdown Button
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SlateDarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
                        modifier = Modifier.clickable { serverMenuExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = selectedServer?.name ?: "Select Server",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Choose server",
                                tint = TextDarkSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = serverMenuExpanded,
                        onDismissRequest = { serverMenuExpanded = false },
                        modifier = Modifier
                            .background(SlateDarkSurface)
                            .border(1.dp, SlateDarkBorder, RoundedCornerShape(8.dp))
                    ) {
                        servers.forEach { srv ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (srv.status == ServerStatus.ONLINE) EmeraldLight else Color.Gray)
                                        )
                                        Text(srv.name, color = Color.White, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    onSelectServer(srv.id)
                                    serverMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Controls Row: Metric Series Mode & Time Window Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Metric Mode Chips (Combined, CPU, RAM)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MetricViewMode.values().forEach { mode ->
                        val isSelected = metricMode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) SlateDarkCardHover else SlateDarkSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldPrimary else SlateDarkBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { metricMode = mode }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = mode.label,
                                color = if (isSelected) Color.White else TextDarkSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Time Window Chips (Live 60s, 1h, 24h)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TrendTimeWindow.values().forEach { win ->
                        val isSelected = selectedWindow == win
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CyanContainerDark else SlateDarkSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanLight else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedWindow = win }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = win.label,
                                color = if (isSelected) CyanLight else TextDarkMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Real-Time Stats Overview Banner
            if (selectedServer != null) {
                val latestPoint = dataPoints.lastOrNull()
                val currentCpu = latestPoint?.cpuPercent ?: selectedServer.cpuUsagePercent
                val currentRamMb = latestPoint?.ramMb ?: selectedServer.ramUsageMb
                val currentRamPct = latestPoint?.ramPercent ?: (selectedServer.ramUsageMb / (selectedServer.resources.ramGb * 1024f) * 100f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateDarkSurface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // CPU Telemetry
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldPrimary))
                        Column {
                            Text("CPU Usage", color = TextDarkMuted, fontSize = 10.sp)
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.1f", currentCpu)}%",
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    VerticalDivider(modifier = Modifier.height(24.dp), color = SlateDarkBorder)

                    // RAM Telemetry
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyanSecondary))
                        Column {
                            Text("RAM Allocated", color = TextDarkMuted, fontSize = 10.sp)
                            Text(
                                text = "${(currentRamMb / 1024f).toInt()} / ${selectedServer.resources.ramGb} GB (${currentRamPct.toInt()}%)",
                                color = CyanLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    VerticalDivider(modifier = Modifier.height(24.dp), color = SlateDarkBorder)

                    // Status / Health
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Node Status", color = TextDarkMuted, fontSize = 10.sp)
                        Text(
                            text = if (selectedServer.status == ServerStatus.ONLINE) "20.0 TPS (Stable)" else "Offline",
                            color = if (selectedServer.status == ServerStatus.ONLINE) EmeraldLight else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Interactive Canvas Trend Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF070B0E))
                    .border(1.dp, Color(0xFF131D24), RoundedCornerShape(10.dp))
                    .pointerInput(dataPoints.size) {
                        detectTapGestures(
                            onPress = { offset ->
                                val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                val index = (offset.x / stepX).roundToInt().coerceIn(0, dataPoints.size - 1)
                                scrubIndex = index
                            }
                        )
                    }
                    .pointerInput(dataPoints.size) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                scrubIndex = (offset.x / stepX).roundToInt().coerceIn(0, dataPoints.size - 1)
                            },
                            onDragEnd = { scrubIndex = null },
                            onDragCancel = { scrubIndex = null },
                            onDrag = { change, _ ->
                                change.consume()
                                val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                scrubIndex = (change.position.x / stepX).roundToInt().coerceIn(0, dataPoints.size - 1)
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val paddingBottom = 22.dp.toPx()
                    val chartHeight = h - paddingBottom
                    val chartWidth = w

                    // 1. Draw horizontal grid lines (0%, 25%, 50%, 75%, 100%)
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = chartHeight * (i.toFloat() / gridLines)
                        drawLine(
                            color = Color(0xFF131E27),
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (dataPoints.size >= 2) {
                        val stepX = chartWidth / (dataPoints.size - 1)

                        // Draw CPU Trend Line & Area
                        if (metricMode == MetricViewMode.COMBINED || metricMode == MetricViewMode.CPU_ONLY) {
                            val cpuPath = Path()
                            val cpuFillPath = Path()

                            dataPoints.forEachIndexed { idx, point ->
                                val x = idx * stepX
                                val normalizedY = (point.cpuPercent / 100f).coerceIn(0f, 1f)
                                val y = chartHeight - (normalizedY * chartHeight)

                                if (idx == 0) {
                                    cpuPath.moveTo(x, y)
                                    cpuFillPath.moveTo(x, chartHeight)
                                    cpuFillPath.lineTo(x, y)
                                } else {
                                    val prevX = (idx - 1) * stepX
                                    val prevNormalizedY = (dataPoints[idx - 1].cpuPercent / 100f).coerceIn(0f, 1f)
                                    val prevY = chartHeight - (prevNormalizedY * chartHeight)
                                    val cx = (prevX + x) / 2f
                                    cpuPath.cubicTo(cx, prevY, cx, y, x, y)
                                    cpuFillPath.cubicTo(cx, prevY, cx, y, x, y)
                                }
                            }

                            cpuFillPath.lineTo(chartWidth, chartHeight)
                            cpuFillPath.close()

                            // Fill gradient
                            drawPath(
                                path = cpuFillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        EmeraldPrimary.copy(alpha = 0.25f),
                                        EmeraldPrimary.copy(alpha = 0.02f)
                                    ),
                                    startY = 0f,
                                    endY = chartHeight
                                )
                            )

                            // Stroke line
                            drawPath(
                                path = cpuPath,
                                color = EmeraldLight,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Draw RAM Trend Line & Area
                        if (metricMode == MetricViewMode.COMBINED || metricMode == MetricViewMode.RAM_ONLY) {
                            val ramPath = Path()
                            val ramFillPath = Path()

                            dataPoints.forEachIndexed { idx, point ->
                                val x = idx * stepX
                                val normalizedY = (point.ramPercent / 100f).coerceIn(0f, 1f)
                                val y = chartHeight - (normalizedY * chartHeight)

                                if (idx == 0) {
                                    ramPath.moveTo(x, y)
                                    ramFillPath.moveTo(x, chartHeight)
                                    ramFillPath.lineTo(x, y)
                                } else {
                                    val prevX = (idx - 1) * stepX
                                    val prevNormalizedY = (dataPoints[idx - 1].ramPercent / 100f).coerceIn(0f, 1f)
                                    val prevY = chartHeight - (prevNormalizedY * chartHeight)
                                    val cx = (prevX + x) / 2f
                                    ramPath.cubicTo(cx, prevY, cx, y, x, y)
                                    ramFillPath.cubicTo(cx, prevY, cx, y, x, y)
                                }
                            }

                            ramFillPath.lineTo(chartWidth, chartHeight)
                            ramFillPath.close()

                            // Fill gradient
                            drawPath(
                                path = ramFillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        CyanSecondary.copy(alpha = 0.22f),
                                        CyanSecondary.copy(alpha = 0.02f)
                                    ),
                                    startY = 0f,
                                    endY = chartHeight
                                )
                            )

                            // Stroke line
                            drawPath(
                                path = ramPath,
                                color = CyanLight,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Draw Scrub / Inspection Indicator line
                        scrubIndex?.let { idx ->
                            if (idx in dataPoints.indices) {
                                val x = idx * stepX
                                drawLine(
                                    color = Color.White.copy(alpha = 0.7f),
                                    start = Offset(x, 0f),
                                    end = Offset(x, chartHeight),
                                    strokeWidth = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )

                                val point = dataPoints[idx]
                                // CPU dot
                                if (metricMode != MetricViewMode.RAM_ONLY) {
                                    val cpuY = chartHeight - ((point.cpuPercent / 100f).coerceIn(0f, 1f) * chartHeight)
                                    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(x, cpuY))
                                    drawCircle(color = EmeraldLight, radius = 2.dp.toPx(), center = Offset(x, cpuY))
                                }

                                // RAM dot
                                if (metricMode != MetricViewMode.CPU_ONLY) {
                                    val ramY = chartHeight - ((point.ramPercent / 100f).coerceIn(0f, 1f) * chartHeight)
                                    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(x, ramY))
                                    drawCircle(color = CyanLight, radius = 2.dp.toPx(), center = Offset(x, ramY))
                                }
                            }
                        }
                    }
                }

                // Inspection Tooltip Pill Overlay
                scrubIndex?.let { idx ->
                    if (idx in dataPoints.indices) {
                        val inspected = dataPoints[idx]
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F1720).copy(alpha = 0.95f))
                                .border(1.dp, SlateDarkBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = inspected.timeLabel,
                                    color = TextDarkMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "CPU: ${inspected.cpuPercent.toInt()}%",
                                    color = EmeraldLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "RAM: ${inspected.ramPercent.toInt()}% (${(inspected.ramMb).toInt()}MB)",
                                    color = CyanLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bottom Time Axis Labels
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = dataPoints.firstOrNull()?.timeLabel ?: "", color = TextDarkMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(text = dataPoints.getOrNull(dataPoints.size / 2)?.timeLabel ?: "", color = TextDarkMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "Now", color = TextDarkMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
            }

            // Legend & Inspection Guidance Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldLight))
                        Text("CPU %", color = TextDarkSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyanLight))
                        Text("RAM %", color = TextDarkSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Touch or drag to inspect data points",
                    color = TextDarkMuted,
                    fontSize = 9.sp
                )
            }
        }
    }
}
