package vn.toiuu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.toiuu.app.AppState
import vn.toiuu.app.Screen
import vn.toiuu.app.data.ALL_TRAPS
import vn.toiuu.app.data.TRAP_GROUPS
import vn.toiuu.app.data.Trap

@Composable
fun TrapsScreen(s: AppState) {
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            ALL_TRAPS
        } else {
            ALL_TRAPS.filter { t ->
                t.id.lowercase().contains(q) ||
                    t.title.lowercase().contains(q) ||
                    t.signal.lowercase().contains(q) ||
                    t.avoid.lowercase().contains(q) ||
                    t.group.lowercase().contains(q)
            }
        }
    }

    ScreenFrame(
        "33 bẫy thường gặp",
        filtered.size.toString() + " / " + ALL_TRAPS.size + " bẫy",
        onBack = { s.screen = Screen.HOME }
    ) {
        Column(Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Tìm: hoà vốn, chi phí chìm, làm tròn…", fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )

            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)
            ) {
                for (group in TRAP_GROUPS) {
                    val list = filtered.filter { it.group == group }
                    if (list.isEmpty()) continue
                    item(key = "header-" + group) {
                        Text(
                            group.uppercase(),
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = Muted,
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                        )
                    }
                    items(list, key = { it.id }) { trap ->
                        TrapItem(trap)
                    }
                }
                if (filtered.isEmpty()) {
                    item {
                        Spacer(Modifier.height(20.dp))
                        NoteCard("Không tìm thấy bẫy nào khớp từ khoá. Thử từ ngắn hơn, ví dụ “giá”.")
                    }
                }
            }
        }
    }
}

@Composable
private fun TrapItem(t: Trap) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Sky)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(t.id, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Navy)
                }
                Spacer(Modifier.width(8.dp))
                Text(t.title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Ink)
            }
            Spacer(Modifier.height(5.dp))
            Row {
                Text("Dấu hiệu", fontSize = 10.5.sp, color = Muted, modifier = Modifier.width(62.dp))
                Text(t.signal, fontSize = 12.sp, color = Ink, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(3.dp))
            Row {
                Text("Cách tránh", fontSize = 10.5.sp, color = Muted, modifier = Modifier.width(62.dp))
                Text(t.avoid, fontSize = 12.sp, color = Navy, modifier = Modifier.weight(1f))
            }
        }
    }
}
