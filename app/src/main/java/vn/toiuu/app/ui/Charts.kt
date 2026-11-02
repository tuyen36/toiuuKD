package vn.toiuu.app.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.core.BreakevenInput
import vn.toiuu.app.core.BreakevenResult
import vn.toiuu.app.core.Constraint
import vn.toiuu.app.core.LpResult
import vn.toiuu.app.core.Pricing
import vn.toiuu.app.core.PricingInput
import vn.toiuu.app.core.PricingResult
import vn.toiuu.app.core.fmtClean
import kotlin.math.max

// ------------------------------------------------------------------ BIEU DO HOA VON

@Composable
fun BreakevenChart(
    input: BreakevenInput,
    res: BreakevenResult,
    unit: String,
    modifier: Modifier = Modifier
) {
    val qBe = res.qLow ?: input.q
    val qMax = (max(input.q, qBe) * 1.55).coerceAtLeast(10.0)

    val tcAtMax = input.fc + input.vc * qMax + input.qc * qMax * qMax
    val trAtMax = input.price * qMax
    val vMax = max(tcAtMax, trAtMax) * 1.15

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FBFE))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "ĐỒ THỊ HÒA VỐN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.weight(1f))
            LegendDot(Green, "Doanh thu (TR)")
            Spacer(Modifier.width(8.dp))
            LegendDot(Red, "Chi phí (TC)")
        }

        Spacer(Modifier.height(10.dp))

        Canvas(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val padL = 60f
            val padR = 24f
            val padT = 24f
            val padB = 40f

            val w = size.width - padL - padR
            val h = size.height - padT - padB

            fun toX(qVal: Double): Float = padL + (qVal / qMax).toFloat() * w
            fun toY(vVal: Double): Float = padT + (1f - (vVal / vMax).toFloat()) * h

            val paint = Paint().apply {
                color = Muted.toArgb()
                textSize = 24f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }

            // Grid lines
            for (step in 1..3) {
                val frac = step / 4f
                val yGrid = padT + h * (1f - frac)
                drawLine(
                    color = Line.copy(alpha = 0.6f),
                    start = Offset(padL, yGrid),
                    end = Offset(padL + w, yGrid),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Axes
            drawLine(Line, Offset(padL, padT), Offset(padL, padT + h), 2.dp.toPx())
            drawLine(Line, Offset(padL, padT + h), Offset(padL + w, padT + h), 2.dp.toPx())

            // Axis labels
            drawContext.canvas.nativeCanvas.drawText("0", padL - 25f, padT + h + 10f, paint)
            drawContext.canvas.nativeCanvas.drawText(
                "Q (${fmtClean(qMax, 0)})",
                padL + w - 40f,
                padT + h + 32f,
                paint
            )

            val samples = 60
            val pathTR = Path()
            val pathTC = Path()

            for (i in 0..samples) {
                val qVal = (qMax * i) / samples
                val trVal = input.price * qVal
                val tcVal = input.fc + input.vc * qVal + input.qc * qVal * qVal

                val x = toX(qVal)
                val yTr = toY(trVal)
                val yTc = toY(tcVal)

                if (i == 0) {
                    pathTR.moveTo(x, yTr)
                    pathTC.moveTo(x, yTc)
                } else {
                    pathTR.lineTo(x, yTr)
                    pathTC.lineTo(x, yTc)
                }
            }

            drawPath(pathTR, Green, style = Stroke(width = 3.dp.toPx()))
            drawPath(pathTC, Red, style = Stroke(width = 3.dp.toPx()))

            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            // Breakeven Point
            if (res.qLow != null && res.qLow > 0.0 && res.qLow <= qMax) {
                val bepX = toX(res.qLow)
                val bepY = toY(input.price * res.qLow)

                drawLine(
                    color = Amber,
                    start = Offset(bepX, padT + h),
                    end = Offset(bepX, bepY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = dashEffect
                )
                drawCircle(Amber, radius = 6.dp.toPx(), center = Offset(bepX, bepY))
                drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(bepX, bepY))

                val bepPaint = Paint().apply {
                    color = AmberInk.toArgb()
                    textSize = 26f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "Hòa vốn: ${fmtClean(res.qLow, 1)}",
                    (bepX - 60f).coerceAtLeast(padL),
                    bepY - 14f,
                    bepPaint
                )
            }

            // Current Q Point
            if (input.q in 0.0..qMax) {
                val curX = toX(input.q)
                val curTrY = toY(input.price * input.q)

                drawLine(
                    color = NavySoft,
                    start = Offset(curX, padT + h),
                    end = Offset(curX, curTrY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = dashEffect
                )
                drawCircle(Navy, radius = 5.dp.toPx(), center = Offset(curX, curTrTr = curTrY))

                val curPaint = Paint().apply {
                    color = Navy.toArgb()
                    textSize = 24f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "Hiện tại (${fmtClean(input.q, 1)})",
                    (curX - 50f).coerceIn(padL, padL + w - 100f),
                    padT + h + 30f,
                    curPaint
                )
            }
        }
    }
}

// ------------------------------------------------------------------ BIEU DO DINH GIA

@Composable
fun PricingChart(
    input: PricingInput,
    res: PricingResult,
    unit: String,
    modifier: Modifier = Modifier
) {
    val pMax = if (input.b > 1e-9) input.a / input.b else 100.0

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FBFE))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "ĐƯỜNG CONG LỢI NHUẬN THEO GIÁ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.weight(1f))
            LegendDot(Navy, "Lợi nhuận π(p)")
        }

        Spacer(Modifier.height(10.dp))

        Canvas(
            Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) {
            val padL = 55f
            val padR = 24f
            val padT = 24f
            val padB = 40f

            val w = size.width - padL - padR
            val h = size.height - padT - padB

            val samples = 70
            val profitList = (0..samples).map { i ->
                val pVal = (pMax * i) / samples
                Pricing.profitAt(input, pVal)
            }

            val maxProfit = (profitList.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
            val minProfit = (profitList.minOrNull() ?: 0.0).coerceAtMost(0.0)
            val profitSpan = (maxProfit - minProfit).coerceAtLeast(1.0)

            fun toX(pVal: Double): Float = padL + (pVal / pMax).toFloat() * w
            fun toY(profit: Double): Float = padT + (1f - ((profit - minProfit) / profitSpan).toFloat()) * h

            val yZero = toY(0.0)

            // Zero line
            if (yZero in padT..(padT + h)) {
                drawLine(
                    color = Line,
                    start = Offset(padL, yZero),
                    end = Offset(padL + w, yZero),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // Profit Curve
            val curvePath = Path()
            val fillPath = Path()
            fillPath.moveTo(padL, yZero)

            for (i in 0..samples) {
                val pVal = (pMax * i) / samples
                val profit = profitList[i]
                val x = toX(pVal)
                val y = toY(profit)

                if (i == 0) {
                    curvePath.moveTo(x, y)
                } else {
                    curvePath.lineTo(x, y)
                }
                fillPath.lineTo(x, y)
            }
            fillPath.lineTo(padL + w, yZero)
            fillPath.close()

            // Fill under curve
            drawPath(fillPath, Navy.copy(alpha = 0.08f))
            drawPath(curvePath, Navy, style = Stroke(width = 3.dp.toPx()))

            // Optimal point
            if (res.pStar in 0.0..pMax) {
                val optX = toX(res.pStar)
                val optY = toY(res.profit)

                val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                drawLine(
                    color = Amber,
                    start = Offset(optX, padT + h),
                    end = Offset(optX, optY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = dash
                )

                drawCircle(Amber, radius = 6.dp.toPx(), center = Offset(optX, optY))
                drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(optX, optY))

                val optPaint = Paint().apply {
                    color = AmberInk.toArgb()
                    textSize = 25f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "Đỉnh lãi: p* = ${fmtClean(res.pStar, 1)} $unit",
                    (optX - 70f).coerceIn(padL, padL + w - 160f),
                    optY - 14f,
                    optPaint
                )
            }

            // Labels
            val paint = Paint().apply {
                color = Muted.toArgb()
                textSize = 24f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("0", padL - 25f, padT + h + 10f, paint)
            drawContext.canvas.nativeCanvas.drawText(
                "Giá p (chặn ${fmtClean(pMax, 1)})",
                padL + w - 80f,
                padT + h + 32f,
                paint
            )
        }
    }
}

// ------------------------------------------------------------------ BIEU DO QUY HOACH TUYEN TINH

@Composable
fun LpChart(
    cons: List<Constraint>,
    res: LpResult,
    modifier: Modifier = Modifier
) {
    if (!res.feasible || res.vertices.isEmpty()) return

    val maxX = (res.vertices.map { it.x } + listOf(res.x, 1.0)).maxOrNull()!! * 1.35
    val maxY = (res.vertices.map { it.y } + listOf(res.y, 1.0)).maxOrNull()!! * 1.35

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FBFE))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "VÙNG NGHIỆM KHẢ THI (2 BIẾN)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.weight(1f))
            LegendDot(NavySoft, "Vùng khả thi")
            Spacer(Modifier.width(8.dp))
            LegendDot(Amber, "Nghiệm tối ưu")
        }

        Spacer(Modifier.height(10.dp))

        Canvas(
            Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            val padL = 50f
            val padR = 24f
            val padT = 24f
            val padB = 40f

            val w = size.width - padL - padR
            val h = size.height - padT - padB

            fun toX(xVal: Double): Float = padL + (xVal / maxX).toFloat() * w
            fun toY(yVal: Double): Float = padT + (1f - (yVal / maxY).toFloat()) * h

            // Axes
            drawLine(Line, Offset(padL, padT), Offset(padL, padT + h), 2.dp.toPx())
            drawLine(Line, Offset(padL, padT + h), Offset(padL + w, padT + h), 2.dp.toPx())

            // Draw constraint lines
            for (c in cons) {
                if (c.a > 0 || c.b > 0) {
                    val xIntercept = if (c.a > 1e-9) c.c / c.a else maxX * 2
                    val yIntercept = if (c.b > 1e-9) c.c / c.b else maxY * 2

                    drawLine(
                        color = Line.copy(alpha = 0.9f),
                        start = Offset(toX(0.0), toY(yIntercept)),
                        end = Offset(toX(xIntercept), toY(0.0)),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }

            // Draw feasible polygon
            val polyPath = Path()
            res.vertices.forEachIndexed { i, v ->
                val px = toX(v.x)
                val py = toY(v.y)
                if (i == 0) polyPath.moveTo(px, py) else polyPath.lineTo(px, py)
            }
            polyPath.close()

            drawPath(polyPath, Navy.copy(alpha = 0.15f))
            drawPath(polyPath, NavySoft, style = Stroke(width = 2.dp.toPx()))

            val vertexPaint = Paint().apply {
                color = Muted.toArgb()
                textSize = 23f
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }

            // Vertices dots and labels
            for (v in res.vertices) {
                val vx = toX(v.x)
                val vy = toY(v.y)
                drawCircle(NavySoft, radius = 3.5.dp.toPx(), center = Offset(vx, vy))
                drawContext.canvas.nativeCanvas.drawText(
                    v.label,
                    vx + 8f,
                    vy - 8f,
                    vertexPaint
                )
            }

            // Optimal point
            val optX = toX(res.x)
            val optY = toY(res.y)

            val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            drawLine(
                color = Amber,
                start = Offset(optX, padT + h),
                end = Offset(optX, optY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dash
            )
            drawLine(
                color = Amber,
                start = Offset(padL, optY),
                end = Offset(optX, optY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dash
            )

            drawCircle(Amber, radius = 7.dp.toPx(), center = Offset(optX, optY))
            drawCircle(Color.White, radius = 3.5.dp.toPx(), center = Offset(optX, optY))

            val optPaint = Paint().apply {
                color = AmberInk.toArgb()
                textSize = 25f
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
                "Tối ưu (${fmtClean(res.x, 1)}, ${fmtClean(res.y, 1)})",
                (optX - 50f).coerceIn(padL, padL + w - 160f),
                optY - 14f,
                optPaint
            )

            // Axes names
            val axisPaint = Paint().apply {
                color = Muted.toArgb()
                textSize = 23f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("Sản phẩm 1 (x)", padL + w - 90f, padT + h + 32f, axisPaint)
            drawContext.canvas.nativeCanvas.drawText("SP 2 (y)", padL - 45f, padT + 15f, axisPaint)
        }
    }
}

@Composable
private fun LegendDot(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 10.5.sp, color = Muted)
    }
}
