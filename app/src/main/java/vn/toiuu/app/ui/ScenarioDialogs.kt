package vn.toiuu.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import vn.toiuu.app.data.SavedScenario
import vn.toiuu.app.data.ScenarioStorage
import vn.toiuu.app.data.ToolKind
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun shareAnalysisResult(context: Context, text: String, subject: String = "Kết quả phân tích Tối Ưu") {
    // 1. Copy to clipboard
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("ToiUuResult", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Đã sao chép kết quả vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()

    // 2. Open Android system Share sheet
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ kết quả qua:")
    context.startActivity(shareIntent)
}

@Composable
fun SaveScenarioDialog(
    tool: ToolKind,
    currentValues: Map<String, String>,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    val defaultTitle = remember {
        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        "Kịch bản " + sdf.format(Date())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Lưu kịch bản tính toán", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy)
        },
        text = {
            Column {
                Text(
                    "Đặt tên cho kịch bản để nạp lại số liệu bất cứ khi nào bạn cần:",
                    fontSize = 13.sp,
                    color = Muted
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text(defaultTitle, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTitle = title.trim().ifEmpty { defaultTitle }
                    val item = SavedScenario(
                        id = System.currentTimeMillis().toString(),
                        title = finalTitle,
                        tool = tool,
                        timestamp = System.currentTimeMillis(),
                        values = currentValues
                    )
                    ScenarioStorage.save(context, item)
                    Toast.makeText(context, "Đã lưu kịch bản thành công!", Toast.LENGTH_SHORT).show()
                    onSaved()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) {
                Text("Lưu lại", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Muted)
            }
        }
    )
}

@Composable
fun LoadScenarioDialog(
    tool: ToolKind,
    onDismiss: () -> Unit,
    onSelect: (SavedScenario) -> Unit
) {
    val context = LocalContext.current
    var list by remember { mutableStateOf(ScenarioStorage.getByTool(context, tool)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Kịch bản đã lưu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy)
        },
        text = {
            if (list.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Bạn chưa có kịch bản nào được lưu cho công cụ này.",
                        fontSize = 13.sp,
                        color = Muted
                    )
                }
            } else {
                LazyColumn(Modifier.fillMaxWidth().height(280.dp)) {
                    items(list, key = { it.id }) { item ->
                        val dateStr = remember(item.timestamp) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.format(Date(item.timestamp))
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF4F6F9))
                                .clickable {
                                    onSelect(item)
                                    onDismiss()
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(item.title, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                                Text(dateStr, fontSize = 11.sp, color = Muted)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Nạp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Sky)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "✕",
                                fontSize = 12.sp,
                                color = Red,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        ScenarioStorage.delete(context, item.id)
                                        list = ScenarioStorage.getByTool(context, tool)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = Navy)
            }
        }
    )
}
