package com.example.moneynote.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moneynote.ui.theme.CategoryBlue
import com.example.moneynote.ui.theme.CategoryGreen
import com.example.moneynote.ui.theme.CategoryPink
import com.example.moneynote.ui.theme.CategoryPurple
import com.example.moneynote.ui.theme.CategoryRed
import com.example.moneynote.ui.theme.CategoryYellow
import com.example.moneynote.ui.theme.MoneyNoteTheme
import androidx.compose.ui.tooling.preview.Preview

// Danh sách Icon (Tạm thời)
val mockIcons = listOf(
    Icons.Default.Fastfood,
    Icons.Default.Train,
    Icons.Default.ShoppingCart,
    Icons.Default.LocalAtm,
    Icons.Default.Savings,
    Icons.Default.CardGiftcard,
    Icons.Default.Pets,
    Icons.Default.Flight,
    Icons.Default.BusinessCenter,
    Icons.Default.LocalMall
)

// Danh sách Màu (Tạm thời)
val mockColors = listOf(
    CategoryRed, CategoryYellow, CategoryGreen, CategoryBlue, CategoryPurple, CategoryPink,
    Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5),
    Color(0xFF009688), Color(0xFF4CAF50), Color(0xFFCDDC39), Color(0xFFFFEB3B),
    Color(0xFFFF9800), Color(0xFF795548), Color(0xFF607D8B)
)

/**
 * Màn hình Tạo mới Danh mục (image_8c5c86.png)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryScreen(navController: NavHostController) {

    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf<ImageVector?>(null) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo mới") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // (Tạm thời) Chỉ quay lại
                    // (Tương lai) Sẽ lưu vào ViewModel/DB
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("Lưu")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Tên
            Text("Tên", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Vui lòng nhập vào tên đề mục") },
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Biểu tượng
            Text("Biểu tượng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(60.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mockIcons) { icon ->
                    IconSelector(
                        icon = icon,
                        isSelected = selectedIcon == icon,
                        onClick = { selectedIcon = icon }
                    )
                }
            }

            // 3. Màu sắc
            Text("Màu sắc", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(50.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mockColors) { color ->
                    ColorSelector(
                        color = color,
                        isSelected = selectedColor == color,
                        onClick = { selectedColor = color }
                    )
                }
            }
        }
    }
}

// Composable cho một Ô chọn Icon
@Composable
fun IconSelector(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(60.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Composable cho một Ô chọn Màu
@Composable
fun ColorSelector(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Đã chọn",
                tint = MaterialTheme.colorScheme.onPrimary // (Màu của dấu tick)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateCategoryScreenPreview() {
    MoneyNoteTheme(darkTheme = true) {
        CreateCategoryScreen(navController = rememberNavController())
    }
}