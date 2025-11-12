package com.example.moneynote.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneynote.ui.ChartData
import com.example.moneynote.ui.formatCurrency
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DonutChart(
    data: List<ChartData>,
    type: String,
    modifier: Modifier = Modifier,
    gapDegrees: Float = 2f // Khoảng cách giữa các danh mục
) {
    if (data.isEmpty()) return

    val totalAmount = data.sumOf { it.amount }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val chartSize = 200.dp.toPx()
            val radius = chartSize / 2
            val strokeWidth = 50.dp.toPx()
            val outerRadius = radius + strokeWidth / 2
            var startAngle = -90f

            data.forEach { chartData ->
                val sweepAngle = 360f * chartData.percentage - gapDegrees
                val middleAngle = startAngle + sweepAngle / 2

                // Vẽ cung tròn với gap giữa các danh mục
                drawArc(
                    color = chartData.color,
                    startAngle = startAngle + gapDegrees / 2,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(chartSize, chartSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt) // góc nhọn bo nhẹ
                )

                // Chỉ vẽ nhãn nếu >5%
                if (chartData.percentage > 0.049f) {
                    val angleRad = Math.toRadians(middleAngle.toDouble())
                    val startX = centerX + (outerRadius * cos(angleRad)).toFloat()
                    val startY = centerY + (outerRadius * sin(angleRad)).toFloat()
                    val lineLength = 40.dp.toPx()
                    val endX = centerX + ((outerRadius + lineLength) * cos(angleRad)).toFloat()
                    val endY = centerY + ((outerRadius + lineLength) * sin(angleRad)).toFloat()
                    val horizontalLineLength = 20.dp.toPx()
                    val isLeft = middleAngle > 90 && middleAngle < 270
                    val horizontalEndX = if (isLeft) endX - horizontalLineLength else endX + horizontalLineLength

                    // Đường từ chart ra ngoài
                    drawLine(chartData.color, Offset(startX, startY), Offset(endX, endY), 2.dp.toPx())
                    drawLine(chartData.color, Offset(endX, endY), Offset(horizontalEndX, endY), 2.dp.toPx())

                    // Vẽ nhãn (text)
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textAlign = if (isLeft) android.graphics.Paint.Align.RIGHT else android.graphics.Paint.Align.LEFT
                            textSize = 13.sp.toPx()
                            color = android.graphics.Color.WHITE
                            isFakeBoldText = true
                        }
                        drawText("${"%.1f".format(chartData.percentage * 100)}%", horizontalEndX + if (isLeft) -5.dp.toPx() else 5.dp.toPx(), endY - 2.dp.toPx(), paint)

                        paint.apply {
                            textSize = 10.sp.toPx()
                            alpha = 180
                            isFakeBoldText = false
                        }
                        drawText(chartData.category, horizontalEndX + if (isLeft) -5.dp.toPx() else 5.dp.toPx(), endY + 12.dp.toPx(), paint)
                    }
                }

                startAngle += 360f * chartData.percentage
            }
        }

        // Tổng tiền ở giữa
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (type == "expense") "Tổng chi" else "Tổng thu",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(totalAmount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// =======================
// PREVIEW
// =======================
@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    val mockData = listOf(
        ChartData("Ăn uống", 800000.0, 0.447f, Color(0xFF00B894)),
        ChartData("Đi lại", 120000.0, 0.067f, Color(0xFF0984E3)),
        ChartData("Chi tiêu hàng ngày", 400000.0, 0.225f, Color(0xFFFFC312)),
        ChartData("Y tế", 90000.0, 0.051f, Color(0xFFE17055)),
        ChartData("Khác", 380000.0, 0.210f, Color(0xFF6C5CE7))
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DonutChart(
            data = mockData,
            type = "expense",
            gapDegrees = 4f // Tăng khoảng cách giữa các danh mục
        )
    }
}
