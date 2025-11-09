package com.example.moneynote.ui

// XÓA CÁC IMPORT CŨ
// import java.text.NumberFormat
// import java.util.Locale
// THÊM IMPORT MỚI
import java.text.DecimalFormat
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

// #### BẮT ĐẦU SỬA LỖI ĐỊNH DẠNG TIỀN TỆ ####
/**
 * Định dạng số (Double) thành chuỗi tiền tệ
 * (Ví dụ: 5000000.0 -> "5.000.000đ")
 */
fun formatCurrency(amount: Double): String {
    // Tạo một định dạng số
    // #,##0 đảm bảo sử dụng dấu phân cách (ví dụ: 5.000.000)
    // 'đ' là ký tự đơn vị tiền tệ ở cuối
    val formatter = DecimalFormat("#,##0'đ'")

    // Lấy các ký hiệu định dạng (dấu phẩy, dấu chấm)
    val symbols = formatter.decimalFormatSymbols.apply {
        // Đặt ký tự phân cách hàng nghìn là dấu CHẤM (.)
        groupingSeparator = '.'
    }
    formatter.decimalFormatSymbols = symbols

    // Trả về chuỗi đã định dạng
    return formatter.format(amount)
}
// #### KẾT THÚC SỬA LỖI ####