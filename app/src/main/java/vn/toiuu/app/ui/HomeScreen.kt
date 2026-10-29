package vn.toiuu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.AppState
import vn.toiuu.app.Screen

@Composable
fun HomeScreen(s: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Navy)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                "TỐI ƯU HÓA",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF9DB2CE)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Quyết định kinh doanh",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
                lineHeight = 30.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Nhập vài con số — ra quyết định có cơ sở.\nKhông cần internet.",
                fontSize = 12.5.sp,
                color = androidx.compose.ui.graphics.Color(0xFFC9D6E6),
                lineHeight = 18.sp
            )
        }

        Column(Modifier.padding(14.dp)) {
            Text(
                "BẠN ĐANG QUYẾT ĐỊNH GÌ?",
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
                color = Muted
            )
            Spacer(Modifier.height(8.dp))

            ToolCard(
                "01", "Giá & sản lượng",
                "Bán bao nhiêu, giá bao nhiêu để lãi tối đa"
            ) { s.screen = Screen.PRICING }

            ToolCard(
                "02", "Điểm hoà vốn",
                "Cần bán bao nhiêu để không lỗ, đòn bẩy có mong manh"
            ) { s.screen = Screen.BREAKEVEN }

            ToolCard(
                "03", "Quy hoạch 2 biến",
                "Chia nguồn lực khan hiếm giữa hai sản phẩm"
            ) { s.screen = Screen.LP }

            Spacer(Modifier.height(16.dp))
            Text(
                "TRA CỨU",
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
                color = Muted
            )
            Spacer(Modifier.height(8.dp))

            ToolCard(
                "33", "33 bẫy thường gặp",
                "Tìm theo từ khoá, đúng thứ tự như trong sách"
            ) { s.screen = Screen.TRAPS }

            ToolCard(
                "05", "5 case mẫu",
                "Nạp sẵn số liệu, xem cách giải và đáp án"
            ) { s.screen = Screen.CASES }

            Spacer(Modifier.height(18.dp))
            NoteCard(
                "Ứng dụng chạy hoàn toàn offline và không thu thập dữ liệu. " +
                    "Kết quả là công cụ hỗ trợ tính toán, không phải tư vấn tài chính."
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Phiên bản 2.0 · dựng từ cẩm nang “Tối ưu hóa quyết định kinh doanh”",
                fontSize = 10.5.sp,
                color = Muted
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
