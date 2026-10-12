package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ResourceLinearBar(
    label: String,
    valueText: String,
    progress: Float, // 0.0 to 1.0
    color: Color = EmeraldPrimary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextDarkSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = valueText,
                color = TextDarkPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = SlateDarkBorder
        )
    }
}

@Composable
fun SparklineChart(
    dataPoints: List<Float>,
    lineColor: Color = EmeraldLight,
    fillColor: Color = EmeraldPrimary.copy(alpha = 0.15f),
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDarkCard)
            .border(1.dp, SlateDarkBorder, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val minVal = (dataPoints.minOrNull() ?: 0f) * 0.9f
            val maxVal = (dataPoints.maxOrNull() ?: 100f) * 1.1f
            val range = (maxVal - minVal).coerceAtLeast(1f)

            val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

            val path = Path()
            val fillPath = Path()

            dataPoints.forEachIndexed { index, value ->
                val x = index * stepX
                val normalizedY = ((value - minVal) / range).coerceIn(0f, 1f)
                val y = height - (normalizedY * height)

                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, height)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            drawPath(path = fillPath, color = fillColor)
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Current endpoint dot
            val lastX = (dataPoints.size - 1) * stepX
            val lastNormalized = ((dataPoints.last() - minVal) / range).coerceIn(0f, 1f)
            val lastY = height - (lastNormalized * height)
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(lastX, lastY)
            )
            drawCircle(
                color = lineColor,
                radius = 5.dp.toPx(),
                center = Offset(lastX, lastY),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
