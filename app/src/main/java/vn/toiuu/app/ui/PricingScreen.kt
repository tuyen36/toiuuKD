package vn.toiuu.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.toiuu.app.AppState
import vn.toiuu.app.Screen
import vn.toiuu.app.core.Pricing
import vn.toiuu.app.core.PricingInput
import vn.toiuu.app.core.fmt
import vn.toiuu.app.core.fmtClean
import vn.toiuu.app.core.money
import vn.toiuu.app.core.parseNum

@Composable
fun PricingScreen(s: AppState) {
    val a = parseNum(s.priceA)
    val b = parseNum(s.priceB)
    val fc = parseNum(s.priceFc)
    val vc = parseNum(s.priceVc)
    val qc = parseNum(s.priceQc)

    val input = if (a != null && b != null && fc != null && vc != null && qc != null &&
        a > 0.0 && b > 0.0 && vc >= 0.0 && fc >= 0.0 && qc >= 0.0
    ) {
        PricingInput(a, b, fc, vc, qc)
    } else {
        null
    }

    val res = remember(input) {
        input?.let { runCatching { Pricing.solve(it) }.getOrNull() }
    }

    val unit = s.priceUnit

    ScreenFrame(
        "Giá & sản lượng",
        "Tối đa lợi nhuận khi chọn giá",
        onBack = { s.screen = Screen.HOME }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            SectionCard("Mô hình") {
                Row {
                    NumField("a  (Q = a − b·p)", s.priceA, { s.priceA = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    NumField("b  (độ dốc cầu)", s.priceB, { s.priceB = it }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    NumField("Định phí FC", s.priceFc, { s.priceFc = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    NumField("Biến phí / đơn vị", s.priceVc, { s.priceVc = it }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                NumField(
                    "Hệ số Q² (chi phí tăng nhanh dần, thường = 0)",
                    s.priceQc, { s.priceQc = it }
                )
                Spacer(Modifier.height(10.dp))
                UnitPicker(s.priceUnit) { s.priceUnit = it }
            }

            Spacer(Modifier.height(10.dp))

            when {
                res == null -> SectionCard("Kết quả") {
                    WarnCard("Chưa đọc được số liệu. Cần a > 0, b > 0 và các chi phí không âm.")
                }

                else -> {
                    SectionCard("Kết quả") {
                        ResultRow(
                            "Giá tối ưu", money(res.pStar, unit), hero = true,
                            hint = "điểm MR = MC"
                        )
                        Divider()
                        ResultRow("Sản lượng bán", fmtClean(res.qStar, 1) + " / kỳ")
                        ResultRow("Doanh thu", money(res.revenue, unit))
                        ResultRow("Tổng chi phí", money(res.cost, unit))
                        ResultRow("Lợi nhuận", money(res.profit, unit), hero = true)
                        Divider()
                        ResultRow("Lãi gộp (chưa trừ FC)", money(res.contribution, unit))
                        ResultRow("Chi phí biên MC", money(res.mc, unit))
                        ResultRow("Doanh thu biên MR", money(res.mr, unit))
                    }

                    Spacer(Modifier.height(10.dp))

                    SectionCard("Đọc kết quả") {
                        ResultRow(
                            "|ε| tại điểm tối ưu", fmt(res.elasticity, 2),
                            hint = "cầu càng co giãn, markup càng mỏng"
                        )
                        ResultRow("Tỷ lệ markup (p − MC)/p", fmt(res.markup * 100.0, 1) + "%")
                        Spacer(Modifier.height(6.dp))
                        Caption(
                            "Quy tắc markup: p* = MC / (1 − 1/|ε|). " +
                                "Nếu |ε| = 2,5 thì giá tối ưu bằng 1,67 lần chi phí biên."
                        )
                    }

                    val warns = buildList {
                        if (res.boundary) add(
                            "Nghiệm nằm sát biên. Mô hình có thể sai cấu trúc " +
                                "(bẫy A8 — áp dụng ngoài miền hợp lệ)."
                        )
                        if (res.secondOrder >= 0.0) add(
                            "Điều kiện bậc hai không thoả (π″ ≥ 0): điểm tìm được " +
                                "chưa chắc là cực đại (bẫy B9)."
                        )
                        if (res.qStar < 1e-6) add(
                            "Sản lượng gần 0: giá tối ưu vượt quá giá chặn a/b của thị trường."
                        )
                        if (res.profit < 0.0) add(
                            "Đang lỗ " + money(-res.profit, unit) +
                                " mỗi kỳ: lãi gộp chưa bù được định phí. " +
                                "Dùng công cụ Điểm hoà vốn để biết cần bán bao nhiêu."
                        )
                        if (res.mc > res.pStar + 1e-9) add(
                            "Giá thấp hơn chi phí biên: bán thêm một đơn vị là lỗ thêm."
                        )
                    }
                    if (warns.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        warns.forEach { WarnCard(it) }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            NoteCard(
                "Chi phí biên phải bằng doanh thu biên, không phải bằng giá. " +
                    "Với cầu tuyến tính và chi phí tuyến tính: p* = (a + b·c) / (2b)."
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}
