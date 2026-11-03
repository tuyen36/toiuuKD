package vn.toiuu.app.core

import kotlin.math.sqrt

object BreakevenMulti {

    data class Product(
        val name: String,
        val price: Double,
        val vc: Double,
        val qc: Double = 0.0
    )

    data class Params(
        val fc: Double,
        val products: List<Product>,
        val totalQ: Double
    )

    fun solve(params: Params): BreakevenMultiResult? {
        val n = params.products.size
        if (n == 0) return null

        fun computeProfit(q: DoubleArray): Double {
            var tr = 0.0
            var tc = params.fc
            for (i in 0 until n) {
                val qi = q[i]
                val prod = params.products[i]
                tr += prod.price * qi
                tc += prod.vc * qi + prod.qc * qi * qi
            }
            return tr - tc
        }

        fun computeGradient(q: DoubleArray): DoubleArray {
            val grad = DoubleArray(n)
            for (i in 0 until n) {
                val prod = params.products[i]
                grad[i] = prod.price - (prod.vc + 2.0 * prod.qc * q[i])
            }
            return grad
        }

        val q = DoubleArray(n) { params.totalQ / n.toDouble() }
        val maxIter = 200
        val eps = 1e-6

        var iter = 0
        while (iter < maxIter) {
            val g = computeGradient(q)
            val normG = g.sumOf { it * it }.let { sqrt(it) }
            if (normG < eps) break
            val alpha = if (normG > 0) 0.1 / normG else 0.0
            for (i in q.indices) {
                q[i] -= alpha * g[i]
                if (q[i] < 0.0) q[i] = 0.0
            }
            iter++
        }

        val totalQ = q.sum()
        val currentProfit = computeProfit(q)
        val hasSolution = currentProfit >= 0.0

        return if (hasSolution) {
            val productResults = mutableListOf<ProductResultItem>()
            for (i in 0 until n) {
                val prod = params.products[i]
                val qi = q[i]
                val profitItem = prod.price * qi - (prod.vc * qi + prod.qc * qi * qi)
                productResults.add(
                    ProductResultItem(
                        name = prod.name,
                        qOpt = qi,
                        profitAtOpt = profitItem,
                        unitMargin = prod.price - prod.vc
                    )
                )
            }
            BreakevenMultiResult(
                feasible = true,
                products = productResults,
                totalQ = totalQ,
                overallProfit = currentProfit,
                hasBreakeven = true
            )
        } else {
            BreakevenMultiResult(
                feasible = false,
                products = emptyList(),
                totalQ = totalQ,
                overallProfit = computeProfit(DoubleArray(n) { 0.0 }),
                hasBreakeven = false
            )
        }
    }
}

data class ProductResultItem(
    val name: String,
    val qOpt: Double,
    val profitAtOpt: Double,
    val unitMargin: Double
)

data class BreakevenMultiResult(
    val feasible: Boolean,
    val products: List<ProductResultItem>,
    val totalQ: Double,
    val overallProfit: Double,
    val hasBreakeven: Boolean
)
