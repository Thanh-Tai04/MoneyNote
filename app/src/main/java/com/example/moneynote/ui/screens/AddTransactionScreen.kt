package com.example.moneynote.ui.screens

import com.example.moneynote.ui.Category
import com.example.moneynote.ui.incomeCategories
import com.example.moneynote.ui.expenseCategories
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneynote.data.Account
import com.example.moneynote.ui.AddTransactionViewModel
import com.example.moneynote.ui.theme.CardNight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// #### MÀN HÌNH 1: NHẬP VÀO ####
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: AddTransactionViewModel) {
    // Lấy danh sách tài khoản từ ViewModel
    val accounts by viewModel.allAccounts.collectAsState()

    // Trạng thái (State) cho toàn màn hình
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Chi tiêu, 1: Thu nhập
    val tabs = listOf("Chi tiêu", "Thu nhập")

    // Trạng thái cho Form
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // #### BẮT ĐẦU SỬA LỖI - MẶC ĐỊNH CHỌN TÀI KHOẢN ####
    // Được chạy mỗi khi danh sách `accounts` thay đổi
    LaunchedEffect(accounts) {
        if (selectedAccount == null && accounts.isNotEmpty()) {
            // Mặc định chọn tài khoản đầu tiên ("Tiền mặt")
            selectedAccount = accounts.first()
        }
    }
    // #### KẾT THÚC SỬA LỖI - MẶC ĐỊNH CHỌN TÀI KHOẢN ####


    // Hàm reset form
    fun resetForm() {
        amount = ""
        note = ""
        selectedCategory = null
        selectedDate = Date()
        // GIỮ LẠI selectedAccount (để nó luôn là "Tiền mặt")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Tiêu đề "Sổ thu chi"
        Text(
            text = "Sổ thu chi",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2. TabRow (Chi tiêu / Thu nhập)
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        selectedCategory = null // Reset danh mục khi chuyển tab
                    },
                    text = { Text(title, style = MaterialTheme.typography.titleMedium) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Form nhập liệu chung
        // Chọn ngày
        OutlinedTextField(
            value = SimpleDateFormat("dd/MM/yyyy (E)", Locale.getDefault()).format(selectedDate),
            onValueChange = {},
            readOnly = true,
            label = { Text("Ngày") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Chọn ngày",
                    modifier = Modifier.clickable { showDatePicker = true }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Chọn tài khoản (Dropdown)
        var accountDropdownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = accountDropdownExpanded,
            onExpandedChange = { accountDropdownExpanded = !accountDropdownExpanded }
        ) {
            OutlinedTextField(
                // Hiển thị tên tài khoản đã chọn (hoặc "Chọn tài khoản" nếu null)
                value = selectedAccount?.name ?: "Chọn tài khoản",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tài khoản") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor() // Quan trọng
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = accountDropdownExpanded,
                onDismissRequest = { accountDropdownExpanded = false }
            ) {
                accounts.forEach { account ->
                    DropdownMenuItem(
                        text = { Text(account.name) },
                        onClick = {
                            selectedAccount = account
                            accountDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ghi chú
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Ghi chú") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Số tiền
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Số tiền") },
            trailingIcon = { Text("đ", style = MaterialTheme.typography.bodyLarge) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Lưới Danh mục
        Text(
            text = "Danh mục",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val currentCategories = if (selectedTab == 0) expenseCategories else incomeCategories
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 90.dp),
            modifier = Modifier.weight(1f), // Chiếm phần còn lại
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentCategories) { category ->
                CategoryItem(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Nút Nhập
        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                // Kiểm tra dữ liệu: đảm bảo 4 trường quan trọng được điền
                if (amountDouble != null && amountDouble > 0 && selectedCategory != null && selectedAccount != null) {
                    viewModel.addTransaction(
                        type = if (selectedTab == 0) "expense" else "income",
                        date = selectedDate,
                        amount = amountDouble,
                        category = selectedCategory!!.name,
                        note = note.takeIf { it.isNotBlank() },
                        accountId = selectedAccount!!.id
                    )
                    // Reset form sau khi nhập
                    resetForm()
                } else {
                    // (Tùy chọn) Hiển thị thông báo lỗi cho người dùng
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (selectedTab == 0) "Nhập khoản Tiền chi" else "Nhập khoản Tiền thu",
                fontSize = 18.sp
            )
        }
    }

    // Hiển thị DatePickerDialog khi showDatePicker == true
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time,
            initialDisplayMode = DisplayMode.Picker // Hiển thị kiểu Lịch
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Date(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Composable cho một mục danh mục
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(90.dp),
        colors = CardDefaults.cardColors(
            // SỬA: Dùng màu CardNight cho màu nền mặc định
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        // Thêm đổ bóng
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(32.dp),
                // LẤY MÀU TỪ CATEGORY
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else category.color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                // Màu chữ (trắng mờ hoặc trắng)
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}