package com.example.moneynote.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color
private val DarkColorScheme = darkColorScheme(
    primary = OceanBlue,       // Màu chính (nút, tab, icon được chọn)
    secondary = AquaGreen,     // Màu nhấn
    background = NightBlue,    // Màu nền ứng dụng
    surface = CardNight,       // Màu của Card, BottomBar, Dialog
    error = NegativeRed,       // Màu cho lỗi, chi tiêu

    onPrimary = SoftWhite,     // Chữ trên nền primary
    onSecondary = SoftWhite,   // Chữ trên nền secondary
    onBackground = SoftWhite,  // Chữ trên nền background
    onSurface = SoftWhite,     // Chữ trên nền surface
    onError = SoftWhite,       // Chữ trên nền error

    // Màu chữ/icon phụ
    onSurfaceVariant = MutedGray,
    // Màu viền
    outline = MutedGray
)

// Chúng ta không dùng LightTheme, nhưng vẫn giữ nó
private val LightColorScheme = lightColorScheme(
    primary = OceanBlue,
    secondary = AquaGreen,
    background = Color(0xFFFFFFFF)
)

@Composable
fun MoneyNoteTheme(
    // Buộc ứng dụng luôn ở chế độ tối (dark theme)
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // LUÔN DÙNG DarkColorScheme của chúng ta
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Màu Status bar
            window.navigationBarColor = colorScheme.surface.toArgb() // Màu Navigation bar (dưới cùng)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, // Áp dụng bảng màu đã chọn
        typography = Typography,
        content = content
    )
}