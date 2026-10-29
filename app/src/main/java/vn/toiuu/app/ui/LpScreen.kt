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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.AppState
import vn.toiuu.app.LpForm
import vn.toiuu.app.Screen
import vn.toiuu.app.core.Constraint
import vn.toiuu.app.core.Lp
import vn.toiuu.app.core.fmt
import vn.toiuu.app.core.fmtClean
import vn.toiuu.app.core.parseNum

@Composable
fun LpScreen(s: AppState) {
    val c1 = parseNum(s.lpC1)
    val c2 = parseNum(s.lpC2)

    val cons = s.lpCons.mapNotNull { f ->
        val a = parseNum(f.a)
        val b = parseNum(f.b)
        val c = parseNum(f.c)
        if (a != null && b != null && c != null) Constraint(a, b, c, f.name) else null
    }
    val allValid = cons.size == s.lpCons.size

    val res = remember(c1, c2, cons, s.lpInteger) {
        if (c1 != null && c2 != null && allValid && cons.isNotEmpty()) {
            runCatching { Lp.solve(c1, c2, cons, s.lpInteger) }.getOrNull()
        } else {
            null
        }
    }

    ScreenFrame(
        "Quy hoạch 2 biến",
        "Chia nguồn lực giữa hai sản phẩm",
        onBack = { s.screen = Screen.HOME }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            SectionCard("Hàm mục tiêu  (tối đa)") {
                Row {
                    NumField("Lãi / đơn vị x", s.lpC1, { s.lpC1 = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    NumField("Lãi / đơn vị y", s.lpC2, { s.lpC2 = it }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(6.dp))
                Caption("Mục tiêu: max  c₁·x + c₂·y")
            }

            Spacer(Modifier.height(10.dp))

            SectionCard("Ràng buộc  (a·x + b·y ≤ c)") {
                s.lpCons.forEachIndexed { index, form ->
                    if (index > 0) Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            form.name,
                            fontSize = 12.sp,
                            color = Muted,
                            modifier = Modifier.weight(1f)
                        )
                        if (s.lpCons.size > 1) {
                            TextButton(onClick = {
                                s.lpCons = s.lpCons.filterIndexed { k, _ -> k != index }
                            }) {
                                Text("Xoá", fontSize = 11.sp, color = Muted)
                            }
                        }
                    }
                    Row {
                        NumField("a", form.a, { v ->
                            s.lpCons = s.lpCons.toMutableList().also { it[index] = form.copy(a = v) }
                        }, Modifier.weight(1f))
                        Spacer(Modifier.width(6.dp))
                        NumField("b", form.b, { v ->
                            s.lpCons = s.lpCons.toMutableList().also { it[index] = form.copy(b = v) }
                        }, Modifier.weight(1f))
                        Spacer(Modifier.width(6.dp))
                        NumField("c", form.c, { v ->
                            s.lpCons = s.lpCons.toMutableList().also { it[index] = form.copy(c = v) }
                        }, Modifier.weight(1f))
                    }
                }

                if (s.lpCons.size < 6) {
                    Spacer(Modifier.height(10.dp))
                    val newName = "Ràng buộc " + (s.lpCons.size + 1)
                    TextButton(onClick = {
                        s.lpCons = s.lpCons + LpForm("1", "1", "100", newName)
                    }) {
                        Text("+ Thêm ràng buộc", fontSize = 12.sp, color = Navy)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Sky)
                    .clickable { s.lpInteger = !s.lpInteger }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = s.lpInteger, onCheckedChange = { s.lpInteger = it })
                Column {
                    Text("Chỉ nhận nghiệm nguyên", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                    Text("Số người, số bàn, số chuyến — không chia lẻ", fontSize = 11.sp, color = Muted)
                }
            }

            Spacer(Modifier.height(10.dp))

            when {
                res == null -> SectionCard("Kết quả") {
                    WarnCard("Chưa đọc được số liệu hoặc ràng buộc không hợp lệ.")
                }

                !res.feasible -> SectionCard("Kết quả") {
                    WarnCard(
                        "Miền khả thi rỗng: các ràng buộc mâu thuẫn nhau " +
                            "(ví dụ x ≤ 10 nhưng x ≥ 20)."
                    )
                }

                else -> {
                    SectionCard("Kết quả") {
                        ResultRow("x tối ưu", fmtClean(res.x, 2), hero = true)
                        ResultRow("y tối ưu", fmtClean(res.y, 2), hero = true)
                        Divider()
                        ResultRow(
                            "Giá trị mục tiêu", fmt(res.value, 2),
                            hint = if (res.integer) "nghiệm nguyên" else "nghiệm thực"
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    SectionCard("Các đỉnh của miền khả thi") {
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text("Đỉnh", Modifier.width(42.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Muted)
                            Text("x", Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Muted)
                            Text("y", Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Muted)
                            Text("Mục tiêu", Modifier.weight(1.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Muted)
                        }
                        Divider()
                        res.vertices.forEach { v ->
                            val isBest = kotlin.math.abs(v.value - res.value) < 1e-6
                            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                                Text(
                                    v.label,
                                    Modifier.width(42.dp),
                                    fontSize = 12.sp,
                                    fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isBest) Navy else Muted
                                )
                                Text(
                                    fmtClean(v.x, 2),
                                    Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isBest) Navy else Ink
                                )
                                Text(
                                    fmtClean(v.y, 2),
                                    Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isBest) Navy else Ink
                                )
                                Text(
                                    fmt(v.value, 1),
                                    Modifier.weight(1.2f),
                                    fontSize = 12.sp,
                                    fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isBest) Navy else Ink
                                )
                            }
                        }
                    }

                    if (!res.integer && res.shadow.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        SectionCard("Giá đối ngẫu (shadow price)") {
                            res.shadow.forEachIndexed { i, lam ->
                                if (i < cons.size) {
                                    ResultRow(
                                        cons[i].name,
                                        if (lam == 0.0) "0 (đang dư)" else "+" + fmt(lam, 2),
                                        hint = if (lam != 0.0) "nới 1 đơn vị ràng buộc → thêm " + fmt(lam, 2) else null
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Caption(
                                "Shadow price = 0 nghĩa là đừng đầu tư thêm vào nguồn lực đó. " +
                                    "Đây là phần bị bỏ trống trong hầu hết bài toán LP."
                            )
                        }
                    }

                    val warns = buildList {
                        if (res.unbounded) add(
                            "Bài toán không bị chặn: thiếu ràng buộc phía trên cho một biến, " +
                                "lợi nhuận có thể tăng vô hạn."
                        )
                        if (res.integerSkipped) add(
                            "Miền quá lớn để duyệt nghiệm nguyên — đang hiển thị nghiệm thực. " +
                                "Hãy thu hẹp ràng buộc."
                        )
                        if (!res.integer && res.x > 0 && kotlin.math.abs(res.x - Math.round(res.x).toDouble()) > 1e-6) add(
                            "Nghiệm lẻ (" + fmtClean(res.x, 2) + "). Nếu đơn vị không chia được, " +
                                "bật “chỉ nhận nghiệm nguyên” (bẫy B1)."
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
                "Nghiệm tối ưu luôn nằm ở một đỉnh của miền khả thi. " +
                    "Vẽ miền ra giấy, đánh dấu từng đỉnh, rồi mới tin con số."
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}
