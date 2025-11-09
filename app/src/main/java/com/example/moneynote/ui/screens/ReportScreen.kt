package com.example.moneynote.ui.screens

// #### BẮT ĐẦU SỬA LỖI - DỌN DẸP IMPORT ####
import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneynote.ui.ChartData // <-- Từ ViewModel
import com.example.moneynote.ui.ReportViewModel
import com.example.moneynote.ui.components.AccountFilter
import com.example.moneynote.ui.components.MonthYearPicker
import com.example.moneynote.ui.components.SummaryRow
import com.example.moneynote.ui.expenseCategories // <-- Từ CategoryData
import com.example.moneynote.ui.formatCurrency // <-- Từ DateUtils
import com.example.moneynote.ui.incomeCategories // <-- Từ CategoryData
import com.example.moneynote.ui.theme.NegativeRed
import com.example.moneynote.ui.theme.PositiveGreen
// #### KẾT THÚC SỬA LỖI ####

// #### MÀN HÌNH 3: BÁO CÁO ####

@Composable
fun ReportScreen(viewModel: ReportViewModel) {
    // Thu thập State từ ViewModel
    val selectedDate by viewModel.selectedDate.collectAsState()
    val accounts by viewModel.allAccounts.collectAsState()
    val reportState by viewModel.reportState.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chi tiêu", "Thu nhập")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        // LỖI "verticalAlignment" ĐÃ ĐƯỢC XÓA
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
                onChangeMonth = { viewModel.changeMonth(it) }
            )
            AccountFilter(
                accounts = accounts,
                selectedAccountId = selectedAccountId,
                onAccountSelected = { viewModel.selectAccount(it) }
            )
        }

        // 3. Thẻ Tóm tắt
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            // Dùng màu nền Surface (CardNight) từ Theme
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Dùng màu PositiveGreen và NegativeRed
                SummaryRow(label = "Thu nhập", amount = reportState.totalIncome, color = PositiveGreen)
                SummaryRow(label = "Chi tiêu", amount = reportState.totalExpense, color = NegativeRed)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                SummaryRow(label = "Thu chi", amount = reportState.totalIncome - reportState.totalExpense, color = MaterialTheme.colorScheme.onSurface, isBold = true)
            }
        }

        // 4. Tabs (Chi tiêu / Thu nhập)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface // Màu nền Tab
        ) {
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
            val type = if (selectedTab == 0) "expense" else "income" // <-- Lấy type

            if (data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có dữ liệu")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // (Tùy chọn) Thêm Biểu đồ tròn ở đây
                    // ...

                    // Danh sách chi tiết
                    items(data) { chartData ->
                        // #### BẮT ĐẦU SỬA LỖI ####
                        // Sửa 'item' thành 'data'
                        // Thêm tham số 'type'
                        ReportCategoryRow(
                            data = chartData,
                            type = type
                        )
                        // #### KẾT THÚC SỬA LỖI ####
                    }
                }
            }
        }
    }
}

// Hàng báo cáo (ĐÃ ĐỔI TÊN TỪ ReportRow thành ReportCategoryRow)
@Composable
fun ReportCategoryRow(
    data: ChartData, // <-- Kiểu dữ liệu đúng
    type: String // "expense" hoặc "income"
) {
    // Tìm danh mục tương ứng để lấy icon và màu
    val categories = if (type == "expense") expenseCategories else incomeCategories
    val category = categories.find { it.name == data.category }
        ?: categories.find { it.name == "Khác" } // Mặc định là "Khác"

    val icon = category?.icon ?: Icons.Default.QuestionMark
    // Lấy màu từ danh mục (đã định nghĩa trong CategoryData.kt)
    val tint = category?.color ?: MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = data.category,
            modifier = Modifier.size(32.dp),
            tint = tint // <-- Áp dụng màu
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Tên và Thanh tiến độ
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${"%.1f".format(data.percentage * 100)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { data.percentage }, // Sửa: Dùng lambda
                modifier = Modifier.fillMaxWidth(),
                color = tint, // <-- Dùng màu của danh mục
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        // Tổng tiền
        Text(
            text = formatCurrency(data.amount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (type == "expense") NegativeRed else PositiveGreen,
            textAlign = TextAlign.End,
            modifier = Modifier.width(100.dp) // Đảm bảo căn lề đẹp
        )
    }
}

// #### BẮT ĐẦU SỬA LỖI ####
// XÓA BỎ HOÀN TOÀN HÀM ReportRow CŨ (dùng CategoryReportData)
// @Composable
// fun ReportRow(item: CategoryReportData, type: String) { ... }
// #### KẾT THÚC SỬA LỖI ####