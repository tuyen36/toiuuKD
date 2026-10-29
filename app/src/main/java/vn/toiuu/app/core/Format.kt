package vn.toiuu.app.core

import java.util.Locale
import kotlin.math.abs

/**
 * Doc so kieu Viet Nam: "1.780,5" -> 1780.5 ; "27,5" -> 27.5 ; "0.001" -> 0.001
 */
fun parseNum(text: String): Double? {
    var t = text.trim().replace("\u00A0", "").replace(" ", "")
    if (t.isEmpty()) return null
    t = when {
        t.contains(",") -> t.replace(".", "").replace(",", ".")
        t.count { it == '.' } > 1 -> t.replace(".", "")
        t.contains(".") -> {
            val parts = t.split(".")
            val intPart = parts[0]
            val frac = parts[1]
            if (frac.length == 3 && intPart.isNotEmpty() && intPart != "0") {
                t.replace(".", "")
            } else {
                t
            }
        }
        else -> t
    }
    return t.toDoubleOrNull()
}

/** Dinh dang so kieu Viet Nam: 1780.5 -> "1.780,50" */
fun fmt(x: Double, dec: Int = 2): String {
    if (x.isNaN()) return "—"
    if (x.isInfinite()) return if (x > 0) "+∞" else "−∞"
    val negative = x < -1e-12
    val v = abs(x)
    val raw = String.format(Locale.US, "%.${dec}f", v)
    val parts = raw.split(".")
    val intPart = parts[0]
    val sb = StringBuilder()
    for ((idx, ch) in intPart.withIndex()) {
        if (idx > 0 && (intPart.length - idx) % 3 == 0) sb.append('.')
        sb.append(ch)
    }
    var out = sb.toString()
    if (parts.size > 1) out += "," + parts[1]
    return (if (negative) "−" else "") + out
}

/** Tu chon so le theo do lon: 1780 -> "1.780", 8.33 -> "8,33" */
fun fmtSmart(x: Double): String {
    val ax = abs(x)
    val dec = when {
        ax >= 1000 -> 0
        ax >= 100 -> 1
        ax >= 1 -> 2
        ax >= 0.01 -> 3
        else -> 4
    }
    return fmt(x, dec)
}

/** Bo so 0 thua o cuoi: 27,50 -> 27,5 */
fun fmtClean(x: Double, dec: Int = 2): String {
    var s = fmt(x, dec)
    if (s.contains(",")) {
        s = s.trimEnd('0').trimEnd(',')
    }
    return s
}

fun unitLabel(unit: String): String = when (unit) {
    "tr" -> "triệu"
    "k" -> "nghìn"
    else -> "đồng"
}

/** "27 k", "0,575 tr" */
fun money(x: Double, unit: String): String = fmtSmart(x) + " " + unit
