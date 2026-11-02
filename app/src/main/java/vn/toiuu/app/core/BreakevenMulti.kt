/**
 * Hàm tính điểm hòa vốn đa sản phẩm (n > 1).
 * Tổng Doanh thu: TR = Σ(p_i * q_i)
 * Tổng Chi phí: TC = FC + Σ(vc_i * q_i + qc_i * q_i^2)
 * Giải hệ phương trình Newton tại mỗi bước lặp: π(q) = TR(q) - TC(q) = 0
 */
object BreakevenMulti {

    data class Product(
        val name: String,
        val price: Double,      // Giá bán đơn vị
        val vc: Double,         // Chi phí biến đơn vị
        val qc: Double = 0.0    // Hệ số tăng nhanh
    )

    data class Params(
        val fc: Double,         // Định phí cố định
        val products: List<Product>,
        val totalQ: Double      // Tổng sản lượng mong muốn (không bắt buộc)
    )

    /** Trả về (qlow, qhigh, feasible) - điểm hòa vốn dưới và trên, hoặc null */
    fun solve(params: Params): BreakevenMultiResult? {
        val n = params.products.size
        if (n == 0) return null

        // Hàm tổng lợi nhuận π(q1..qn) = TR - TC
        fun profit(q: DoubleArray): Double {
            val qSum = q.sum()
            val tr = q.sumOfIndexed { i, qi -> params.products[i].price * qi }
            val tc = params.fc + q.sumOfIndexed { i, qi ->
                params.products[i].vc * qi + params.products[i].qc * qi * qi
            }
            return tr - tc
        }

        // HàmGradient vector (đạo hàm riêng theo từng q_i)
        fun gradient(q: DoubleArray): DoubleArray {
            val n = q.size
            val grad = DoubleArray(n)
            for (i in q.indices) {
                // ∂π/∂q_i = p_i - (vc_i + 2*qc_i*q_i)
                grad[i] = params.products[i].price - (params.products[i].vc + 2.0 * params.products[i].qc * q[i])
            }
            return grad
        }

        // Newton-Raphson lặp giữa các q_i
        val q = DoubleArray(n) { params.totalQ / n } // khởi đầu bằng phân phối đều
        val maxIter = 200
        val eps = 1e-6

        var iter = 0
        while (iter < maxIter) {
            val g = gradient(q)
            val normG = sqrt(g.sumOfSquares())
            if (normG < eps) break // đã hội tụ

            // Tìm bước đi alpha (line search đơn giản)
            val alpha = if (normG > 0) 0.1 / normG else 0.0
            for (i in q.indices) q[i] -= alpha * g[i]

            // Giới hạn q_i >= 0
            for (i in q.indices) if (q[i] < 0.0) q[i] = 0.0

            iter++
        }

        val totalQ = q.sum()
        val currentProfit = profit(q)

        // Tìm điểm hòa vốn bằng phương pháp dichotomy (bisection) nếu totalQ có thể điều chỉnh
        // Ở đây ta fix totalQ và kiểm tra xem profit tại phân phối q có dương hay không
        val hasSolution = currentProfit >= 0.0

        return if (hasSolution) {
            BreakevenMultiResult(
                feasible = true,
                products = params.products.indices.mapIndexed { i, idx ->
                    BreakevenMulti.ProductResult(
                        name = params.products[i].name,
                        qOpt = q[i],
                        profitAtOpt = params.products[i].price * q[i] - (params.products[i].vc * q[i] + params.products[i].qc * q[i] * q[i]),
                        unitMargin = params.products[i].price - params.products[i].vc
                    )
                },
                totalQ = totalQ,
                overallProfit = currentProfit,
                hasBreakeven = true
            )
        } else {
            BreakevenMultiResult(
                feasible = false,
                products = emptyList(),
                totalQ = totalQ,
                overallProfit = profit(DoubleArray(n) { 0.0 }),
                hasBreakeven = false
            )
        }
    }
}

/** Kết quả trả về của giải tích đa sản phẩm */
data class BreakevenMultiResult(
    val feasible: Boolean,
    val products: List<BreakevenMulti.ProductResult>,
    val totalQ: Double,
    val overallProfit: Double,
    val hasBreakeven: Boolean
)

/** Kết quả sản phẩm riêng lẻ */
data class ProductResult(
    val name: String,
    val qOpt: Double,
    val profitAtOpt: Double,
    val unitMargin: Double
)