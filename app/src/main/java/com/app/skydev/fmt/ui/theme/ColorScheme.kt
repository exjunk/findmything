package com.app.skydev.fmt.ui.theme

import androidx.compose.ui.graphics.Color

enum class ColorScheme(
    val primaryColor: Color,
    val secondaryColor: Color,
    val tertiaryColor: Color
) {
    DEFAULT(Color(0xFF6200EE), Color(0xFF03DAC5), Color(0xFF3700B3)),
    GREEN(Color(0xFF1B5E20), Color(0xFF4CAF50), Color(0xFF81C784)),
    BLUE(Color(0xFF0D47A1), Color(0xFF2196F3), Color(0xFF64B5F6)),
    RED(Color(0xFFB71C1C), Color(0xFFF44336), Color(0xFFE57373))
}