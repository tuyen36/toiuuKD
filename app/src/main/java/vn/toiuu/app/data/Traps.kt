package vn.toiuu.app.data

/** 33 bay trong cam nang, giu nguyen thu tu va loi van cua sach. */
data class Trap(
    val id: String,
    val group: String,
    val title: String,
    val signal: String,
    val avoid: String
)

val ALL_TRAPS: List<Trap> = listOf(
    Trap("A1", "Mô hình & giả định", "Tối ưu lãi gộp", "\"Mỗi ly lãi 18k\"", "Trừ định phí trước khi kết luận"),
    Trap("A2", "Mô hình & giả định", "Coi giá là tham số", "Giá cho trước, không giải thích", "Đưa giá vào làm biến quyết định"),
    Trap("A3", "Mô hình & giả định", "Gộp ràng buộc", "\"Nguyên liệu và nhân công\"", "Tách mỗi ràng buộc một dòng"),
    Trap("A4", "Mô hình & giả định", "Bỏ qua chi phí cơ hội", "\"Tôi tự làm nên không mất gì\"", "Tính lương chủ, lãi vốn"),
    Trap("A5", "Mô hình & giả định", "Sai mục tiêu", "Tối đa doanh thu, tối đa số khách", "Viết hàm mục tiêu bằng lời trước"),
    Trap("A6", "Mô hình & giả định", "Tối ưu bộ phận", "Mỗi phòng tự tối ưu, cả chuỗi lỗ", "Tối ưu theo chuỗi giá trị"),
    Trap("A7", "Mô hình & giả định", "Bỏ qua ràng buộc ẩn", "Thời gian chủ, uy tín, quan hệ", "Liệt kê mọi nguồn lực khan hiếm"),
    Trap("A8", "Mô hình & giả định", "Áp dụng ngoài miền hợp lệ", "Ngoại suy tuyến tính quá xa", "Ghi rõ miền áp dụng của mô hình"),
    Trap("A9", "Mô hình & giả định", "Nhầm mô hình với thực tế", "Tin con số tuyệt đối", "Mô hình là bản đồ, không phải lãnh thổ"),
    Trap("A10", "Mô hình & giả định", "Bỏ qua phản ứng đối thủ", "Giả định giá đối thủ đứng yên", "Chơi thử ít nhất một vòng"),
    Trap("B1", "Toán & tính toán", "Làm tròn nghiệm LP", "\"19,6 người → 20\"", "Dùng ILP"),
    Trap("B2", "Toán & tính toán", "Dùng AC cho quyết định biên", "\"Dưới giá thành, không bán\"", "Dùng MC / AVC"),
    Trap("B3", "Toán & tính toán", "Dùng p thay MR", "p = MC khi có quyền lực thị trường", "Kiểm tra cấu trúc thị trường"),
    Trap("B4", "Toán & tính toán", "Quên kiểm tra biên", "Nghiệm nội điểm nhưng đang full công suất", "Kiểm tra x = 0 và x = max"),
    Trap("B5", "Toán & tính toán", "Tin solver \"optimal\"", "Bài toán không lồi", "Nhiều điểm khởi đầu, kiểm tra Hessian"),
    Trap("B6", "Toán & tính toán", "Bỏ qua ràng buộc không thắt", "Chỉ nhìn ràng buộc đang thắt", "Đọc cả λ = 0"),
    Trap("B7", "Toán & tính toán", "Nhầm dấu, hoán vị biến", "Shadow price âm ở bài max với ≤", "Kiểm tra chéo bằng đối ngẫu"),
    Trap("B8", "Toán & tính toán", "Chia cho gần 0, quên miền xác định", "Q → 0, chia 0, căn số âm", "Kiểm tra miền trước khi tính"),
    Trap("B9", "Toán & tính toán", "Quên điều kiện bậc hai", "Giải f′(x) = 0 rồi kết luận ngay", "Kiểm tra f″(x): âm là cực đại, dương là cực tiểu"),
    Trap("C1", "Dữ liệu & ước lượng", "Tương quan ≠ nhân quả", "\"Marketing tăng thì doanh thu tăng\"", "Thí nghiệm, biến kiểm soát"),
    Trap("C2", "Dữ liệu & ước lượng", "Quá ít dữ liệu", "5 điểm, \"hệ số rất đẹp\"", "Tối thiểu 30 quan sát"),
    Trap("C3", "Dữ liệu & ước lượng", "Bỏ qua nội sinh", "Giá và lượng quyết định cùng lúc", "Thí nghiệm ngẫu nhiên, biến công cụ"),
    Trap("C4", "Dữ liệu & ước lượng", "Số liệu tự khai, mẫu chệch", "Chỉ hỏi khách quen", "Mẫu ngẫu nhiên, hỏi cả khách rời bỏ"),
    Trap("C5", "Dữ liệu & ước lượng", "Sai số đo và làm tròn", "73,7 so với 73,6", "Giữ 3 chữ số có nghĩa, ghi sai số"),
    Trap("C6", "Dữ liệu & ước lượng", "Bỏ qua mùa vụ", "So tháng 2 với tháng 12", "So cùng kỳ năm trước"),
    Trap("C7", "Dữ liệu & ước lượng", "Ngoại suy quá xa", "\"Tăng 20%/năm trong 10 năm\"", "Kịch bản, giới hạn trên"),
    Trap("D1", "Quyết định & hành vi", "Bỏ qua thời gian", "Cộng trừ dòng tiền khác năm", "Chiết khấu về hiện tại"),
    Trap("D2", "Quyết định & hành vi", "Dùng IRR thay NPV", "\"IRR 30% nên chắc tốt\"", "NPV là tiêu chí quyết định"),
    Trap("D3", "Quyết định & hành vi", "Độ nhạy một chiều", "Mỗi biến đổi riêng lẻ", "Kịch bản hoặc Monte Carlo"),
    Trap("D4", "Quyết định & hành vi", "Monte Carlo lạc quan", "Phân phối toàn số đẹp", "Dữ liệu thật, có đuôi xấu"),
    Trap("D5", "Quyết định & hành vi", "Năng lực theo trung bình", "\"200 khách/ngày, 1 người đủ\"", "Tính theo giờ cao điểm"),
    Trap("D6", "Quyết định & hành vi", "Quên chi phí thoát", "\"Lỗ 4,5 triệu/tháng\"", "Cộng cọc, thanh lý, thời gian"),
    Trap("D7", "Quyết định & hành vi", "Một con số duy nhất", "\"Lãi 23,6 triệu/tháng\"", "Luôn có khoảng hoặc kịch bản"),
)

val TRAP_GROUPS: List<String> = ALL_TRAPS.map { it.group }.distinct()
