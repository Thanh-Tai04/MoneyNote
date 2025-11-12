package com.example.moneynote.ui.screens

// THÊM CÁC IMPORT CHO HIỆU ỨNG VÀ ICON MỚI
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneynote.data.Account
import com.example.moneynote.data.Transaction
import com.example.moneynote.ui.CalendarViewModel
import com.example.moneynote.ui.formatCurrency
import com.example.moneynote.ui.generateCalendarDays
import com.example.moneynote.ui.getWeekDayNames
import com.example.moneynote.ui.isSameDay
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.incomeCategories
import com.example.moneynote.ui.theme.MoneyNoteTheme
import com.example.moneynote.ui.theme.MutedGray
import com.example.moneynote.ui.theme.NegativeRed
import com.example.moneynote.ui.theme.PositiveGreen
import com.example.moneynote.ui.Category
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.incomeCategories
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Data class
data class DaySummary(
    val date: Date,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean
)

// #### MÀN HÌNH 2: HÀM "SMART" (CÓ VIEWMODEL) ####
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    // Lấy trạng thái (State) từ ViewModel
    val selectedDate by viewModel.selectedDate.collectAsState()
    val transactions by viewModel.transactionsForMonth.collectAsState()
    val accounts by viewModel.allAccounts.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    CalendarScreenContent(
        selectedDate = selectedDate,
        transactions = transactions,
        accounts = accounts,
        selectedAccountId = selectedAccountId,
        selectedDay = selectedDay,
        expenseCategories = expenseCategories,
        incomeCategories = incomeCategories,
        onAccountSelected = { viewModel.selectAccount(it) },
        onChangeMonth = { viewModel.changeMonth(it) },
        onDaySelected = { viewModel.onDaySelected(it) }
    )
}

// #### MÀN HÌNH 2: HÀM "DUMB" (CHỈ CÓ UI) ####
@Composable
fun CalendarScreenContent(
    selectedDate: Date,
    transactions: List<Transaction>,
    accounts: List<Account>,
    selectedAccountId: Long,
    selectedDay: Date,
    expenseCategories: List<Category>,
    incomeCategories: List<Category>,
    onAccountSelected: (Long) -> Unit,
    onChangeMonth: (Int) -> Unit,
    onDaySelected: (Date) -> Unit
) {
    // Định dạng ngày tháng
    val monthFormat = SimpleDateFormat("MMMM, yyyy", Locale("vi", "VN"))
    val dayFormat = SimpleDateFormat("dd/MM (E)", Locale("vi", "VN"))
    val dayOnlyFormat = SimpleDateFormat("d", Locale.getDefault())
    val weekDayNames = getWeekDayNames()

    // 1. Thêm trạng thái Ẩn/Hiện
    var isCalendarExpanded by remember { mutableStateOf(true) }

    // Tính toán tổng thu, chi, tổng CỦA THÁNG
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val totalNet = totalIncome - totalExpense

    // Tạo danh sách 42 ngày cho lưới lịch
    val calendarDays = generateCalendarDays(selectedDate)

    // Tính toán Thu/Chi cho từng ngày
    val daysWithSummaries = calendarDays.map { day ->
        val dayTransactions = transactions.filter {
            isSameDay(it.date, day)
        }
        val cal1 = Calendar.getInstance().apply { time = day }
        val cal2 = Calendar.getInstance().apply { time = selectedDate }
        val isCurrentMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)

        DaySummary(
            date = day,
            totalIncome = dayTransactions.filter { it.type == "income" }.sumOf { it.amount },
            totalExpense = dayTransactions.filter { it.type == "expense" }.sumOf { it.amount },
            isCurrentMonth = isCurrentMonth,
            isSelected = isSameDay(day, selectedDay)
        )
    }

    // Nhóm giao dịch theo ngày (cho danh sách ở cuối)
    val groupedTransactions = transactions
        .sortedByDescending { it.date }
        .groupBy {
            Calendar.getInstance().apply {
                time = it.date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Tiêu đề "Lịch" và Icon Tìm kiếm
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lịch",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* (Tạm thời) Chưa làm gì */ }) {
                Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
            }
        }

        // 2. Bộ chọn tháng (có thể Ẩn/Hiện)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onChangeMonth(-1) }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Tháng trước")
            }

            // 2. Làm cho Text và Icon có thể nhấn để Ẩn/Hiện
            Row(
                modifier = Modifier.clickable { isCalendarExpanded = !isCalendarExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthFormat.format(selectedDate).replaceFirstChar { it.titlecase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isCalendarExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isCalendarExpanded) "Thu gọn" else "Mở rộng"
                )
            }

            IconButton(onClick = { onChangeMonth(1) }) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Tháng sau")
            }
        }

        // 3. Gói Lịch vào AnimatedVisibility
        AnimatedVisibility(visible = isCalendarExpanded) {
            Column {
                // 3.1 Tiêu đề các ngày trong tuần (T2, T3...)
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDayNames.forEach { dayName ->
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // 3.2 Lưới Lịch
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    // TẮT cuộn (để LazyColumn chính cuộn)
                    userScrollEnabled = false
                ) {
                    items(daysWithSummaries) { daySummary ->
                        CalendarDayCell(
                            summary = daySummary,
                            dayFormat = dayOnlyFormat,
                            onClick = { onDaySelected(daySummary.date) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Thẻ tóm tắt tháng
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem(title = "Thu nhập", amount = totalIncome, color = PositiveGreen)
                SummaryItem(title = "Chi tiêu", amount = totalExpense, color = NegativeRed)
                SummaryItem(title = "Tổng", amount = totalNet, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Danh sách giao dịch (CHO CẢ THÁNG)
        LazyColumn(
            modifier = Modifier.fillMaxSize(), // Chiếm phần còn lại
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "Không có giao dịch nào trong tháng này.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            } else {
                groupedTransactions.forEach { (date, transactionsOnDay) ->
                    val dailyTotal = transactionsOnDay.sumOf {
                        if (it.type == "expense") -it.amount else it.amount
                    }

                    // Tiêu đề ngày
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dayFormat.format(date),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = formatCurrency(dailyTotal),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (dailyTotal >= 0) PositiveGreen else NegativeRed
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    }

                    // Danh sách giao dịch của ngày đó
                    items(transactionsOnDay) { transaction ->
                        val accountName = accounts.find { it.id == transaction.accountId }?.name ?: "Không rõ"
                        TransactionRow(
                            transaction = transaction,
                            accountName = accountName,
                            expenseCategories = expenseCategories,
                            incomeCategories = incomeCategories
                        )
                    }
                }
            }
        }
    }
}

// Bộ lọc tài khoản (Không dùng, nhưng giữ lại)
@Composable
fun AccountFilterDropdown(
    accounts: List<Account>,
    selectedAccountId: Long,
    onAccountSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.FilterList, contentDescription = "Lọc tài khoản")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Tất cả tài khoản") },
                onClick = {
                    onAccountSelected(0L)
                    expanded = false
                }
            )
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        onAccountSelected(account.id)
                        expanded = false
                    }
                )
            }
        }
    }
}


// Mục tóm tắt (Giữ nguyên)
@Composable
fun SummaryItem(title: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// Một hàng giao dịch
@Composable
fun TransactionRow(transaction: Transaction, accountName: String, expenseCategories: List<Category>,
                   incomeCategories: List<Category>) {
    val category = (expenseCategories + incomeCategories).find { it.name == transaction.category }
    val icon = category?.icon ?: Icons.Default.QuestionMark
    val tint = category?.color ?: MaterialTheme.colorScheme.onSurfaceVariant

    val amountColor = if (transaction.type == "expense")
        NegativeRed
    else PositiveGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = transaction.category,
            modifier = Modifier.size(40.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.category,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = (transaction.note?.plus(" ") ?: "") + "($accountName)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Text(
            text = formatCurrency(transaction.amount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
fun CalendarDayCell(
    summary: DaySummary,
    dayFormat: SimpleDateFormat,
    onClick: () -> Unit
) {
    val alpha = if (summary.isCurrentMonth) 1.0f else 0.4f
    val dayColor = if (summary.isCurrentMonth) MaterialTheme.colorScheme.onSurface else MutedGray

    val border = if (summary.isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(1.dp), // Giữ nguyên
        shape = MaterialTheme.shapes.small,
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = if (summary.isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        onClick = onClick
    ) {
        Column(
            // SỬA: Giảm padding
            modifier = Modifier.padding(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = dayFormat.format(summary.date),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = dayColor.copy(alpha = alpha)
            )
            // SỬA: Giảm khoảng cách
            Spacer(modifier = Modifier.height(1.dp))

            if (summary.totalIncome > 0) {
                Text(
                    text = formatCurrency(summary.totalIncome),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, lineHeight = 9.sp),
                    color = PositiveGreen.copy(alpha = alpha),
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
            }
            if (summary.totalExpense > 0) {
                Text(
                    text = formatCurrency(summary.totalExpense),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, lineHeight = 9.sp),
                    color = NegativeRed.copy(alpha = alpha),
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// #### HÀM PREVIEW  ####
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    MoneyNoteTheme(darkTheme = true) {
        val mockAccounts = listOf(
            Account(1, "Tiền mặt", 0.0, "wallet", "#FFFFFF"),
            Account(2, "Ngân hàng", 0.0, "account_balance", "#FFFFFF")
        )
        val mockTransactions = listOf(
            Transaction(1, "expense", Date(), 50000.0, "Ăn uống", "Cơm trưa", 1L),
            Transaction(2, "income", Date(), 2000000.0, "Tiền lương", null, 2L),
            Transaction(3, "expense", Date(), 150000.0, "Đi lại", "Grab", 1L)
        )

        CalendarScreenContent(
            selectedDate = Date(),
            transactions = mockTransactions,
            accounts = mockAccounts,
            selectedAccountId = 0L,
            selectedDay = Date(),
            expenseCategories = expenseCategories, // <-- SỬA: Dùng list hardcoded
            incomeCategories = incomeCategories,
            onAccountSelected = {},
            onChangeMonth = {},
            onDaySelected = {}
        )
    }
}