package com.example.moneynote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.moneynote.ui.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tệp này chứa các Composable được dùng chung
 * bởi Màn hình Báo cáo (ReportScreen) và Màn hình Ngân sách (BudgetScreen)
 */

// Composable để chọn Tháng/Năm
@Composable
fun MonthYearPicker(
    date: Date,
    onChangeMonth: (Int) -> Unit // -1 cho lùi, +1 cho tiến
) {
    val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onChangeMonth(-1) }) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Tháng trước")
        }
        Text(
            text = monthYearFormat.format(date),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { onChangeMonth(1) }) {
            Icon(Icons.Default.ArrowForwardIos, contentDescription = "Tháng sau")
        }
    }
}

// Composable để lọc tài khoản
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFilter(
    accounts: List<Account>,
    selectedAccountId: Long,
    onAccountSelected: (Long) -> Unit
) {
    var filterExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { filterExpanded = true }) {
            Icon(Icons.Default.FilterList, contentDescription = "Lọc tài khoản")
        }

        DropdownMenu(
            expanded = filterExpanded,
            onDismissRequest = { filterExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Tất cả tài khoản") },
                onClick = {
                    onAccountSelected(0L) // 0L là ID cho "Tất cả"
                    filterExpanded = false
                },
                modifier = if (selectedAccountId == 0L) Modifier.background(MaterialTheme.colorScheme.primaryContainer) else Modifier
            )
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        onAccountSelected(account.id)
                        filterExpanded = false
                    },
                    modifier = if (selectedAccountId == account.id) Modifier.background(MaterialTheme.colorScheme.primaryContainer) else Modifier
                )
            }
        }
    }
}

// Composable cho một hàng tóm tắt
@Composable
fun SummaryRow(label: String, amount: Double, color: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}