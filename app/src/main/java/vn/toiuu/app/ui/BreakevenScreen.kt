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
import vn.toiuu.app.core.Breakeven
import vn.toiuu.app.core.BreakevenInput
import vn.toiuu.app.core.fmt
import vn.toiuu.app.core.fmtClean
import vn.toiuu.app.core.money
import vn.toiuu.app.core.parseNum

@Composable
fun BreakevenScreen(s: AppState) {
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
                    }

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
                            "Biên an toàn dưới 20%: một cú giảm cầu nhỏ là hoà vốn hoặc lỗ."
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
                "Hoà vốn luôn quan trọng hơn lợi nhuận dự kiến: nó cho biết bạn còn " +
                    "bao nhiêu dư địa trước khi mất tiền. DOL = (p − MC)·Q / π."
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}
