package com.example.moneynote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneynote.data.Account
import com.example.moneynote.data.MoneyNoteRepository
import com.example.moneynote.data.Transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted
import java.util.Date
import androidx.compose.ui.graphics.Color
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.incomeCategories

// Dữ liệu cho biểu đồ
data class ChartData(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val color: androidx.compose.ui.graphics.Color
)

// Trạng thái màn hình Báo cáo
data class ReportUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val expenseChartData: List<ChartData> = emptyList(),
    val incomeChartData: List<ChartData> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModel(private val repository: MoneyNoteRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate

    private val _selectedAccountId = MutableStateFlow(0L)
    val selectedAccountId: StateFlow<Long> = _selectedAccountId

    val allAccounts: StateFlow<List<Account>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val reportState: StateFlow<ReportUiState> =
        combine(_selectedDate, _selectedAccountId) { date, accountId -> Pair(date, accountId) }
            .flatMapLatest { (date, accountId) ->
                val startDate = DateUtils.getMonthStartDate(date)
                val endDate = DateUtils.getMonthEndDate(date)

                val transactionsFlow = if (accountId == 0L) {
                    repository.getTransactionsBetweenDates(startDate, endDate)
                } else {
                    repository.getTransactionsBetweenDates(startDate, endDate, accountId)
                }

                transactionsFlow.map { transactions ->
                    calculateReportState(transactions)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, ReportUiState())

    private fun calculateReportState(transactions: List<Transaction>): ReportUiState {
        val incomeTransactions = transactions.filter { it.type == "income" }
        val expenseTransactions = transactions.filter { it.type == "expense" }

        val totalIncome = incomeTransactions.sumOf { it.amount }
        val totalExpense = expenseTransactions.sumOf { it.amount }

        val expenseChartData = calculateChartData(expenseTransactions, totalExpense, "expense")
        val incomeChartData = calculateChartData(incomeTransactions, totalIncome, "income")

        return ReportUiState(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            expenseChartData = expenseChartData,
            incomeChartData = incomeChartData
        )
    }

    private fun calculateChartData(
        transactions: List<Transaction>,
        total: Double,
        type: String
    ): List<ChartData> {
        if (total == 0.0) return emptyList()

        val categoryList = if (type == "expense") expenseCategories else incomeCategories

        return transactions
            .groupBy { it.category }
            .map { (category, list) ->
                val categoryAmount = list.sumOf { it.amount }
                val color = categoryList.find { it.name == category }?.color ?: Color.Gray
                ChartData(
                    category = category,
                    amount = categoryAmount,
                    percentage = (categoryAmount / total).toFloat(),
                    color = color
                )
            }
            .sortedByDescending { it.amount }
    }

    fun changeMonth(amount: Int) {
        _selectedDate.value = DateUtils.changeMonth(_selectedDate.value, amount)
    }

    fun selectAccount(accountId: Long) {
        _selectedAccountId.value = accountId
    }
}

// Factory để tạo ViewModel
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
