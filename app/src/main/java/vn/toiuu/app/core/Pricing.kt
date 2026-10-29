package vn.toiuu.app.core

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Cau tuyen tinh: Q = a - b*p  (b > 0)
 * Chi phi:        C(Q) = fc + vc*Q + qc*Q^2
 *
 * Toi uu theo bien quyet dinh la GIA (khong phai Q), vi do moi la thu
 * doanh nghiep thuc su chon. Q* suy ra tu ham cau.
 */
data class PricingInput(
    val a: Double,
    val b: Double,
    val fc: Double,
    val vc: Double,
    val qc: Double
)

data class PricingResult(
    val pStar: Double,
    val qStar: Double,
    val revenue: Double,
    val cost: Double,
    val profit: Double,
    val contribution: Double,
    val mc: Double,
    val mr: Double,
    /** |epsilon| tai diem toi uu */
    val elasticity: Double,
    /** (p - MC) / p */
    val markup: Double,
    /** Nghiem nam sat bien (mo hinh co the sai) */
    val boundary: Boolean,
    /** Dao ham bac hai cua loi nhuan theo gia; < 0 moi la cuc dai */
    val secondOrder: Double
)

object Pricing {

    fun demand(a: Double, b: Double, p: Double): Double = max(0.0, a - b * p)

    fun costAt(i: PricingInput, q: Double): Double =
        i.fc + i.vc * q + i.qc * q * q

    fun mcAt(i: PricingInput, q: Double): Double = i.vc + 2.0 * i.qc * q

    fun profitAt(i: PricingInput, p: Double): Double {
        val q = demand(i.a, i.b, p)
        return p * q - costAt(i, q)
    }

    fun solve(i: PricingInput): PricingResult {
        val pMax = if (i.b > 1e-12) i.a / i.b else 0.0
        require(pMax > 0.0) { "Giá chặn (a/b) phải dương. Kiểm tra lại a và b." }

        val best = argmax({ p -> profitAt(i, p) }, 0.0, pMax)
        val p = best.first
        val q = demand(i.a, i.b, p)
        val mc = mcAt(i, q)
        val mr = if (i.b > 1e-12) (i.a - 2.0 * q) / i.b else 0.0
        val revenue = p * q
        val cost = costAt(i, q)
        val eps = if (q > 1e-9) -i.b * p / q else 0.0

        // Dao ham bac hai so: pi(p+h) - 2pi(p) + pi(p-h) / h^2
        val h = max(1e-6, p * 1e-4)
        val pPlus = p + h
        val pMinus = max(0.0, p - h)
        val second = (profitAt(i, pPlus) - 2.0 * profitAt(i, p) + profitAt(i, pMinus)) /
            (h * h).coerceAtLeast(1e-18)

        return PricingResult(
            pStar = p,
            qStar = q,
            revenue = revenue,
            cost = cost,
            profit = revenue - cost,
            contribution = (p - i.vc) * q,
            mc = mc,
            mr = mr,
            elasticity = abs(eps),
            markup = if (p > 1e-12) (p - mc) / p else 0.0,
            boundary = p <= pMax * 0.002 || p >= pMax * 0.998,
            secondOrder = second
        )
    }
}

/**
 * Cuc dai 1 chieu: quet luoi de tim khoang chua dinh, roi golden-section.
 * Quet luoi lam ham muc tieu khong can don dieu - an toan hon Newton.
 */
fun argmax(f: (Double) -> Double, lo: Double, hi: Double, steps: Int = 400): Pair<Double, Double> {
    if (hi <= lo) return lo to f(lo)
    var bestX = lo
    var bestY = f(lo)
    for (k in 1..steps) {
        val x = lo + (hi - lo) * k / steps
        val y = f(x)
        if (y > bestY) {
            bestY = y
            bestX = x
        }
    }
    var left = max(lo, bestX - (hi - lo) / steps)
    var right = min(hi, bestX + (hi - lo) / steps)
    val phi = 0.6180339887498949
    repeat(120) {
        val x1 = right - phi * (right - left)
        val x2 = left + phi * (right - left)
        if (f(x1) < f(x2)) left = x1 else right = x2
    }
    val x = (left + right) / 2.0
    return x to f(x)
}
