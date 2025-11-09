package com.example.moneynote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import com.example.moneynote.data.Account
import com.example.moneynote.data.Transaction
import com.example.moneynote.data.MoneyNoteRepository

/**
 * ViewModel cho Màn hình "Nhập vào" (AddTransactionScreen)
 */
class AddTransactionViewModel(private val repository: MoneyNoteRepository) : ViewModel() {

    // Trạng thái (State) cho danh sách tài khoản,
    // Lấy từ repository và chuyển thành StateFlow để Composable có thể theo dõi (collect)
    val allAccounts: StateFlow<List<Account>> = repository.allAccounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Bắt đầu thu thập khi UI hiển thị
            initialValue = emptyList() // Giá trị ban đầu
        )

    /**
     * Hàm để thêm một giao dịch mới
     * Được gọi từ Composable (UI)
     */
    fun addTransaction(
        type: String,
        date: Date,
        amount: Double,
        category: String,
        note: String?,
        accountId: Long
    ) {
        // Chạy coroutine trên ViewModelScope để thực hiện thao tác database
        viewModelScope.launch {
            if (amount > 0 && accountId > 0) { // Kiểm tra dữ liệu hợp lệ
                val newTransaction = Transaction(
                    type = type,
                    date = date,
                    amount = amount,
                    category = category,
                    note = note,
                    accountId = accountId
                )
                repository.insertTransaction(newTransaction)
            }
        }
    }

    /**
     * Hàm để thêm một tài khoản mới (ví dụ)
     * (Bạn có thể thêm màn hình Cài đặt để quản lý tài khoản sau)
     */
    fun addAccount(name: String, initialBalance: Double) {
        viewModelScope.launch {
            val newAccount = Account(
                name = name,
                initialBalance = initialBalance,
                iconName = "default_icon", // Tạm thời
                color = "#FFFFFF" // Tạm thời
            )
            repository.insertOrUpdateAccount(newAccount)
        }
    }
}

/**
 * ViewModelFactory
 * Lớp này RẤT QUAN TRỌNG.
 * Nó cho Android biết cách tạo (khởi tạo) AddTransactionViewModel
 * vì ViewModel này có một tham số (repository)
 */
class AddTransactionViewModelFactory(private val repository: MoneyNoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}