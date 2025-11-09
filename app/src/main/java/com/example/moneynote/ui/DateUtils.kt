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
 * (Chúng ta sẽ để hàm formatCurrency ở đây)
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

// HÀM ĐỊNH DẠNG TIỀN TỆ (SỬ DỤNG DẤU CHẤM .)
fun formatCurrency(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
    symbols.groupingSeparator = '.' // Đặt dấu phân cách hàng nghìn là dấu chấm
    val formatter = DecimalFormat("#,###đ", symbols)
    return formatter.format(amount)
}

// #### BẮT ĐẦU SỬA LỖI - THÊM LỚP MỚI ####

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
                // Tính xem có bao nhiêu dấu chấm đã được thêm vào
                val commas = formattedText.count { it == '.' }
                return offset + commas
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Tính xem có bao nhiêu dấu chấm trước con trỏ
                val commasBefore = formattedText.substring(0, offset).count { it == '.' }
                return offset - commasBefore
            }
        }

        return TransformedText(
            AnnotatedString(formattedText),
            offsetMapping
        )
    }
}
// #### KẾT THÚC SỬA LỖI ####