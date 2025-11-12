package com.example.moneynote.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class MoneyNoteRepository(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) {

    val allAccounts: Flow<List<Account>> = accountDao.getAllAccounts()

    suspend fun insertOrUpdateAccount(account: Account) {
        accountDao.insertOrUpdateAccount(account)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
    }

    fun getTransactionsBetweenDates(
        startDate: Date,
        endDate: Date,
        accountId: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate, accountId)
    }

    fun getBudgetsForMonth(monthYear: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonth(monthYear)
    }

    suspend fun insertOrUpdateBudget(budget: Budget) {
        budgetDao.insertOrUpdateBudget(budget)
    }

    suspend fun getBudgetForCategory(monthYear: String, categoryName: String): Budget? {
        return budgetDao.getBudgetForCategory(monthYear, categoryName)
    }
}