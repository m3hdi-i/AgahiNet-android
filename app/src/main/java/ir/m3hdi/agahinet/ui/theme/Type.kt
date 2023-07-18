package ir.m3hdi.agahinet.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import ir.m3hdi.agahinet.R

private val appFont = FontFamily(
    Font(R.font.vazirmatn_regular),
)

private val defaultTypography = Typography()
val appTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = appFont),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = appFont),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = appFont),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = appFont),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = appFont),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = appFont),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = appFont),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = appFont),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = appFont),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = appFont),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = appFont),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = appFont),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = appFont),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = appFont),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = appFont)
)