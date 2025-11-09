package com.example.moneynote

import android.app.Application
import com.example.moneynote.data.AppDatabase
import com.example.moneynote.data.MoneyNoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
/**
 * Lớp Application tùy chỉnh
 * Dùng để khởi tạo và giữ các đối tượng tồn tại trong suốt vòng đời ứng dụng
 * (Singleton) như Database và Repository.
 */
class MoneyNoteApplication : Application() {

    // Khởi tạo Database (chỉ một lần)
    // 'lazy' nghĩa là nó chỉ được tạo khi được gọi lần đầu tiên
    val applicationScope = CoroutineScope(SupervisorJob())

    // 2. Sửa 'database by lazy' để truyền scope vào
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    // Khởi tạo Repository (chỉ một lần)
    val repository by lazy {
        MoneyNoteRepository(
            database.accountDao(),
            database.transactionDao(),
            database.budgetDao()
        )
    }
}