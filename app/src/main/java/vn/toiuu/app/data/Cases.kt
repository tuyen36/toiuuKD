package vn.toiuu.app.data

enum class ToolKind { PRICING, BREAKEVEN, LP }

data class PresetConstraint(
    val a: Double,
    val b: Double,
    val c: Double,
    val name: String
)

/**
 * 5 case lay tu cam nang. So lieu da duoc kiem tra lai trong ban 2.0,
 * va duoc dung lam "test vang" cho phan toan.
 */
data class Preset(
    val id: String,
    val title: String,
    val question: String,
    val answer: String,
    val lesson: String,
    val tool: ToolKind,
    val unit: String = "k",
    // pricing
    val a: Double = 0.0,
    val b: Double = 0.0,
    val fc: Double = 0.0,
    val vc: Double = 0.0,
    val qc: Double = 0.0,
    // breakeven
    val price: Double = 0.0,
    val q: Double = 0.0,
    // lp
    val c1: Double = 0.0,
    val c2: Double = 0.0,
    val cons: List<PresetConstraint> = emptyList()
)

val PRESETS: List<Preset> = listOf(
    Preset(
        id = "C1",
        title = "Quán trà sữa — quy hoạch 2 món",
        question = "100 khách/ngày, trà tối đa 80, cà phê tối đa 70. Bán bao nhiêu mỗi món?",
        answer = "80 trà + 20 cà phê = 1.780k/ngày",
        lesson = "Giá đối ngẫu: thêm 1 khách = +17k, thêm 1 ly trà = +1k. Nên kéo khách, đừng mua máy.",
        tool = ToolKind.LP,
        c1 = 18.0,
        c2 = 17.0,
        cons = listOf(
            PresetConstraint(1.0, 1.0, 100.0, "Tổng khách"),
            PresetConstraint(1.0, 0.0, 80.0, "Công suất trà"),
            PresetConstraint(0.0, 1.0, 70.0, "Công suất cà phê")
        )
    ),
    Preset(
        id = "C1b",
        title = "Quán trà sữa — thêm máy pha",
        question = "Cả hai dùng chung máy pha: 1,5 phút/trà, 1 phút/cà phê, 120 phút/ngày.",
        answer = "40 trà + 60 cà phê = 1.740k/ngày",
        lesson = "Máy pha làm phương án “bán tối đa trà” bất khả thi. Con số 1.780k trước đó " +
            "chưa tính công suất máy — thêm ràng buộc thật thì lợi nhuận giảm, không tăng.",
        tool = ToolKind.LP,
        c1 = 18.0,
        c2 = 17.0,
        cons = listOf(
            PresetConstraint(1.0, 1.0, 100.0, "Tổng khách"),
            PresetConstraint(1.0, 0.0, 80.0, "Công suất trà"),
            PresetConstraint(0.0, 1.0, 70.0, "Công suất cà phê"),
            PresetConstraint(1.5, 1.0, 120.0, "Máy pha")
        )
    ),
    Preset(
        id = "C2",
        title = "Quán 20 m² — giá ly trà",
        question = "Cầu Q = 157,5 − 3,75p, chi phí nguyên liệu 12k/ly. Bán giá bao nhiêu?",
        answer = "27k · 56,25 ly/ngày · lãi gộp 844k/ngày",
        lesson = "p* = (a + b·c)/(2b). Đây là giá tối ưu theo lãi gộp, chưa trừ định phí.",
        tool = ToolKind.PRICING,
        unit = "k",
        a = 157.5,
        b = 3.75,
        fc = 0.0,
        vc = 12.0,
        qc = 0.0
    ),
    Preset(
        id = "C2b",
        title = "Quán 20 m² — điểm hoà vốn",
        question = "Định phí 20 triệu/tháng (666,7k/ngày), giá 27k, biến phí 12k. Cần bán bao nhiêu?",
        answer = "44,4 ly/ngày (làm tròn 44)",
        lesson = "Hoà vốn quan trọng hơn lợi nhuận dự kiến: nó cho biết bạn có sống được tháng sau không.",
        tool = ToolKind.BREAKEVEN,
        unit = "k",
        price = 27.0,
        vc = 12.0,
        qc = 0.0,
        fc = 666.7,
        q = 56.25
    ),
    Preset(
        id = "C3",
        title = "Xưởng bàn gỗ — có đường cầu",
        question = "P = 0,7 − 0,001Q (triệu), C(Q) = 20 + 0,2Q + 0,001Q². Sản lượng nào tối ưu?",
        answer = "Q* = 125 · P* = 575k · lãi 11,25 triệu/tháng",
        lesson = "Cùng hàm chi phí, thêm đường cầu làm lợi nhuận gấp 4,5 lần so với bán giá cố định 500k.",
        tool = ToolKind.PRICING,
        unit = "tr",
        a = 700.0,
        b = 1000.0,
        fc = 20.0,
        vc = 0.2,
        qc = 0.001
    )
)
