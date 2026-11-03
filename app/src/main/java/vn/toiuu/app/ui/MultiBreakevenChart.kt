package vn.toiuu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import vn.toiuu.app.core.BreakevenMultiResult
import vn.toiuu.app.core.fmtClean

@Composable
fun MultiBreakevenChart(
    result: BreakevenMultiResult?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .height(160.dp)
            .padding(12.dp)
    ) {
        Text(
            "TỔNG HỢP ĐA SẢN PHẨM",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D)
        )
        Spacer(Modifier.height(8.dp))
        if (result != null && result.feasible) {
            Text(
                "Tổng lợi nhuận: ${fmtClean(result.overallProfit, 0)} đ · Q: ${fmtClean(result.totalQ, 1)}",
                fontSize = 11.sp,
                color = Color(0xFF2E7D32)
            )
            Row {
                result.products.forEach { p ->
                    Box(
                        Modifier
                            .padding(end = 6.dp)
                            .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(4.dp))
                            .padding(4.dp, 2.dp)
                    ) {
                        Text(p.name, fontSize = 10.sp)
                    }
                }
            }
        } else {
            Text("Chưa đủ dữ liệu đa sản phẩm", fontSize = 11.sp, color = Color(0xFFC62828))
        }
    }
}
