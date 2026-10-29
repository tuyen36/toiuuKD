package vn.toiuu.app.core

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

/** Ràng buộc: a*x + b*y <= c */
data class Constraint(
    val a: Double,
    val b: Double,
    val c: Double,
    val name: String
)

data class Vertex(
    val x: Double,
    val y: Double,
    val value: Double,
    val label: String
)

data class LpResult(
    val feasible: Boolean,
    val unbounded: Boolean,
    val integer: Boolean,
    val integerSkipped: Boolean,
    val x: Double,
    val y: Double,
    val value: Double,
    val vertices: List<Vertex>,
    val binding: List<Int>,
    val shadow: List<Double>
)

/**
 * Quy hoach tuyen tinh 2 bien, giai bang cach duyet dinh (dung tinh than
 * "5 dinh" trong sach): moi dinh la giao cua 2 duong, loc diem kha thi,
 * roi chon dinh tot nhat.
 */
object Lp {

    private const val EPS = 1e-7

    fun solve(
        c1: Double,
        c2: Double,
        cons: List<Constraint>,
        integer: Boolean = false
    ): LpResult {
        val lines = ArrayList<Constraint>()
        lines.addAll(cons)
        // Mien x, y >= 0 duoc bieu dien bang -x <= 0, -y <= 0
        lines.add(Constraint(-1.0, 0.0, 0.0, "x ≥ 0"))
        lines.add(Constraint(0.0, -1.0, 0.0, "y ≥ 0"))

        val raw = ArrayList<Pair<Double, Double>>()
        for (i in lines.indices) {
            for (j in i + 1 until lines.size) {
                val p = intersect(lines[i], lines[j]) ?: continue
                if (p.first < -EPS || p.second < -EPS) continue
                if (!feasible(p.first, p.second, cons)) continue
                raw.add(p)
            }
        }

        val pts = dedup(raw)
        if (pts.isEmpty()) {
            return LpResult(false, false, integer, false, 0.0, 0.0, 0.0, emptyList(), emptyList(), emptyList())
        }

        val maxX = cons.filter { it.a > EPS }.minOfOrNull { it.c / it.a }
        val maxY = cons.filter { it.b > EPS }.minOfOrNull { it.c / it.b }
        val unbounded = (c1 > EPS && maxX == null) || (c2 > EPS && maxY == null)

        val cx = pts.map { it.first }.average()
        val cy = pts.map { it.second }.average()
        var ordered = pts.sortedBy { atan2(it.second - cy, it.first - cx) }
        val start = ordered.indices.minByOrNull {
            ordered[it].first * ordered[it].first + ordered[it].second * ordered[it].second
        } ?: 0
        ordered = ordered.drop(start) + ordered.take(start)

        val labels = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
        val vertices = ordered.mapIndexed { idx, p ->
            Vertex(
                x = p.first,
                y = p.second,
                value = c1 * p.first + c2 * p.second,
                label = labels.getOrElse(idx) { "P$idx" }
            )
        }

        var best = vertices.maxByOrNull { it.value }!!
        var skipped = false

        if (integer) {
            val pt = bestInteger(c1, c2, cons)
            if (pt == null) {
                skipped = true
            } else if (pt.first != best.x || pt.second != best.y) {
                best = Vertex(pt.first, pt.second, c1 * pt.first + c2 * pt.second, "Z*")
            }
        }

        val binding = cons.indices.filter { i ->
            abs(cons[i].a * best.x + cons[i].b * best.y - cons[i].c) < 1e-6 * (1.0 + abs(cons[i].c))
        }

        val shadow = if (integer) {
            emptyList()
        } else {
            cons.indices.map { i ->
                if (i !in binding) {
                    0.0
                } else {
                    val delta = max(1e-3, abs(cons[i].c) * 1e-3)
                    val bumped = cons.toMutableList()
                    bumped[i] = cons[i].copy(c = cons[i].c + delta)
                    val v2 = bestValue(c1, c2, bumped)
                    if (v2 == null) 0.0 else (v2 - best.value) / delta
                }
            }
        }

        return LpResult(
            feasible = true,
            unbounded = unbounded,
            integer = integer,
            integerSkipped = skipped,
            x = best.x,
            y = best.y,
            value = best.value,
            vertices = vertices,
            binding = binding,
            shadow = shadow
        )
    }

    // ---------------------------------------------------------------- noi bo

    private fun intersect(l1: Constraint, l2: Constraint): Pair<Double, Double>? {
        val det = l1.a * l2.b - l2.a * l1.b
        if (abs(det) < 1e-12) return null
        val x = (l1.c * l2.b - l2.c * l1.b) / det
        val y = (l1.a * l2.c - l2.a * l1.c) / det
        return x to y
    }

    private fun feasible(x: Double, y: Double, cons: List<Constraint>): Boolean {
        if (x < -EPS || y < -EPS) return false
        for (c in cons) {
            if (c.a * x + c.b * y > c.c + 1e-6 * (1.0 + abs(c.c))) return false
        }
        return true
    }

    private fun dedup(pts: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
        val out = ArrayList<Pair<Double, Double>>()
        for (p in pts) {
            val dup = out.any { abs(it.first - p.first) < 1e-7 && abs(it.second - p.second) < 1e-7 }
            if (!dup) out.add(p)
        }
        return out
    }

    private fun bestValue(c1: Double, c2: Double, cons: List<Constraint>): Double? {
        val lines = ArrayList<Constraint>()
        lines.addAll(cons)
        lines.add(Constraint(-1.0, 0.0, 0.0, ""))
        lines.add(Constraint(0.0, -1.0, 0.0, ""))
        var best: Double? = null
        for (i in lines.indices) {
            for (j in i + 1 until lines.size) {
                val p = intersect(lines[i], lines[j]) ?: continue
                if (!feasible(p.first, p.second, cons)) continue
                val v = c1 * p.first + c2 * p.second
                if (best == null || v > best) best = v
            }
        }
        return best
    }

    private fun bestInteger(
        c1: Double,
        c2: Double,
        cons: List<Constraint>
    ): Pair<Double, Double>? {
        val maxX = cons.filter { it.a > EPS }.minOfOrNull { it.c / it.a } ?: return null
        val maxY = cons.filter { it.b > EPS }.minOfOrNull { it.c / it.b } ?: return null
        if (maxX < 0 || maxY < 0) return null
        if (maxX > 2000 || maxY > 2000 || maxX * maxY > 400_000) return null

        var bx = 0.0
        var by = 0.0
        var bv = Double.NEGATIVE_INFINITY
        var x = 0.0
        while (x <= maxX + EPS) {
            var y = 0.0
            while (y <= maxY + EPS) {
                if (feasible(x, y, cons)) {
                    val v = c1 * x + c2 * y
                    if (v > bv) {
                        bv = v
                        bx = x
                        by = y
                    }
                }
                y += 1.0
            }
            x += 1.0
        }
        return if (bv == Double.NEGATIVE_INFINITY) null else bx to by
    }
}
