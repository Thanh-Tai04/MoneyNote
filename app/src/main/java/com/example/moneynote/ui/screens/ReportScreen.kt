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
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.moneynote.data.Account
import com.example.moneynote.ui.ChartData
import com.example.moneynote.ui.ReportUiState
import com.example.moneynote.ui.ReportViewModel
import com.example.moneynote.ui.components.AccountFilter
import com.example.moneynote.ui.components.DonutChart
import com.example.moneynote.ui.components.MonthYearPicker
import com.example.moneynote.ui.components.SummaryRow
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.formatCurrency
import com.example.moneynote.ui.incomeCategories
import com.example.moneynote.ui.theme.NegativeRed
import com.example.moneynote.ui.theme.PositiveGreen
import java.util.Date
import androidx.compose.ui.tooling.preview.Preview
import com.example.moneynote.ui.theme.MoneyNoteTheme

// #### MÀN HÌNH 3: HÀM "SMART" (CÓ VIEWMODEL) ####
@Composable
fun ReportScreen(viewModel: ReportViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val accounts by viewModel.allAccounts.collectAsState()
    val reportState by viewModel.reportState.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()

    ReportScreenContent(
        selectedDate = selectedDate,
        accounts = accounts,
        reportState = reportState,
        selectedAccountId = selectedAccountId,
        onChangeMonth = { viewModel.changeMonth(it) },
        onAccountSelected = { viewModel.selectAccount(it) }
    )
}

// #### MÀN HÌNH 3: HÀM "DUMB" (CHỈ CÓ UI) ####
@Composable
fun ReportScreenContent(
    selectedDate: Date,
    accounts: List<Account>,
    reportState: ReportUiState,
    selectedAccountId: Long,
    onChangeMonth: (Int) -> Unit,
    onAccountSelected: (Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chi tiêu", "Thu nhập")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Tiêu đề
        Text(
            text = "Báo cáo",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2. Bộ chọn tháng và Bộ lọc tài khoản
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonthYearPicker(
                date = selectedDate,
                onChangeMonth = onChangeMonth
            )
            AccountFilter(
                accounts = accounts,
                selectedAccountId = selectedAccountId,
                onAccountSelected = onAccountSelected
            )
        }

        // 3. Thẻ Tóm tắt
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow(label = "Thu nhập", amount = reportState.totalIncome, color = PositiveGreen)
                SummaryRow(label = "Chi tiêu", amount = reportState.totalExpense, color = NegativeRed)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow(label = "Thu chi", amount = reportState.totalIncome - reportState.totalExpense, color = MaterialTheme.colorScheme.onSurface, isBold = true)
            }
        }

        // 4. Tabs (Chi tiêu / Thu nhập)
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.titleMedium) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Nội dung Tab
        Box(modifier = Modifier.weight(1f)) {
            val data = if (selectedTab == 0) reportState.expenseChartData else reportState.incomeChartData
            val type = if (selectedTab == 0) "expense" else "income"

            if (data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có dữ liệu")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // ★ THÊM BIỂU ĐỒ TRÒN Ở ĐÂY ★
                    item {
                        DonutChart(
                            data = data,
                            type = type,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Danh sách chi tiết
                    items(data) { chartData ->
                        ReportCategoryRow(data = chartData, type = type)
                    }
                }
            }
        }
    }
}


// #### HÀM COMPOSABLE PHỤ ####
@Composable
fun ReportCategoryRow(data: ChartData, type: String) {
    val categoryList = if (type == "expense") expenseCategories else incomeCategories
    val category = categoryList.find { it.name == data.category }
    val icon = category?.icon ?: Icons.Default.QuestionMark
    val tint = category?.color ?: MaterialTheme.colorScheme.onSurfaceVariant

    val amountColor = if (type == "expense") NegativeRed else PositiveGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = data.category,
            modifier = Modifier.size(40.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = data.category,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatCurrency(data.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Text(
                text = "${"%.1f".format(data.percentage * 100)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// #### PREVIEW ####
@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    MoneyNoteTheme(darkTheme = true) {
        val mockAccounts = listOf(
            Account(1, "Tiền mặt", 0.0, "wallet", "#FFFFFF"),
            Account(2, "Ngân hàng", 0.0, "account_balance", "#FFFFFF")
        )

        val mockReportState = ReportUiState(
            totalIncome = 5000000.0,
            totalExpense = 1790000.0,
            expenseChartData = listOf(
                ChartData("Ăn uống", 800000.0, 0.447f, Color(0xFF00B894)),
                ChartData("Đi lại", 120000.0, 0.067f, Color(0xFF0984E3)),
                ChartData("Chi tiêu hàng ngày", 400000.0, 0.225f, Color(0xFFFFC312)),
                ChartData("Y tế", 90000.0, 0.051f, Color(0xFFE17055)),
                ChartData("Khác", 380000.0, 0.210f, Color(0xFF6C5CE7))
            ),
            incomeChartData = listOf(
                ChartData("Tiền lương", 5000000.0, 1.0f, Color(0xFF00B894))
            )
        )

        ReportScreenContent(
            selectedDate = Date(),
            accounts = mockAccounts,
            reportState = mockReportState,
            selectedAccountId = 0L,
            onChangeMonth = {},
            onAccountSelected = {}
        )
    }
}
