package com.example.moneynote.data // <-- ĐÃ SỬA

import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository (Kho lưu trữ)
 * Lớp này là nguồn cung cấp dữ liệu duy nhất (Single Source of Truth) cho ứng dụng.
 * Nó ẩn giấu logic (lấy từ Room DB hay API) khỏi ViewModel.
 */
class MoneyNoteRepository(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) {

    // --- Account operations ---
    val allAccounts: Flow<List<Account>> = accountDao.getAllAccounts()

    suspend fun insertOrUpdateAccount(account: Account) {
        accountDao.insertOrUpdateAccount(account)
    }

    // --- Transaction operations ---
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    // Lấy các giao dịch trong một khoảng thời gian
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
    }

    // Lấy các giao dịch CÓ LỌC theo tài khoản
    fun getTransactionsBetweenDates(
        startDate: Date,
        endDate: Date,
        accountId: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate, accountId)
    }

    // --- Budget operations ---
    fun getBudgetsForMonth(monthYear: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonth(monthYear)
    }

    suspend fun insertOrUpdateBudget(budget: Budget) {
        budgetDao.insertOrUpdateBudget(budget)
    }
}