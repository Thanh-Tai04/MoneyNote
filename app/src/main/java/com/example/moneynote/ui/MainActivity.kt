package com.example.moneynote.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneynote.MoneyNoteApplication
// THÊM CÁC IMPORT CHO CÁC MÀN HÌNH ĐÃ TÁCH RA
import com.example.moneynote.ui.screens.AddTransactionScreen
import com.example.moneynote.ui.screens.BudgetScreen
import com.example.moneynote.ui.screens.CalendarScreen
import com.example.moneynote.ui.screens.ReportScreen
// ---
import com.example.moneynote.ui.theme.MoneyNoteTheme

// #### MAIN ACTIVITY (ĐÃ TÁI CẤU TRÚC) ####
// TẤT CẢ GIAO DIỆN VÀ DANH MỤC ĐÃ ĐƯỢC XÓA KHỎI TỆP NÀY
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyNoteTheme {
                MainScreen()
            }
        }
    }
}

// #### CẤU TRÚC MÀN HÌNH CHÍNH (MainScreen) ####
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        // Gọi NavigationGraph (đã được cập nhật)
        NavigationGraph(navController = navController, innerPadding = innerPadding)
    }
}

// #### ĐIỀU HƯỚNG DƯỚI (BOTTOM NAVIGATION) ####
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Add : BottomNavItem("add", "Nhập vào", Icons.Outlined.Edit, Icons.Default.Edit)
    data object Calendar : BottomNavItem("calendar", "Lịch", Icons.Outlined.CalendarMonth, Icons.Default.CalendarMonth)
    data object Report : BottomNavItem("report", "Báo cáo", Icons.Outlined.BarChart, Icons.Default.BarChart)
    data object Budget : BottomNavItem("budget", "Ngân sách", Icons.Outlined.Payments, Icons.Default.Payments)
}

val bottomNavItems = listOf(BottomNavItem.Add, BottomNavItem.Calendar, BottomNavItem.Report, BottomNavItem.Budget)

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = if (currentRoute == item.route) item.selectedIcon else item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) }
            )
        }
    }
}

// #### ĐỒ THỊ ĐIỀU HƯỚNG (NAVIGATION GRAPH) ####
// (ĐÃ CẬP NHẬT ĐỂ KHỞI TẠO TẤT CẢ VIEWMODEL VÀ GỌI ĐÚNG MÀN HÌNH)
@Composable
fun NavigationGraph(navController: NavHostController, innerPadding: PaddingValues) {

    // Lấy Repository từ Application
    val repository = (navController.context.applicationContext as MoneyNoteApplication).repository

    // Tạo Factory cho các ViewModel
    val addTransactionFactory = AddTransactionViewModelFactory(repository)
    val calendarFactory = CalendarViewModelFactory(repository)
    val reportFactory = ReportViewModelFactory(repository)
    val budgetFactory = BudgetViewModelFactory(repository)

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Add.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        // SỬ DỤNG CÁC MÀN HÌNH ĐÃ TÁCH RA TỪ package .ui.screens
        composable(BottomNavItem.Add.route) {
            val addViewModel: AddTransactionViewModel = viewModel(factory = addTransactionFactory)
            AddTransactionScreen(viewModel = addViewModel) // <-- TỪ ui.screens
        }
        composable(BottomNavItem.Calendar.route) {
            val calendarViewModel: CalendarViewModel = viewModel(factory = calendarFactory)
            CalendarScreen(viewModel = calendarViewModel) // <-- TỪ ui.screens
        }
        composable(BottomNavItem.Report.route) {
            val reportViewModel: ReportViewModel = viewModel(factory = reportFactory)
            ReportScreen(viewModel = reportViewModel) // <-- TỪ ui.screens
        }
        composable(BottomNavItem.Budget.route) {
            val budgetViewModel: BudgetViewModel = viewModel(factory = budgetFactory)
            BudgetScreen(viewModel = budgetViewModel) // <-- TỪ ui.screens
        }
    }
}