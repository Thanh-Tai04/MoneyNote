package com.example.moneynote.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.Woman
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.moneynote.ui.theme.CategoryBlue
import com.example.moneynote.ui.theme.CategoryGreen
import com.example.moneynote.ui.theme.CategoryPink
import com.example.moneynote.ui.theme.CategoryPurple
import com.example.moneynote.ui.theme.CategoryRed
import com.example.moneynote.ui.theme.CategoryYellow
import com.example.moneynote.ui.theme.MutedGray
// Data class (Dùng cho UI)
data class Category(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val expenseCategories = listOf(
    Category("Ăn uống", Icons.Default.Fastfood, CategoryRed),
    Category("Đi lại", Icons.Default.Train, CategoryBlue),
    Category("Tiền nhà", Icons.Default.House, CategoryPurple),
    Category("Quần áo", Icons.Default.Checkroom, CategoryPink),
    Category("Y tế", Icons.Default.LocalHospital, CategoryGreen),
    Category("Giáo dục", Icons.Default.School, CategoryYellow),
    Category("Tiền điện", Icons.Default.WaterDamage, CategoryBlue),
    Category("Mỹ phẩm", Icons.Default.Woman, CategoryPink),
    Category("Khác", Icons.Default.QuestionMark, MutedGray) // <-- ĐÃ THÊM LẠI
)
val incomeCategories = listOf(
    Category("Tiền lương", Icons.Default.Payments, CategoryGreen),
    Category("Tiết kiệm", Icons.Default.Savings, CategoryYellow),
    Category("Thưởng", Icons.Default.CardGiftcard, CategoryBlue),
    Category("Thu nhập khác", Icons.Default.AccountBalance, CategoryPurple),
    Category("Khác", Icons.Default.QuestionMark, MutedGray) // <-- ĐÃ THÊM LẠI
)