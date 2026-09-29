package com.onyxera.fs.util

object SolnodConstants {
    const val APP_NAME = "Solnod F&S"
    const val COMPANY_NAME = "Solnod Maritime & Logistics Group"
    const val PREFS_NAME = "solnod_fs_settings_prefs"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_THEME_COLOR = "theme_color"
    const val KEY_SECURE_SCREEN = "key_secure_screen"

    // Desteklenen Para Birimleri
    val CURRENCIES = listOf("TL", "USD", "EUR", "GBP", "JPY", "Dinar")

    fun getCurrencySymbol(currencyCode: String?): String {
        return when (currencyCode?.uppercase()) {
            "TL" -> "₺"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "DINAR" -> "د.ك"
            null, "" -> "₺"
            else -> currencyCode
        }
    }
}
