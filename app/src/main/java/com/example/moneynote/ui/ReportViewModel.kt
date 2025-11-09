package com.example.moneynote.ui // <-- ĐÃ SỬA

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// THÊM CÁC IMPORT NÀY:
import com.example.moneynote.data.Account
import com.example.moneynote.data.MoneyNoteRepository
import com.example.moneynote.data.Transaction
// ---
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import kotlinx.coroutines.flow.SharingStarted // <-- Import bạn bị thiếu nằm ở đây

// Dữ liệu cho biểu đồ
data class ChartData(
    val category: String,
    val amount: Double,
    val percentage: Float
)

// Trạng thái của màn hình Báo cáo
data class ReportUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val expenseChartData: List<ChartData> = emptyList(),
    val incomeChartData: List<ChartData> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModel(private val repository: MoneyNoteRepository) : ViewModel() {

    // Trạng thái cho tháng/năm đang chọn
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate

    // Trạng thái cho tài khoản đang chọn (0L = Tất cả tài khoản)
    private val _selectedAccountId = MutableStateFlow(0L)
    val selectedAccountId: StateFlow<Long> = _selectedAccountId

    // Danh sách tất cả tài khoản
    val allAccounts: StateFlow<List<Account>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // StateFlow này sẽ tự động cập nhật khi _selectedDate hoặc _selectedAccountId thay đổi
    val reportState: StateFlow<ReportUiState> =
        combine(_selectedDate, _selectedAccountId) { date, accountId ->
            Pair(date, accountId)
        }.flatMapLatest { (date, accountId) ->
            val startDate = DateUtils.getMonthStartDate(date)
            val endDate = DateUtils.getMonthEndDate(date)

            // SỬ DỤNG HÀM ĐÚNG TỪ REPOSITORY
            val transactionsFlow = if (accountId == 0L) {
                repository.getTransactionsBetweenDates(startDate, endDate)
            } else {
                repository.getTransactionsBetweenDates(startDate, endDate, accountId)
            }

            // Tính toán dữ liệu báo cáo từ flow giao dịch
            transactionsFlow.map { transactions ->
                calculateReportState(transactions)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, ReportUiState())

    // Hàm tính toán logic
    private fun calculateReportState(transactions: List<Transaction>): ReportUiState {
        val incomeTransactions = transactions.filter { it.type == "income" }
        val expenseTransactions = transactions.filter { it.type == "expense" }

        val totalIncome = incomeTransactions.sumOf { it.amount }
        val totalExpense = expenseTransactions.sumOf { it.amount }

        val expenseChartData = calculateChartData(expenseTransactions, totalExpense)
        val incomeChartData = calculateChartData(incomeTransactions, totalIncome)

        return ReportUiState(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            expenseChartData = expenseChartData,
            incomeChartData = incomeChartData
        )
    }

    private fun calculateChartData(transactions: List<Transaction>, total: Double): List<ChartData> {
        if (total == 0.0) return emptyList()

        return transactions
            .groupBy { it.category }
            .map { (category, list) ->
                val categoryAmount = list.sumOf { it.amount }
                ChartData(
                    category = category,
                    amount = categoryAmount,
                    percentage = (categoryAmount / total).toFloat()
                )
            }
            .sortedByDescending { it.amount }
    }


    // Hàm để thay đổi tháng
    fun changeMonth(amount: Int) {
        _selectedDate.value = DateUtils.changeMonth(_selectedDate.value, amount)
    }

    // Hàm để chọn tài khoản
    fun selectAccount(accountId: Long) {
        _selectedAccountId.value = accountId
    }
}

// Factory để tạo ReportViewModel
class ReportViewModelFactory(private val repository: MoneyNoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}