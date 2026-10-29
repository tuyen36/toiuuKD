package vn.toiuu.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test vang: 5 case trong cam nang, so lieu da kiem tra lai.
 * Sai mot con so la build do.
 */
class GoldenTest {

    private val eps = 1e-3

    // ---------------------------------------------------------------- CASE 1

    @Test
    fun case1_lp_traSua() {
        val cons = listOf(
            Constraint(1.0, 1.0, 100.0, "Tổng khách"),
            Constraint(1.0, 0.0, 80.0, "Công suất trà"),
            Constraint(0.0, 1.0, 70.0, "Công suất cà phê")
        )
        val r = Lp.solve(18.0, 17.0, cons)

        assertTrue(r.feasible)
        assertEquals(80.0, r.x, eps)
        assertEquals(20.0, r.y, eps)
        assertEquals(1780.0, r.value, 0.01)

        // Gia doi ngau: +17k / +1k / 0
        assertEquals(17.0, r.shadow[0], 0.05)
        assertEquals(1.0, r.shadow[1], 0.05)
        assertEquals(0.0, r.shadow[2], 0.05)
    }

    @Test
    fun case1_lp_themMayPha() {
        // 1,5x + y <= 120 them vao mo hinh goc (x + y <= 100 van con)
        val cons = listOf(
            Constraint(1.0, 1.0, 100.0, "Tổng khách"),
            Constraint(1.0, 0.0, 80.0, "Công suất trà"),
            Constraint(0.0, 1.0, 70.0, "Công suất cà phê"),
            Constraint(1.5, 1.0, 120.0, "Máy pha")
        )
        val r = Lp.solve(18.0, 17.0, cons)

        assertTrue(r.feasible)
        assertEquals(40.0, r.x, eps)
        assertEquals(60.0, r.y, eps)
        assertEquals(1740.0, r.value, 0.01)

        // Nghiem phai thoa MOI rang buoc, ke ca x + y <= 100
        assertTrue(r.x + r.y <= 100.0 + 1e-6)
    }

    @Test
    fun case1_lp_nguyen() {
        val cons = listOf(
            Constraint(1.0, 1.0, 100.0, "Tổng khách"),
            Constraint(1.0, 0.0, 80.0, "Công suất trà"),
            Constraint(0.0, 1.0, 70.0, "Công suất cà phê")
        )
        val r = Lp.solve(18.0, 17.0, cons, integer = true)
        assertEquals(0.0, r.x % 1.0, 1e-9)
        assertEquals(0.0, r.y % 1.0, 1e-9)
        assertEquals(1780.0, r.value, 0.01)
    }

    // ---------------------------------------------------------------- CASE 2

    @Test
    fun case2_giaTra() {
        val r = Pricing.solve(PricingInput(a = 157.5, b = 3.75, fc = 0.0, vc = 12.0, qc = 0.0))
        assertEquals(27.0, r.pStar, 0.01)
        assertEquals(56.25, r.qStar, 0.02)
        assertEquals(843.75, r.contribution, 0.5)
        assertEquals(843.75, r.profit, 0.5)
        assertTrue(r.secondOrder < 0.0)
    }

    @Test
    fun case2_hoaVon() {
        // Dinh phi 20 trieu/thang = 666,7k/ngay
        val r = Breakeven.solve(
            BreakevenInput(price = 27.0, vc = 12.0, qc = 0.0, fc = 666.7, q = 56.25)
        )
        assertNotNull(r.qLow)
        assertEquals(44.44, r.qLow!!, 0.1)
        assertEquals(177.05, r.profit, 1.0)
        assertTrue(r.hasBreakeven)
    }

    // ---------------------------------------------------------------- CASE 3

    @Test
    fun case3_banGo() {
        val r = Pricing.solve(PricingInput(a = 700.0, b = 1000.0, fc = 20.0, vc = 0.2, qc = 0.001))
        assertEquals(0.575, r.pStar, 0.001)
        assertEquals(125.0, r.qStar, 0.5)
        assertEquals(11.25, r.profit, 0.05)
        // MC tai Q* = 0,2 + 0,002*125 = 0,45
        assertEquals(0.45, r.mc, 0.001)
    }

    @Test
    fun case3_giaCoDinh_khongCoCau() {
        // Ban gia 500k, Q* = 150: chi minh hoa ham chi phi, khong dung bo giai gia
        val q = 150.0
        val revenue = 0.5 * q
        val cost = 20.0 + 0.2 * q + 0.001 * q * q
        assertEquals(75.0, revenue, 0.01)
        assertEquals(72.5, cost, 0.01)
        assertEquals(2.5, revenue - cost, 0.01)
    }

    // ---------------------------------------------------------------- dinh dang

    @Test
    fun docSoKieuViet() {
        assertEquals(1780.0, parseNum("1.780")!!, 1e-9)
        assertEquals(27.5, parseNum("27,5")!!, 1e-9)
        assertEquals(0.001, parseNum("0.001")!!, 1e-9)
        assertEquals(158.75, parseNum("158,75")!!, 1e-9)
    }

    @Test
    fun hienThiSoKieuViet() {
        assertEquals("1.780", fmt(1780.0, 0))
        assertEquals("56,25", fmt(56.25, 2))
        assertEquals("8,33", fmt(8.33, 2))
        assertEquals("27,5", fmtClean(27.50))
    }

    // ---------------------------------------------------------------- bay

    @Test
    fun baMuoiBaBay() {
        assertEquals(33, vn.toiuu.app.data.ALL_TRAPS.size)
        assertEquals(33, vn.toiuu.app.data.ALL_TRAPS.map { it.id }.distinct().size)
    }
}
