package com.example.moneynote.ui.screens

// TẤT CẢ IMPORT CẦN THIẾT
import com.example.moneynote.ui.Category
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.incomeCategories
import com.example.moneynote.ui.formatCurrency
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
// IMPORT CHO ẨN BÀN PHÍM
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneynote.data.Account
import com.example.moneynote.ui.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
// IMPORT CHO ẨN BÀN PHÍM
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
// IMPORT CHO PREVIEW
import androidx.compose.ui.tooling.preview.Preview
import com.example.moneynote.ui.theme.MoneyNoteTheme
import com.example.moneynote.ui.CurrencyVisualTransformation
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moneynote.ui.theme.MutedGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// #### MÀN HÌNH 1: HÀM "SMART" (CÓ VIEWMODEL) ####
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    // Lấy danh sách tài khoản từ ViewModel
    val accounts by viewModel.allAccounts.collectAsState()

    AddTransactionScreenContent(
        accounts = accounts,
        expenseCategories = expenseCategories,
        incomeCategories = incomeCategories,
        navController = navController,
        snackbarHostState = snackbarHostState,
        scope = scope,
        onAddTransaction = { type, date, amount, category, note, accountId ->
            viewModel.addTransaction(type, date, amount, category, note, accountId)
        }
    )
}

// #### MÀN HÌNH 1: HÀM "DUMB" (CHỈ CÓ UI) ####
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreenContent(
    accounts: List<Account>,
    expenseCategories: List<Category>,
    incomeCategories: List<Category>,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onAddTransaction: (String, Date, Double, String, String?, Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chi tiêu", "Thu nhập")
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(accounts) {
        if (selectedAccount == null && accounts.isNotEmpty()) {
            selectedAccount = accounts.find { it.name == "Tiền mặt" } ?: accounts.first()
        }
    }
    fun resetForm() {
        amount = ""
        note = ""
        selectedCategory = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sổ thu chi",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
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
        Spacer(modifier = Modifier.height(12.dp))
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
        Spacer(modifier = Modifier.height(4.dp))
        var accountDropdownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = accountDropdownExpanded,
            onExpandedChange = { accountDropdownExpanded = !accountDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedAccount?.name ?: "Đang tải...",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tài khoản") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
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
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Ghi chú") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    amount = it
                }
            },
            label = { Text("Số tiền") },
            trailingIcon = { Text("đ", style = MaterialTheme.typography.bodyLarge) },
            visualTransformation = CurrencyVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Danh mục",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 4. Lưới Danh mục
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
                val amountDouble = amount.replace(".", "").toDoubleOrNull()

                if (amountDouble != null && amountDouble > 0 && selectedCategory != null && selectedAccount != null) {
                    onAddTransaction(
                        if (selectedTab == 0) "expense" else "income",
                        selectedDate,
                        amountDouble,
                        selectedCategory!!.name,
                        note.takeIf { it.isNotBlank() },
                        selectedAccount!!.id
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Đã thêm giao dịch ${formatCurrency(amountDouble)}",
                            duration = androidx.compose.material3.SnackbarDuration.Short
                        )
                    }

                    resetForm()
                    keyboardController?.hide()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Vui lòng điền đủ thông tin (Số tiền, Danh mục, Tài khoản)",
                            duration = androidx.compose.material3.SnackbarDuration.Short
                        )
                    }
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time,
            initialDisplayMode = DisplayMode.Picker
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
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
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
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else category.color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    MoneyNoteTheme(darkTheme = true) {
        val mockAccounts = listOf(
            Account(1, "Tiền mặt", 0.0, "wallet", "#FFFFFF"),
            Account(2, "Ngân hàng", 0.0, "account_balance", "#FFFFFF")
        )

        AddTransactionScreenContent(
            accounts = mockAccounts,
            expenseCategories = expenseCategories,
            incomeCategories = incomeCategories,
            navController = rememberNavController(),
            snackbarHostState = remember { SnackbarHostState() },
            scope = rememberCoroutineScope(),
            onAddTransaction = { _, _, _, _, _, _ -> }
        )
    }
}