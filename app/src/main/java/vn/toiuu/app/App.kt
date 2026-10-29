package vn.toiuu.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import vn.toiuu.app.core.fmtClean
import vn.toiuu.app.data.Preset
import vn.toiuu.app.data.ToolKind
import vn.toiuu.app.ui.BreakevenScreen
import vn.toiuu.app.ui.CasesScreen
import vn.toiuu.app.ui.HomeScreen
import vn.toiuu.app.ui.LpScreen
import vn.toiuu.app.ui.PricingScreen
import vn.toiuu.app.ui.TrapsScreen

enum class Screen { HOME, PRICING, BREAKEVEN, LP, TRAPS, CASES }

data class LpForm(
    val a: String,
    val b: String,
    val c: String,
    val name: String
)

class AppState {

    var screen by mutableStateOf(Screen.HOME)

    // --- dinh gia
    var priceA by mutableStateOf("157,5")
    var priceB by mutableStateOf("3,75")
    var priceFc by mutableStateOf("0")
    var priceVc by mutableStateOf("12")
    var priceQc by mutableStateOf("0")
    var priceUnit by mutableStateOf("k")

    // --- hoa von
    var bePrice by mutableStateOf("27")
    var beVc by mutableStateOf("12")
    var beQc by mutableStateOf("0")
    var beFc by mutableStateOf("666,7")
    var beQ by mutableStateOf("56,25")
    var beUnit by mutableStateOf("k")

    // --- quy hoach
    var lpC1 by mutableStateOf("18")
    var lpC2 by mutableStateOf("17")
    var lpInteger by mutableStateOf(false)
    var lpCons by mutableStateOf(
        listOf(
            LpForm("1", "1", "100", "Tổng khách"),
            LpForm("1", "0", "80", "Công suất trà"),
            LpForm("0", "1", "70", "Công suất cà phê")
        )
    )

    fun applyPreset(p: Preset) {
        when (p.tool) {
            ToolKind.PRICING -> {
                priceA = fmtClean(p.a)
                priceB = fmtClean(p.b)
                priceFc = fmtClean(p.fc)
                priceVc = fmtClean(p.vc)
                priceQc = fmtClean(p.qc)
                priceUnit = p.unit
                screen = Screen.PRICING
            }

            ToolKind.BREAKEVEN -> {
                bePrice = fmtClean(p.price)
                beVc = fmtClean(p.vc)
                beQc = fmtClean(p.qc)
                beFc = fmtClean(p.fc)
                beQ = fmtClean(p.q)
                beUnit = p.unit
                screen = Screen.BREAKEVEN
            }

            ToolKind.LP -> {
                lpC1 = fmtClean(p.c1)
                lpC2 = fmtClean(p.c2)
                lpInteger = false
                lpCons = if (p.cons.isEmpty()) {
                    listOf(LpForm("1", "1", "100", "Ràng buộc 1"))
                } else {
                    p.cons.map { LpForm(fmtClean(it.a), fmtClean(it.b), fmtClean(it.c), it.name) }
                }
                screen = Screen.LP
            }
        }
    }
}

@Composable
fun App() {
    val state = remember { AppState() }

    BackHandler(enabled = state.screen != Screen.HOME) {
        state.screen = Screen.HOME
    }

    when (state.screen) {
        Screen.HOME -> HomeScreen(state)
        Screen.PRICING -> PricingScreen(state)
        Screen.BREAKEVEN -> BreakevenScreen(state)
        Screen.LP -> LpScreen(state)
        Screen.TRAPS -> TrapsScreen(state)
        Screen.CASES -> CasesScreen(state)
    }
}
