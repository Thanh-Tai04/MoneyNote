package com.example.moneynote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneynote.data.Account
import com.example.moneynote.data.Budget
import com.example.moneynote.data.MoneyNoteRepository
import com.example.moneynote.data.Transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Dữ liệu cho một mục ngân sách trên UI
data class BudgetItem(
    val category: String,
    val limitAmount: Double,
    val spentAmount: Double,
    val progress: Float // (0.0f -> 1.0f+)
)

// Trạng thái tổng quan của màn hình Ngân sách
data class BudgetUiState(
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val budgetItems: List<BudgetItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModel(private val repository: MoneyNoteRepository) : ViewModel() {

    // Trạng thái cho tháng/năm đang chọn
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate

    // Lấy chuỗi "YYYY-MM" (ví dụ "2025-11") từ _selectedDate
    // Đây là "khóa" để lưu và truy vấn ngân sách
    private val selectedMonthYear: StateFlow<String> = _selectedDate.map {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    // Lấy các giao dịch CHI TIÊU của tháng
    private val transactionsForMonth: StateFlow<List<Transaction>> = _selectedDate.flatMapLatest { date ->
        val startDate = DateUtils.getMonthStartDate(date)
        val endDate = DateUtils.getMonthEndDate(date)
        // Chỉ lấy giao dịch "expense"
        repository.getTransactionsBetweenDates(startDate, endDate)
            .map { list -> list.filter { it.type == "expense" } }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Lấy danh sách ngân sách đã đặt cho tháng
    private val budgetsForMonth: StateFlow<List<Budget>> = selectedMonthYear.flatMapLatest { monthYear ->
        repository.getBudgetsForMonth(monthYear)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Trạng thái chính của UI
    // Kết hợp (combine) Giao dịch và Ngân sách để tạo ra UI
    val budgetState: StateFlow<BudgetUiState> =
        combine(transactionsForMonth, budgetsForMonth) { transactions, budgets ->
            calculateBudgetState(transactions, budgets)
        }.stateIn(viewModelScope, SharingStarted.Lazily, BudgetUiState())


    // Hàm tính toán logic
    private fun calculateBudgetState(transactions: List<Transaction>, budgets: List<Budget>): BudgetUiState {
        val totalSpent = transactions.sumOf { it.amount }
        val totalBudget = budgets.sumOf { it.limitAmount }

        // Nhóm các giao dịch đã chi theo danh mục
        val spentByCategory = transactions.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        // Tạo danh sách BudgetItem
        // Ưu tiên 1: Lấy từ ngân sách đã đặt
        val budgetItems = budgets.map { budget ->
            val spent = spentByCategory[budget.categoryName] ?: 0.0
            BudgetItem(
                category = budget.categoryName,
                limitAmount = budget.limitAmount,
                spentAmount = spent,
                progress = (spent / budget.limitAmount).toFloat()
            )
        }

        // (Tùy chọn) Thêm các mục đã chi nhưng chưa đặt ngân sách
        // ... (bạn có thể mở rộng sau)

        return BudgetUiState(
            totalBudget = totalBudget,
            totalSpent = totalSpent,
            budgetItems = budgetItems.sortedByDescending { it.progress }
        )
    }

    // Hàm để thay đổi tháng
    fun changeMonth(amount: Int) {
        _selectedDate.value = DateUtils.changeMonth(_selectedDate.value, amount)
    }

    // Hàm để cập nhật hoặc thêm mới một ngân sách
    fun setBudget(category: String, amount: Double) {
        viewModelScope.launch {
            val monthYear = selectedMonthYear.value // Lấy tháng hiện tại
            if (monthYear.isBlank()) return@launch

            val newBudget = Budget(
                monthYear = monthYear,
                categoryName = category,
                limitAmount = amount
            )
            repository.insertOrUpdateBudget(newBudget)
        }
    }
}

// Factory để tạo BudgetViewModel
class BudgetViewModelFactory(private val repository: MoneyNoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}