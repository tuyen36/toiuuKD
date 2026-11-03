package vn.toiuu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.AppState
import vn.toiuu.app.Screen
import vn.toiuu.app.core.Breakeven
import vn.toiuu.app.core.BreakevenInput
import vn.toiuu.app.core.fmt
import vn.toiuu.app.core.fmtClean
import vn.toiuu.app.core.money
import vn.toiuu.app.core.parseNum
import vn.toiuu.app.data.ToolKind

@Composable
fun BreakevenScreen(s: AppState) {
    val context = LocalContext.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }

    val p = parseNum(s.bePrice)
    val vc = parseNum(s.beVc)
    val qc = parseNum(s.beQc)
    val fc = parseNum(s.beFc)
    val q = parseNum(s.beQ)

    val input = if (p != null && vc != null && qc != null && fc != null && q != null &&
        p > 0.0 && vc >= 0.0 && qc >= 0.0 && fc >= 0.0 && q >= 0.0
    ) {
        BreakevenInput(p, vc, qc, fc, q)
    } else {
        null
    }

    val res = remember(input) {
        input?.let { runCatching { Breakeven.solve(it) }.getOrNull() }
    }

    val unit = s.beUnit

    if (showSaveDialog) {
        val vals = mapOf(
            "price" to s.bePrice,
            "vc" to s.beVc,
            "qc" to s.beQc,
            "fc" to s.beFc,
            "q" to s.beQ,
            "unit" to s.beUnit
        )
        SaveScenarioDialog(
            tool = ToolKind.BREAKEVEN,
            currentValues = vals,
            onDismiss = { showSaveDialog = false },
            onSaved = {}
        )
    }

    if (showLoadDialog) {
        LoadScenarioDialog(
            tool = ToolKind.BREAKEVEN,
            onDismiss = { showLoadDialog = false },
            onSelect = { scenario ->
                scenario.values["price"]?.let { s.bePrice = it }
                scenario.values["vc"]?.let { s.beVc = it }
                scenario.values["qc"]?.let { s.beQc = it }
                scenario.values["fc"]?.let { s.beFc = it }
                scenario.values["q"]?.let { s.beQ = it }
                scenario.values["unit"]?.let { s.beUnit = it }
            }
        )
    }

    ScreenFrame(
        "Điểm hoà vốn",
        "Bán bao nhiêu để không lỗ",
        onBack = { s.screen = Screen.HOME }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            // Scenario Action Bar
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Sky)
                        .clickable { showSaveDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("💾 Lưu kịch bản", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Navy)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .clickable { showLoadDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("📂 Kịch bản đã lưu", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavySoft)
                }
            }

            SectionCard("Số liệu") {
                Row {
                    NumField("Giá bán p", s.bePrice, { s.bePrice = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    NumField("Biến phí / đơn vị", s.beVc, { s.beVc = it }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    NumField("Định phí FC / kỳ", s.beFc, { s.beFc = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    NumField("Sản lượng hiện tại Q", s.beQ, { s.beQ = it }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                NumField("Hệ số Q² (thường = 0)", s.beQc, { s.beQc = it })
                Spacer(Modifier.height(10.dp))
                UnitPicker(s.beUnit) { s.beUnit = it }
            }

            Spacer(Modifier.height(10.dp))

            when {
                res == null -> SectionCard("Kết quả") {
                    WarnCard("Chưa đọc được số liệu. Giá bán và sản lượng phải dương.")
                }

                else -> {
                    SectionCard("Kết quả") {
                        if (res.qLow != null) {
                            ResultRow(
                                "Điểm hoà vốn", fmtClean(res.qLow, 1) + " / kỳ",
                                hero = true,
                                hint = if (res.qHigh != null) {
                                    "còn một điểm nữa ở " + fmtClean(res.qHigh, 1)
                                } else null
                            )
                        } else {
                            ResultRow("Điểm hoà vốn", "Không có", hero = true)
                        }
                        Divider()
                        ResultRow("Lợi nhuận tại Q hiện tại", money(res.profit, unit))
                        ResultRow("Lãi gộp / đơn vị", money(res.unitMargin, unit))
                        ResultRow("Tổng lãi gộp", money(res.contribution, unit))
                        Divider()
                        ResultRow(
                            "Đòn bẩy hoạt động (DOL)",
                            if (res.dol == null) "—" else fmt(res.dol, 2) + " lần",
                            hint = "1% sản lượng → DOL% lợi nhuận"
                        )
                        ResultRow(
                            "Biên an toàn",
                            if (res.marginOfSafety == null) "—"
                            else fmt(res.marginOfSafety * 100.0, 1) + "%"
                        )
                        ResultRow("Giá đóng cửa (AVC min)", money(res.shutdownPrice, unit))

                        Button(
                        onClick = {
                            val report = buildString {
                                val safeInput = input
                                if (safeInput != null) {
                                    appendLine("📊 PHÂN TÍCH ĐIỂM HÒA VỐN")
                                    appendLine("• Giá bán: ${money(safeInput.price, unit)}")
                                    appendLine("• Biến phí / đv: ${money(safeInput.vc, unit)}")
                                    appendLine("• Định phí: ${money(safeInput.fc, unit)}")
                                    appendLine("• Sản lượng hiện tại Q: ${fmtClean(safeInput.q, 1)}")
                                    appendLine("---------------------------")
                                    appendLine("• Điểm hòa vốn: ${res.qLow?.let { fmtClean(it, 1) + " / kỳ" } ?: "Không có"}")
                                    appendLine("• Lợi nhuận tại Q: ${money(res.profit, unit)}")
                                    appendLine("• Lãi gộp / đv: ${money(res.unitMargin, unit)}")
                                    appendLine("• Đòn bẩy DOL: ${res.dol?.let { fmt(it, 2) + " lần" } ?: "—"}")
                                    appendLine("• Biên an toàn: ${res.marginOfSafety?.let { fmt(it * 100, 1) + "%" } ?: "—"}")
                                    appendLine("[Ứng dụng Tối Ưu Kinh Doanh]")
                                }
                            }
                            shareAnalysisResult(context, report, "Phân tích điểm hòa vốn")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("📤 Chia sẻ kết quả", fontSize = 13.5.sp, color = Color.White)
                    }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Bieu do truc quan hoa von
                    BreakevenChart(input = input, res = res, unit = unit)

                    val warns = buildList {
                        if (!res.hasBreakeven) add(
                            "Không có điểm hoà vốn: giá không vượt được biến phí đơn vị. " +
                                "Bán càng nhiều lỗ càng nhiều (bẫy B4)."
                        ) else if (res.qLow != null && res.q < res.qLow) add(
                            "Đang dưới điểm hoà vốn " + fmtClean(res.qLow, 1) +
                                " đơn vị. Mỗi kỳ vẫn lỗ " + money(-res.profit, unit) + "."
                        )
                        if (!res.aboveShutdown) add(
                            "Giá thấp hơn chi phí biến phí trung bình: về ngắn hạn nên đóng cửa."
                        )
                        if (res.dol != null && res.dol > 5.0) add(
                            "Đòn bẩy rất cao (" + fmt(res.dol, 1) + " lần): " +
                                "sản lượng giảm nhẹ cũng xoá sạch lợi nhuận."
                        )
                        if (res.marginOfSafety != null && res.marginOfSafety < 0.2) add(
                            "Biên an toàn dưới 20%: doanh thu giảm nhẹ là rơi vào vùng lỗ."
                        )
                    }

                    if (warns.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        SectionCard("Lưu ý rủi ro") {
                            for (w in warns) {
                                WarnCard(w)
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
