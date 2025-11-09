package com.example.moneynote.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
// THÊM CÁC IMPORT CHO PREVIEW, STATE, VÀ COMPONENTS
import com.example.moneynote.ui.BudgetItem
import com.example.moneynote.ui.BudgetUiState
import com.example.moneynote.ui.BudgetViewModel
import com.example.moneynote.ui.Category
import com.example.moneynote.ui.components.MonthYearPicker
import com.example.moneynote.ui.components.SummaryRow
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.formatCurrency
import com.example.moneynote.ui.theme.NegativeRed
import com.example.moneynote.ui.theme.PositiveGreen
import com.example.moneynote.ui.theme.WarningOrange
import java.util.Date
import androidx.compose.ui.tooling.preview.Preview
import com.example.moneynote.ui.theme.MoneyNoteTheme

// #### MÀN HÌNH 4: HÀM "SMART" (CÓ VIEWMODEL) ####
@Composable
fun BudgetScreen(viewModel: BudgetViewModel) {
    // Thu thập State từ ViewModel
    val selectedDate by viewModel.selectedDate.collectAsState()
    val budgetState by viewModel.budgetState.collectAsState()

    BudgetScreenContent(
        selectedDate = selectedDate,
        budgetState = budgetState,
        onChangeMonth = { viewModel.changeMonth(it) },
        onSetBudget = { category, amount -> viewModel.setBudget(category, amount) }
    )
}

// #### MÀN HÌNH 4: HÀM "DUMB" (CHỈ CÓ UI) ####
@Composable
fun BudgetScreenContent(
    selectedDate: Date,
    budgetState: BudgetUiState,
    onChangeMonth: (Int) -> Unit,
    onSetBudget: (String, Double) -> Unit
) {
    // Trạng thái cho Dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Ăn uống") } // Mặc định
    var budgetAmount by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Tiêu đề
        Text(
            text = "Ngân sách",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2. Bộ chọn tháng
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonthYearPicker(
                date = selectedDate,
                onChangeMonth = onChangeMonth
            )

            TextButton(onClick = {
                // Mở dialog để TẠO MỚI
                selectedCategory = "Ăn uống" // Reset
                budgetAmount = ""
                isEditing = false
                showDialog = true
            }) {
                Text("Đặt Ngân sách")
            }
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
                SummaryRow(label = "Tổng ngân sách", amount = budgetState.totalBudget, color = MaterialTheme.colorScheme.onSurface)
                SummaryRow(label = "Đã chi tiêu", amount = budgetState.totalSpent, color = NegativeRed)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow(label = "Còn lại", amount = budgetState.totalBudget - budgetState.totalSpent, color = PositiveGreen, isBold = true)
            }
        }

        // 4. Danh sách Ngân sách
        if (budgetState.budgetItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa đặt ngân sách cho tháng này")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(budgetState.budgetItems) { budgetItem ->
                    BudgetRow(
                        item = budgetItem,
                        onClick = {
                            // Mở dialog để CHỈNH SỬA
                            selectedCategory = budgetItem.category
                            budgetAmount = budgetItem.limitAmount.toLong().toString()
                            isEditing = true
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // Hiển thị Dialog
    if (showDialog) {
        SetBudgetDialog(
            category = selectedCategory,
            amount = budgetAmount,
            isEditing = isEditing,
            onAmountChange = { budgetAmount = it },
            onCategoryChange = { selectedCategory = it },
            onDismiss = { showDialog = false },
            onConfirm = {
                val amountDouble = budgetAmount.toDoubleOrNull()
                if (amountDouble != null && amountDouble > 0) {
                    onSetBudget(selectedCategory, amountDouble) // Gọi sự kiện
                    showDialog = false
                }
            }
        )
    }
}

// Composable cho một hàng Ngân sách
@Composable
fun BudgetRow(item: BudgetItem, onClick: () -> Unit) {
    // TÌM CATEGORY ĐỂ LẤY ICON VÀ MÀU
    val category = expenseCategories.find { it.name == item.category }
        ?: Category("Khác", Icons.Default.QuestionMark, MaterialTheme.colorScheme.onSurfaceVariant)

    val progressColor = when {
        item.progress < 0.75f -> PositiveGreen
        item.progress < 1.0f -> WarningOrange
        else -> NegativeRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    modifier = Modifier.size(32.dp),
                    tint = category.color // <-- ÁP DỤNG MÀU ICON
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${"%.0f".format(item.progress * 100)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = progressColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Thanh tiến độ
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text (Đã chi / Hạn mức)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatCurrency(item.spentAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = progressColor
                )
                Text(
                    text = formatCurrency(item.limitAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Màu xám mờ
                )
            }
        }
    }
}

// Dialog để đặt ngân sách
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetDialog(
    category: String,
    amount: String,
    isEditing: Boolean,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Sửa Ngân sách" else "Đặt Ngân sách") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Chọn danh mục (chỉ khi tạo mới)
                if (!isEditing) {
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Danh mục") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor() // Quan trọng
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            expenseCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        onCategoryChange(cat.name)
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Hiển thị tên danh mục (không cho sửa)
                    Text(
                        text = "Danh mục: $category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Nhập số tiền
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Hạn mức") },
                    trailingIcon = { Text("đ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}


// #### THÊM HÀM PREVIEW NÀY VÀO CUỐI TỆP ####
@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    MoneyNoteTheme(darkTheme = true) {
        // Tạo dữ liệu giả (mock data)
        val mockBudgetState = BudgetUiState(
            totalBudget = 5000000.0,
            totalSpent = 1350000.0,
            budgetItems = listOf(
                BudgetItem("Ăn uống", 2000000.0, 1500000.0, 0.75f),
                BudgetItem("Đi lại", 1000000.0, 500000.0, 0.5f),
                BudgetItem("Mua sắm", 2000000.0, 2500000.0, 1.25f)
            )
        )

        BudgetScreenContent(
            selectedDate = Date(),
            budgetState = mockBudgetState,
            onChangeMonth = {},
            onSetBudget = { _, _ -> }
        )
    }
}