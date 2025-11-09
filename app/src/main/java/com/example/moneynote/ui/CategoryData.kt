package com.example.moneynote.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.moneynote.ui.theme.NegativeRed
import com.example.moneynote.ui.theme.PositiveGreen
import com.example.moneynote.ui.theme.WarningOrange


// THÊM thuộc tính `color: Color` vào data class
data class Category(val name: String, val icon: ImageVector, val color: Color)

// Gán màu cho các danh mục chi tiêu
val expenseCategories = listOf(
    Category("Ăn uống", Icons.Default.Fastfood, Color(0xFFFFA726)), // Orange
    Category("Đi lại", Icons.Default.Train, Color(0xFF42A5F5)), // Blue
    Category("Tiền nhà", Icons.Default.House, Color(0xFF7E57C2)), // Purple
    Category("Mua sắm", Icons.Default.ShoppingCart, Color(0xFFEC407A)), // Pink
    Category("Y tế", Icons.Default.LocalHospital, Color(0xFFEF5350)), // Red
    Category("Giáo dục", Icons.Default.School, Color(0xFF26A69A)), // Teal
    Category("Khác", Icons.Default.QuestionMark, Color(0xFFBDBDBD)) // Gray
)

// Gán màu cho các danh mục thu nhập
val incomeCategories = listOf(
    Category("Tiền lương", Icons.Default.Payments, PositiveGreen),
    Category("Tiền thưởng", Icons.Default.CardGiftcard, Color(0xFFFFD54F)), // Yellow
    Category("Đầu tư", Icons.Default.AccountBalance, Color(0xFF42A5F5)), // Blue
    Category("Khác", Icons.Default.QuestionMark, Color(0xFFBDBDBD)) // Gray
)