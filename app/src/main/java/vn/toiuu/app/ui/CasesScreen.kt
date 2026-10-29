package vn.toiuu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.AppState
import vn.toiuu.app.Screen
import vn.toiuu.app.data.PRESETS
import vn.toiuu.app.data.Preset

@Composable
fun CasesScreen(s: AppState) {
    ScreenFrame(
        "5 case mẫu",
        "Số liệu thật, đáp án đã kiểm tra",
        onBack = { s.screen = Screen.HOME }
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp)
        ) {
            item {
                NoteCard(
                    "Mỗi case nạp sẵn số vào máy tính. Hãy tự bấm trước, " +
                        "rồi so với đáp án — đó là cách học nhanh nhất."
                )
                Spacer(Modifier.height(10.dp))
            }
            items(PRESETS, key = { it.id }) { preset ->
                CaseCard(preset) { s.applyPreset(preset) }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun CaseCard(p: Preset, onOpen: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row {
                Text(
                    p.id,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy,
                    modifier = Modifier
                        .background(Sky, RoundedCornerShape(5.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(p.title, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Ink)
            }
            Spacer(Modifier.height(7.dp))
            Text(p.question, fontSize = 12.5.sp, color = Muted, lineHeight = 18.sp)
            Spacer(Modifier.height(8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Paper, RoundedCornerShape(9.dp))
                    .padding(10.dp)
            ) {
                Text("ĐÁP ÁN", fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, color = Muted)
                Spacer(Modifier.height(3.dp))
                Text(p.answer, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy)
            }
            Spacer(Modifier.height(8.dp))
            Text(p.lesson, fontSize = 11.5.sp, color = Muted, lineHeight = 16.sp)
            Spacer(Modifier.height(10.dp))
            PrimaryButton("Mở máy tính với số này", onOpen)
        }
    }
}
