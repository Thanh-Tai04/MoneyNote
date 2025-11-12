package com.example.moneynote.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import java.util.Date
import androidx.sqlite.db.SupportSQLiteDatabase

// #### 1. BỘ CHUYỂN ĐỔI KIỂU DỮ LIỆU (TYPE CONVERTERS) ####
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "initial_balance")
    val initialBalance: Double,
    @ColumnInfo(name = "icon_name")
    val iconName: String,
    val color: String
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "expense" or "income"
    val date: Date,
    val amount: Double,
    val category: String, // Tên danh mục (Hardcoded)
    val note: String?,
    @ColumnInfo(name = "account_id")
    val accountId: Long // Foreign key to Account
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "month_year")
    val monthYear: String, // Format: "YYYY-MM"
    @ColumnInfo(name = "category_name")
    val categoryName: String, // Tên danh mục (Hardcoded)
    @ColumnInfo(name = "limit_amount")
    val limitAmount: Double
)

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(account: Account)
    @Delete
    suspend fun deleteAccount(account: Account)
    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<Account>>
}

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction)
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): Flow<List<Transaction>>
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND account_id = :accountId ORDER BY date DESC")
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date, accountId: Long): Flow<List<Transaction>>
}
@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: Budget)
    @Query("SELECT * FROM budgets WHERE month_year = :monthYear")
    fun getBudgetsForMonth(monthYear: String): Flow<List<Budget>>
    @Query("SELECT * FROM budgets WHERE month_year = :monthYear AND category_name = :categoryName")
    suspend fun getBudgetForCategory(monthYear: String, categoryName: String): Budget?
}

// #### 4. LỚP APP DATABASE CHÍNH ####

@Database(
    entities = [Account::class, Transaction::class, Budget::class],
    version = 1, // (Giữ nguyên, nhưng phải Clear Data)
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    private class MoneyNoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.accountDao())
                }
            }
        }

        suspend fun populateDatabase(accountDao: AccountDao) {
            // Thêm tài khoản mặc định
            accountDao.insertOrUpdateAccount(Account(name = "Tiền mặt", initialBalance = 0.0, iconName = "wallet", color = "#4CAF50"))
            accountDao.insertOrUpdateAccount(Account(name = "Ngân hàng", initialBalance = 0.0, iconName = "account_balance", color = "#2196F3"))

        }
    }


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_note_database"
                )
                    // Dòng này sẽ xóa DB cũ (có Bảng Category)
                    .fallbackToDestructiveMigration()
                    .addCallback(MoneyNoteDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}