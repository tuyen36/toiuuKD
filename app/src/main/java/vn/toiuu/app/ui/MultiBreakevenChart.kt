package vn.toiuu.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.core.BreakevenMulti
import vn.toiuu.app.core.BreakevenMultiResult

@Composable
fun MultiBreakevenChart(
    products: List<BreakevenMulti.Product>,
    result: BreakevenMultiResult?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FBFE))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "TỔNG HỢP ĐA SẢN PHẨM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                result?.let { if (it.feasible) "Feasible" else "Not feasible" } ?: "—",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = result?.let { if (it.feasible) Green else Red } ?: Muted
            )
        }

        Spacer(Modifier.height(10.dp))

        val labelColors = listOf(
            Color(0xFF1A365D),
            Color(0xFFB7791F),
            Color(0xFF2E7D32),
            Color(0xFFC62828)
        )

        Canvas(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val padL = 60f
            val padT = 24f
            val w = size.width - padL - 24f
            val h = size.height - padT - 40f

            if (result != null && result.feasible) {
                val qTotal = result.totalQ.coerceAtLeast(1.0)

                // Draw stacked bar chart (simplified visual)
                val barCount = result.products.size
                val barW = w / barCount * 0.6f
                val gapW = w / barCount * 0.4f

                for ((idx, prod) in result.products.withIndex()) {
                    val x = padL + idx * (barW + gapW) + gapW / 2
                    val y = toY(prod.qOpt / qTotal * 100f, h, padT)
                    val barH = (h * (prod.qOpt / qTotal).toFloat()).coerceAtMost(h)

                    val color = labelColors[idx % labelColors.size]
                    drawRect(
                        color = color.copy(alpha = 0.85f),
                        topLeft = androidx.compose.ui.geometry.Offset(x, y + barH - h),
                        size = androidx.compose.ui.geometry.Size(barW, barH)
                    )
                    // Label
                    val paint = android.graphics.Paint().apply {
                        color = Color.White.toArgb()
                        textSize = 22f
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        prod.name.take(3),
                        x + barW / 2 - 20f,
                        y + barH + 12f,
                        paint
                    )
                }
            } else {
                // Show message when not feasible
                val textPaint = android.graphics.Paint().apply {
                    color = Muted.toArgb()
                    textSize = 28f
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "Chưa đủ dữ liệu đa sản phẩm",
                    padL + 20f,
                    padT + h / 2 + 10f,
                    textPaint
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Legend + Summary
        Row(Modifier.fillMaxWidth()) {
            result?.products?.forEachIndexed { idx, p ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 10.dp)) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(labelColors[idx % labelColors.size])
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(p.name, fontSize = 10.sp, color = Muted)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        result?.let {
            if (it.feasible) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenBg)
                        .padding(8.dp)
                ) {
                    Text(
                        "Tổng lợi nhuận tối ưu: ${fmtClean(it.overallProfit, 0)} đ · Tổng Q: ${fmtClean(it.totalQ, 1)} · ${it.products.size} sản phẩm",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Green
                    )
                }
            }
        }
    }
}
