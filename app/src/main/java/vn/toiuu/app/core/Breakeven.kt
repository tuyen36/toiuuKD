package vn.toiuu.app.core

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Hoa von voi chi phi co the co phan bac hai.
 * pi(Q) = p*Q - (fc + vc*Q + qc*Q^2)
 * Nghiem cua pi(Q) = 0  <=>  qc*Q^2 + (vc - p)*Q + fc = 0
 */
data class BreakevenInput(
    val price: Double,
    val vc: Double,
    val qc: Double,
    val fc: Double,
    /** San luong hien tai de tinh DOL, bien an toan */
    val q: Double
)

data class BreakevenResult(
    val qLow: Double?,
    val qHigh: Double?,
    val price: Double,
    val q: Double,
    val profit: Double,
    val unitMargin: Double,
    val contribution: Double,
    val dol: Double?,
    val marginOfSafety: Double?,
    val minAvc: Double,
    val shutdownPrice: Double,
    val aboveShutdown: Boolean,
    val hasBreakeven: Boolean
)

object Breakeven {

    fun solve(i: BreakevenInput): BreakevenResult {
        val m = i.price - i.vc
        val disc = m * m - 4.0 * i.qc * i.fc

        var low: Double? = null
        var high: Double? = null

        if (abs(i.qc) < 1e-12) {
            if (m > 1e-12) {
                low = i.fc / m
            }
        } else if (disc >= 0.0) {
            val s = sqrt(disc)
            val r1 = (m - s) / (2.0 * i.qc)
            val r2 = (m + s) / (2.0 * i.qc)
            val a = min(r1, r2)
            val b = max(r1, r2)
            if (a >= 0.0) low = a
            if (b >= 0.0) high = b
        }

        val revenue = i.price * i.q
        val cost = i.fc + i.vc * i.q + i.qc * i.q * i.q
        val profit = revenue - cost
        val mc = i.vc + 2.0 * i.qc * i.q
        val contribution = (i.price - i.vc) * i.q

        val dol = if (abs(profit) > 1e-9) (i.price - mc) * i.q / profit else null
        val mos = if (low != null && low > 1e-12 && i.q > 1e-12) (i.q - low) / i.q else null

        // AVC(Q) = vc + qc*Q ; qc >= 0 -> cuc tieu tai Q = 0
        val minAvc = if (i.qc >= 0.0) i.vc else Double.NEGATIVE_INFINITY
        val shutdown = minAvc

        return BreakevenResult(
            qLow = low,
            qHigh = high,
            price = i.price,
            q = i.q,
            profit = profit,
            unitMargin = i.price - i.vc,
            contribution = contribution,
            dol = dol,
            marginOfSafety = mos,
            minAvc = minAvc,
            shutdownPrice = shutdown,
            aboveShutdown = i.price >= shutdown - 1e-9,
            hasBreakeven = low != null
        )
    }
}
