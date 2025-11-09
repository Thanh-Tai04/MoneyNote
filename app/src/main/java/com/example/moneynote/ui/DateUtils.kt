package com.example.moneynote.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
// THÊM CÁC IMPORT NÀY CHO VISUAL TRANSFORMATION
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Tệp này chứa các hàm tiện ích
 */

// Hàm tiện ích để thay đổi tháng
object DateUtils {

    /**
     * Lấy ngày đầu tiên của tháng từ một ngày bất kỳ
     */
    fun getMonthStartDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * Lấy ngày cuối cùng của tháng từ một ngày bất kỳ
     */
    fun getMonthEndDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    /**
     * Thay đổi tháng (tăng hoặc giảm)
     */
    fun changeMonth(date: Date, amount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, amount)
        return calendar.time
    }
}

// #### BẮT ĐẦU THÊM MỚI (LOGIC LỊCH) ####

/**
 * Lấy danh sách 7 tên ngày trong tuần (T2, T3, ...)
 * SỬA: Đặt firstDayOfWeek là MONDAY
 */
fun getWeekDayNames(locale: Locale = Locale("vi", "VN")): List<String> {
    val calendar = Calendar.getInstance(locale)
    // Đặt ngày đầu tuần là THỨ HAI
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val weekDays = (0..6).map {
        val dayName = SimpleDateFormat("E", locale).format(calendar.time)
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        // Viết hoa chữ cái đầu: "T2", "T3"...
        dayName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }
    return weekDays
}

/**
 * Tạo danh sách các ngày (Date) để hiển thị trên lưới lịch
 * SỬA: Tự động tính 5 hoặc 6 dòng (35 hoặc 42 ngày)
 */
fun generateCalendarDays(date: Date): List<Date> {

    // 1. Tìm ngày đầu tiên của lưới (Thứ Hai)
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.DAY_OF_MONTH, 1) // Bắt đầu từ ngày 1

    val firstDayOfWeek = Calendar.MONDAY
    var daysBeforeCount = 0 // Số ngày mờ của tháng trước

    // Lùi về ngày T2 đầu tiên của tuần
    while (calendar.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        daysBeforeCount++
        // Xử lý trường hợp đặc biệt (nếu Locale mặc định là Chủ Nhật)
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && firstDayOfWeek == Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -6)
            daysBeforeCount += 6
            break // Thoát vòng lặp
        }
    }

    // 2. Tính toán xem cần 5 hay 6 dòng
    val firstDayOfGrid = calendar.clone() as Calendar // Lưu lại ngày bắt đầu lưới

    // Lấy số ngày trong tháng
    val daysInMonth = Calendar.getInstance().apply { time = date }.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Tổng số ô cần = (Số ngày mờ) + (Số ngày trong tháng)
    val totalCellsNeeded = daysBeforeCount + daysInMonth

    // Nếu tổng số ô > 35 (5 dòng * 7 cột) thì ta cần 6 dòng (42 ô)
    val totalDaysInGrid = if (totalCellsNeeded > 35) 42 else 35

    // 3. Tạo danh sách ngày
    val days = (0 until totalDaysInGrid).map {
        val d = firstDayOfGrid.time
        firstDayOfGrid.add(Calendar.DAY_OF_MONTH, 1)
        d
    }
    return days
}


/**
 * Kiểm tra xem 2 đối tượng Date có phải là CÙNG MỘT NGÀY
 */
fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
// #### KẾT THÚC THÊM MỚI ####


// HÀM ĐỊNH DẠNG TIỀN TỆ (SỬ DỤNG DẤU CHẤM .)
fun formatCurrency(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
    symbols.groupingSeparator = '.' // Đặt dấu phân cách hàng nghìn là dấu chấm
    val formatter = DecimalFormat("#,###đ", symbols)
    return formatter.format(amount)
}

/**
 * Lớp này tự động thêm dấu chấm (.) phân cách hàng nghìn
 * khi người dùng nhập số tiền vào OutlinedTextField.
 */
class CurrencyVisualTransformation : VisualTransformation {

    // Định dạng số của Việt Nam (dùng dấu chấm)
    private val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
    private val formatter = DecimalFormat("#,###", symbols)

    override fun filter(text: AnnotatedString): TransformedText {
        // Lấy chuỗi số (ví dụ: "5000000")
        val originalText = text.text.trim()
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Chuyển "5000000" thành 5000000 (kiểu Long)
        val number = try {
            originalText.toLong()
        } catch (e: NumberFormatException) {
            // Xảy ra nếu số quá lớn
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Định dạng số đó thành "5.000.000"
        val formattedText = formatter.format(number)

        // Tạo OffsetMapping để giữ vị trí con trỏ (cursor) đúng
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val commas = formattedText.count { it == '.' }
                // Tính toán vị trí mới của con trỏ
                // Logic đơn giản:
                val originalCount = originalText.length
                val formattedCount = formattedText.length
                return offset + (formattedCount - originalCount)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Tính xem có bao nhiêu dấu chấm trước con trỏ
                val commasBefore = formattedText.substring(0, offset).count { it == '.' }
                return (offset - commasBefore).coerceAtLeast(0)
            }
        }

        return TransformedText(
            AnnotatedString(formattedText),
            offsetMapping
        )
    }
}