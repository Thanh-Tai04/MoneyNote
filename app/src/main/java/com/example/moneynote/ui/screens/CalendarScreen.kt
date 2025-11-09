package com.example.moneynote.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QuestionMark
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
import androidx.compose.ui.unit.dp
import com.example.moneynote.data.Account
import com.example.moneynote.data.Transaction
import com.example.moneynote.ui.CalendarViewModel
import com.example.moneynote.ui.formatCurrency
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
// THÊM IMPORT MỚI
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.incomeCategories
import com.example.moneynote.ui.theme.PositiveGreen
import com.example.moneynote.ui.theme.NegativeRed

// #### MÀN HÌNH 2: LỊCH ####

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    // Lấy trạng thái (State) từ ViewModel
    val selectedDate by viewModel.selectedDate.collectAsState()
    val transactions by viewModel.transactionsForMonth.collectAsState()
    val accounts by viewModel.allAccounts.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()

    // Định dạng ngày tháng
    val monthFormat = SimpleDateFormat("MMMM, yyyy", Locale("vi", "VN"))
    val dayFormat = SimpleDateFormat("dd/MM (E)", Locale("vi", "VN"))

    // Tính toán tổng thu, chi, tổng
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val totalNet = totalIncome - totalExpense

    // Nhóm giao dịch theo ngày
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
        // 1. Tiêu đề "Lịch" và Bộ lọc
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
            // Nút bộ lọc tài khoản
            AccountFilterDropdown(
                accounts = accounts,
                selectedAccountId = selectedAccountId,
                onAccountSelected = { viewModel.selectAccount(it) }
            )
        }

        // 2. Bộ chọn tháng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.changeMonth(-1) }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Tháng trước")
            }
            Text(
                text = monthFormat.format(selectedDate).replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.changeMonth(1) }) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Tháng sau")
            }
        }

        // 3. Thẻ tóm tắt tháng
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            // SỬA: Dùng màu nền Surface (CardNight) từ Theme
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // SỬA: Dùng màu PositiveGreen và NegativeRed từ Theme
                SummaryItem(title = "Thu nhập", amount = totalIncome, color = PositiveGreen)
                SummaryItem(title = "Chi tiêu", amount = totalExpense, color = NegativeRed)
                SummaryItem(title = "Tổng", amount = totalNet, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Danh sách giao dịch (đã nhóm)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "Không có giao dịch nào trong tháng.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
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
                                // SỬA: Dùng màu PositiveGreen và NegativeRed từ Theme
                                color = if (dailyTotal >= 0) PositiveGreen else NegativeRed
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    }

                    // Danh sách giao dịch của ngày đó
                    items(transactionsOnDay) { transaction ->
                        // #### BẮT ĐẦU SỬA LỖI ####
                        // Lấy tên tài khoản từ ID
                        // (Mặc định là "Không rõ" nếu không tìm thấy)
                        val accountName = accounts.find { it.id == transaction.accountId }?.name ?: "Không rõ"

                        // Truyền accountName vào
                        TransactionRow(
                            transaction = transaction,
                            accountName = accountName
                        )
                        // #### KẾT THÚC SỬA LỖI ####
                    }
                }
            }
        }
    }
}

// Bộ lọc tài khoản
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


// Mục tóm tắt (Thu nhập, Chi tiêu, Tổng)
@Composable
fun SummaryItem(title: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) // Màu xám mờ
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color // <-- Áp dụng màu (PositiveGreen/NegativeRed)
        )
    }
}

// Một hàng giao dịch
@Composable
fun TransactionRow(transaction: Transaction, accountName: String) {
    // TÌM CATEGORY ĐỂ LẤY ICON VÀ MÀU
    val category = (expenseCategories + incomeCategories).find { it.name == transaction.category }
    val icon = category?.icon ?: Icons.Default.QuestionMark
    val tint = category?.color ?: MaterialTheme.colorScheme.onSurfaceVariant

    val amountColor = if (transaction.type == "expense")
        NegativeRed // <-- Dùng màu đỏ
    else PositiveGreen // <-- Dùng màu xanh

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp), // Tăng padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = transaction.category,
            modifier = Modifier.size(40.dp),
            tint = tint // <-- ÁP DỤNG MÀU ICON
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.category,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            // Hiển thị tên tài khoản và ghi chú
            Text(
                // SỬA: Chỉ hiển thị dấu ngoặc nếu có ghi chú
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