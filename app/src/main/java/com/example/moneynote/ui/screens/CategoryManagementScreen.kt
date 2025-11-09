package com.example.moneynote.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moneynote.ui.Category
import com.example.moneynote.ui.expenseCategories
import com.example.moneynote.ui.incomeCategories
import com.example.moneynote.ui.theme.MoneyNoteTheme
import androidx.compose.ui.tooling.preview.Preview

/**
 * Màn hình Quản lý Danh mục (image_8c5f4a.png)
 * Tạm thời dùng dữ liệu hardcoded từ CategoryData.kt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chi tiêu", "Thu nhập")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm danh mục") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("create_category") }) {
                        Icon(Icons.Default.Add, contentDescription = "Tạo mới")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = MaterialTheme.typography.titleMedium) }
                    )
                }
            }

            // Hiển thị danh sách dựa trên Tab
            val currentList = if (selectedTab == 0) expenseCategories else incomeCategories
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(currentList) { category ->
                    CategoryListRow(category = category)
                }
            }
        }
    }
}

// Một hàng trong danh sách Quản lý Danh mục
@Composable
fun CategoryListRow(category: Category) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.name,
            tint = category.color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { /* (Tạm thời) Chưa làm gì */ }) {
            Icon(Icons.Default.DragHandle, contentDescription = "Sắp xếp")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryManagementScreenPreview() {
    MoneyNoteTheme(darkTheme = true) {
        CategoryManagementScreen(navController = rememberNavController())
    }
}