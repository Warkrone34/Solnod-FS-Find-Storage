package com.onyxera.fs.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Solnod Maritime & Logistics - Renk Paletleri ve Tema Tanımları.
 * Kurumsal kimlik standartlarına uygun, yüksek kontrastlı ve modern Material 3 renk uzayları.
 */

enum class AppThemeColor(val title: String, val previewColor: Color) {
    SOLNOD_MARINE("Solnod Lacivert", Color(0xFF002A54)),
    OCEAN_EMERALD("Okyanus Zümrüt", Color(0xFF004D40)),
    NORDIC_SLATE("Kuzey Çeliği", Color(0xFF1E3A5F)),
    PORT_SUNSET("Liman Gün Batımı", Color(0xFFC2410C)),
    DYNAMIC("Dinamik (Material You)", Color(0xFF386663))
}

enum class AppThemeMode(val title: String) {
    SYSTEM("Sistem Varsayılanı"),
    LIGHT("Açık Tema"),
    DARK("Koyu Tema")
}

// ----------------------------------------------------
// 1. SOLNOD MARINE PALETTE (VARSAYILAN KURUMSAL)
// ----------------------------------------------------
val SolnodMarineLight = lightColorScheme(
    primary = Color(0xFF002A54),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001A36),
    secondary = Color(0xFFC5A03A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDF8B),
    onSecondaryContainer = Color(0xFF251A00),
    tertiary = Color(0xFF006495),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCCE5FF),
    onTertiaryContainer = Color(0xFF001E31),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF071017),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF071017),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF74777F)
)

val SolnodMarineDark = darkColorScheme(
    primary = Color(0xFF689BD4),
    onPrimary = Color(0xFF001E3C),
    primaryContainer = Color(0xFF00376B),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFFE2C264),
    onSecondary = Color(0xFF3A2D00),
    secondaryContainer = Color(0xFF544200),
    onSecondaryContainer = Color(0xFFFFE08B),
    tertiary = Color(0xFF76B2DE),
    onTertiary = Color(0xFF002F4B),
    tertiaryContainer = Color(0xFF004971),
    onTertiaryContainer = Color(0xFFCCE5FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0B131C),
    onBackground = Color(0xFFE1E8EF),
    surface = Color(0xFF101C2B),
    onSurface = Color(0xFFE1E8EF),
    surfaceVariant = Color(0xFF233142),
    onSurfaceVariant = Color(0xFFBCC6D3),
    outline = Color(0xFF758599)
)

// ----------------------------------------------------
// 2. OCEAN EMERALD PALETTE (OKYANUS ZÜMRÜDÜ)
// ----------------------------------------------------
val OceanEmeraldLight = lightColorScheme(
    primary = Color(0xFF004D40),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF96F7E5),
    onPrimaryContainer = Color(0xFF00201A),
    secondary = Color(0xFFB68F3C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDF9E),
    onSecondaryContainer = Color(0xFF271900),
    tertiary = Color(0xFF006874),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF97F0FF),
    onTertiaryContainer = Color(0xFF001F24),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    background = Color(0xFFF6FAF8),
    onBackground = Color(0xFF0E1A17),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0E1A17),
    surfaceVariant = Color(0xFFDCE5E0),
    onSurfaceVariant = Color(0xFF404946),
    outline = Color(0xFF707976)
)

val OceanEmeraldDark = darkColorScheme(
    primary = Color(0xFF4DD0BA),
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF005144),
    onPrimaryContainer = Color(0xFF96F7E5),
    secondary = Color(0xFFD5AF56),
    onSecondary = Color(0xFF382900),
    secondaryContainer = Color(0xFF513D00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = Color(0xFF4FD8EC),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF004F58),
    onTertiaryContainer = Color(0xFF97F0FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    background = Color(0xFF0C1613),
    onBackground = Color(0xFFE0EAE5),
    surface = Color(0xFF12201D),
    onSurface = Color(0xFFE0EAE5),
    surfaceVariant = Color(0xFF243430),
    onSurfaceVariant = Color(0xFFBFC9C4),
    outline = Color(0xFF74847E)
)

// ----------------------------------------------------
// 3. NORDIC SLATE PALETTE (KUZEY ÇELİĞİ)
// ----------------------------------------------------
val NordicSlateLight = lightColorScheme(
    primary = Color(0xFF1E3A5F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD0E2FF),
    onPrimaryContainer = Color(0xFF001D35),
    secondary = Color(0xFF006684),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFBDE9FF),
    onSecondaryContainer = Color(0xFF001F2A),
    tertiary = Color(0xFF4B607C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E4FF),
    onTertiaryContainer = Color(0xFF041C35),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    background = Color(0xFFF7F9FB),
    onBackground = Color(0xFF11171C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF11171C),
    surfaceVariant = Color(0xFFDFE3EB),
    onSurfaceVariant = Color(0xFF42474E),
    outline = Color(0xFF73777F)
)

val NordicSlateDark = darkColorScheme(
    primary = Color(0xFF9BCAFF),
    onPrimary = Color(0xFF003257),
    primaryContainer = Color(0xFF00497B),
    onPrimaryContainer = Color(0xFFD0E2FF),
    secondary = Color(0xFF66D3FF),
    onSecondary = Color(0xFF003546),
    secondaryContainer = Color(0xFF004D64),
    onSecondaryContainer = Color(0xFFBDE9FF),
    tertiary = Color(0xFFB2C8EC),
    onTertiary = Color(0xFF1D324B),
    tertiaryContainer = Color(0xFF344863),
    onTertiaryContainer = Color(0xFFD3E4FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    background = Color(0xFF0F141A),
    onBackground = Color(0xFFDFE4EB),
    surface = Color(0xFF161D26),
    onSurface = Color(0xFFDFE4EB),
    surfaceVariant = Color(0xFF28323F),
    onSurfaceVariant = Color(0xFFBAC3CF),
    outline = Color(0xFF768392)
)

// ----------------------------------------------------
// 4. PORT SUNSET PALETTE (LİMAN GÜN BATIMI)
// ----------------------------------------------------
val PortSunsetLight = lightColorScheme(
    primary = Color(0xFF9C3A00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF350B00),
    secondary = Color(0xFF865300),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDDB3),
    onSecondaryContainer = Color(0xFF2B1700),
    tertiary = Color(0xFF6F5B40),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFADEBD),
    onTertiaryContainer = Color(0xFF271904),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    background = Color(0xFFFCF8F6),
    onBackground = Color(0xFF1C130E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C130E),
    surfaceVariant = Color(0xFFEDE0DB),
    onSurfaceVariant = Color(0xFF4E4440),
    outline = Color(0xFF80746F)
)

val PortSunsetDark = darkColorScheme(
    primary = Color(0xFFFFB59B),
    onPrimary = Color(0xFF551B00),
    primaryContainer = Color(0xFF782A00),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFFFFB952),
    onSecondary = Color(0xFF472A00),
    secondaryContainer = Color(0xFF653E00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = Color(0xFFDDC2A2),
    onTertiary = Color(0xFF3E2E16),
    tertiaryContainer = Color(0xFF56442B),
    onTertiaryContainer = Color(0xFFFADEBD),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    background = Color(0xFF160E0A),
    onBackground = Color(0xFFEBE0DB),
    surface = Color(0xFF221711),
    onSurface = Color(0xFFEBE0DB),
    surfaceVariant = Color(0xFF382922),
    onSurfaceVariant = Color(0xFFC7B8B1),
    outline = Color(0xFF8C7D76)
)