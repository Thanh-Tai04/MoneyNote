package com.example.moneynote.ui

// THÊM 2 DÒNG NÀY:
import java.text.NumberFormat
import java.util.Locale
// ---
import java.util.Calendar
import java.util.Date

/**
 * Tệp tiện ích (utils) để xử lý logic ngày tháng
 */
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

// #### THÊM HÀM MỚI NÀY VÀO CUỐI TỆP ####
/**
 * Định dạng số (Double) thành chuỗi tiền tệ (ví dụ: "3.000.000đ")
 */
fun formatCurrency(amount: Double): String {
    val locale = Locale("vi", "VN")
    val format = NumberFormat.getCurrencyInstance(locale)
    // Thay thế "₫" bằng "đ" và loại bỏ phần ".00"
    return format.format(amount)
        .replace(Regex("\\.00|\\s?₫"), "đ")
        .replace(",", ".") // Đổi dấu phẩy thành dấu chấm
}