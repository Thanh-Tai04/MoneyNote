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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(private val repository: MoneyNoteRepository) : ViewModel() {

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
    val transactionsForMonth: StateFlow<List<Transaction>> =
        combine(_selectedDate, _selectedAccountId) { date, accountId ->
            Pair(date, accountId)
        }.flatMapLatest { (date, accountId) ->
            val startDate = DateUtils.getMonthStartDate(date)
            val endDate = DateUtils.getMonthEndDate(date)

            if (accountId == 0L) {
                repository.getTransactionsBetweenDates(startDate, endDate)
            } else {
                repository.getTransactionsBetweenDates(startDate, endDate, accountId)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Hàm để thay đổi tháng
    fun changeMonth(amount: Int) {
        _selectedDate.value = DateUtils.changeMonth(_selectedDate.value, amount)
    }

    // Hàm để chọn tài khoản
    fun selectAccount(accountId: Long) {
        _selectedAccountId.value = accountId
    }
}

// Factory để tạo CalendarViewModel
class CalendarViewModelFactory(private val repository: MoneyNoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}