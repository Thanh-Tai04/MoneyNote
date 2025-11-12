package com.example.moneynote.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneynote.MoneyNoteApplication
import com.example.moneynote.ui.screens.AddTransactionScreen
import com.example.moneynote.ui.screens.BudgetScreen
import com.example.moneynote.ui.screens.CalendarScreen
import com.example.moneynote.ui.screens.CategoryManagementScreen
import com.example.moneynote.ui.screens.CreateCategoryScreen
import com.example.moneynote.ui.screens.ReportScreen
import com.example.moneynote.ui.theme.MoneyNoteTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyNoteTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

// #### CẤU TRÚC MÀN HÌNH CHÍNH (MainScreen) ####
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    // 1. Thêm State cho Snackbar và CoroutineScope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            // Chỉ hiển thị BottomNav nếu không ở màn hình "Chỉnh sửa"
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == BottomNavItem.Add.route ||
                currentRoute == BottomNavItem.Calendar.route ||
                currentRoute == BottomNavItem.Report.route ||
                currentRoute == BottomNavItem.Budget.route
            ) {
                BottomNavigationBar(navController = navController)
            }
        },
        // 2. Thêm SnackbarHost vào Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        // 3. Truyền padding và các state mới vào NavHost
        NavigationGraph(
            navController = navController,
            innerPadding = innerPadding,
            snackbarHostState = snackbarHostState,
            scope = scope
        )
    }
}

// (Lưu ý: Icon cho "Nhập vào" đã được đổi thành Edit)
sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Add : BottomNavItem("add", Icons.Default.Edit, "Nhập vào")
    object Calendar : BottomNavItem("calendar", Icons.Default.CalendarMonth, "Lịch")
    object Report : BottomNavItem("report", Icons.Default.PieChart, "Báo cáo")
    object Budget : BottomNavItem("budget", Icons.Default.Addchart, "Ngân sách")
}
val bottomNavItems = listOf(
    BottomNavItem.Add,
    BottomNavItem.Calendar,
    BottomNavItem.Report,
    BottomNavItem.Budget
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}


// #### ĐỒ THỊ ĐIỀU HƯỚNG (NAVIGATION GRAPH) ####
@Composable
fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    // 4. Nhận SnackbarHostState và Scope
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
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
        composable(BottomNavItem.Add.route) {
            val addViewModel: AddTransactionViewModel = viewModel(factory = addTransactionFactory)
            // 5. Truyền NavController, Scope và State cho Màn hình 1
            AddTransactionScreen(
                viewModel = addViewModel,
                navController = navController,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
        composable(BottomNavItem.Calendar.route) {
            val calendarViewModel: CalendarViewModel = viewModel(factory = calendarFactory)
            CalendarScreen(viewModel = calendarViewModel)
        }
        composable(BottomNavItem.Report.route) {
            val reportViewModel: ReportViewModel = viewModel(factory = reportFactory)
            ReportScreen(viewModel = reportViewModel)
        }
        composable(BottomNavItem.Budget.route) {
            val budgetViewModel: BudgetViewModel = viewModel(factory = budgetFactory)
            BudgetScreen(viewModel = budgetViewModel)
        }

        // 6. THÊM 2 ROUTE (ĐƯỜNG DẪN) MỚI
        composable("category_management") {
            // Màn hình này cần NavController để quay lại (Back)
            // và để điều hướng đến "create_category"
            CategoryManagementScreen(navController = navController)
        }
        composable("create_category") {
            // Màn hình này cần NavController để quay lại (Back)
            CreateCategoryScreen(navController = navController)
        }
    }
}