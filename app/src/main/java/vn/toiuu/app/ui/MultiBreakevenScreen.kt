package vn.toiuu.app.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.AppState
import vn.toiuu.app.Screen
import vn.toiuu.app.core.BreakevenMulti
import vn.toiuu.app.core.BreakevenMulti.Params
import vn.toiuu.app.core.BreakevenMulti.Product
import vn.toiuu.app.core.fmtClean

@Composable
fun MultiBreakevenScreen(s: AppState) {
    val totalQ by remember { mutableStateOf(100.0) }

    val products = remember {
        listOf(
            Product("Trà sữa", 30.0, 15.0, 0.0),
            Product("Cà phê", 25.0, 10.0, 0.0),
            Product("Bánh ngọt", 35.0, 20.0, 0.0)
        )
    }

    val result = remember(products, totalQ) {
        BreakevenMulti.solve(Params(fc = 1000.0, products = products, totalQ = totalQ))
    }

    ScreenFrame(
        "Quy hoạch đa sản phẩm",
        "Chọn lượng bán tổng theo túi tiền bạn có",
        onBack = { s.screen = Screen.HOME }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            SectionCard("Thiết lập (Ví dụ demo)") {
                Text(
                    "Dữ liệu mẫu cho 3 sản phẩm. Bạn có thể chỉnh sửa lại sau.",
                    fontSize = 12.sp,
                    color = Muted
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    Text("Tổng sản lượng Q:", fontSize = 12.5.sp, color = Muted)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        totalQ.toInt().toString(),
                        fontSize = 13.sp,
                        color = Navy,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Sky)
                            .clickable { s.screen = Screen.HOME }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            result?.let {
                if (it.feasible) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F8FF))
                            .padding(10.dp)
                    ) {
                        Text(
                            "✔️ Tồn tại điểm hòa vốn tối ưu cho các sản phẩm này",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Green
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tổng lợi nhuận: ${fmtClean(it.overallProfit, 0)} đ · Tổng Q: ${fmtClean(it.totalQ, 0)}",
                            fontSize = 11.5.sp,
                            color = Muted
                        )
                    }
                } else {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RedBg)
                            .padding(10.dp)
                    ) {
                        Text(
                            "❌ Chưa đủ dữ liệu / điểm hòa vốn cho các sản phẩm này",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Red
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Hãy điều chỉnh tổng sản lượng hoặc các tham số của sản phẩm.",
                            fontSize = 11.5.sp,
                            color = Muted
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            // MultiBreakevenChart(result = result, products = products)

            Spacer(Modifier.height(14.dp))
        }
    }
}
