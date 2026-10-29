package vn.toiuu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ------------------------------------------------------------------ khung man hinh

@Composable
fun ScreenFrame(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxSize().background(Paper)) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Navy)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    TextButton(onClick = onBack) {
                        Text("←", fontSize = 20.sp, color = Color.White)
                    }
                } else {
                    Spacer(Modifier.width(6.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (subtitle != null) {
                        Text(subtitle, fontSize = 11.sp, color = Color(0xFFB9C7DA))
                    }
                }
            }
        }
        Column(Modifier.fillMaxSize()) { content() }
    }
}

// ------------------------------------------------------------------ nhap lieu

@Composable
fun NumField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun UnitPicker(unit: String, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Đơn vị tiền:", fontSize = 13.sp, color = Muted)
        Spacer(Modifier.width(8.dp))
        for ((code, label) in listOf("đ" to "đồng", "k" to "nghìn", "tr" to "triệu")) {
            val on = unit == code
            Box(
                Modifier
                    .padding(end = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (on) Navy else Color.Transparent)
                    .border(1.dp, if (on) Navy else Line, RoundedCornerShape(8.dp))
                    .clickable { onChange(code) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(label, fontSize = 12.sp, color = if (on) Color.White else Muted)
            }
        }
    }
}

// ------------------------------------------------------------------ ket qua

@Composable
fun SectionCard(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            if (title != null) {
                Text(
                    title.uppercase(),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
                Spacer(Modifier.height(8.dp))
            }
            content()
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, hero: Boolean = false, hint: String? = null) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, color = Muted)
            if (hint != null) Text(hint, fontSize = 10.5.sp, color = Muted)
        }
        Text(
            value,
            fontSize = if (hero) 21.sp else 14.sp,
            fontWeight = if (hero) FontWeight.Bold else FontWeight.SemiBold,
            color = if (hero) Navy else Ink
        )
    }
}

@Composable
fun WarnCard(text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AmberBg)
            .padding(11.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Amber),
            contentAlignment = Alignment.Center
        ) {
            Text("!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 12.5.sp, color = AmberInk)
    }
}

@Composable
fun NoteCard(text: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Sky)
            .padding(11.dp)
    ) {
        Text(text, fontSize = 12.5.sp, color = Navy)
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Navy)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
fun ToolCard(kicker: String, title: String, desc: String, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            Modifier.clickable { onClick() }.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Sky),
                contentAlignment = Alignment.Center
            ) {
                Text(kicker, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text(desc, fontSize = 12.sp, color = Muted)
            }
            Text("→", fontSize = 18.sp, color = Navy)
        }
    }
}

@Composable
fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
}

@Composable
fun Gap(h: Int = 10) {
    Spacer(Modifier.height(h.dp))
}

@Composable
fun BigTitle(text: String) {
    Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy)
}

@Composable
fun Body(text: String) {
    Text(text, fontSize = 13.5.sp, color = Ink, lineHeight = 19.sp)
}

@Composable
fun Caption(text: String) {
    Text(text, fontSize = 11.5.sp, color = Muted, lineHeight = 16.sp)
}

@Composable
fun RowScopeSpacer() {
    Spacer(Modifier.width(8.dp))
}
