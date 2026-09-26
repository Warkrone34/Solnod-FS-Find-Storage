package com.onyxera.fs.util

/**
 * Güvenlik ve Veri Doğrulama Modülü.
 * Uygulamaya girilen tüm verileri, veritabanına inmeden önce temizler ve doğrular.
 * XSS, bellek taşırma (Buffer Overflow) ve mantıksal hataları engeller.
 */
object InputValidator {

    // Alan Bazlı Maksimum Karakter Sınırları
    const val MAX_SHIP_NAME_LENGTH = 50
    const val MAX_SHIP_DETAILS_LENGTH = 200
    const val MAX_MATERIAL_NAME_LENGTH = 100
    const val MAX_DESCRIPTION_LENGTH = 500
    const val MAX_DATE_LENGTH = 20
    const val MAX_SEARCH_LENGTH = 80
    const val MAX_NUMERIC_LENGTH = 10

    /**
     * Kullanıcıdan alınan metinsel ifadeleri temizler ve güvenli hale getirir.
     * @param input Kullanıcı metni.
     * @param maxLength Veritabanı şişmesini önlemek için karakter sınırı.
     * @return Temizlenmiş ve uzunluğu sınırlandırılmış metin.
     */
    fun sanitizeText(input: String?, maxLength: Int = 255): String {
        if (input.isNullOrBlank()) return ""

        // HTML etiketleri ve zararlı Script formatlarını basit Regex ile süzer
        val sanitized = input.replace(Regex("<.*?>"), "").trim()

        return if (sanitized.length > maxLength) {
            sanitized.substring(0, maxLength)
        } else {
            sanitized
        }
    }

    /**
     * Metin girişlerini belirli bir karakter limitiyle sınırlar.
     */
    fun limitText(input: String, maxLength: Int): String {
        return if (input.length > maxLength) input.take(maxLength) else input
    }

    /**
     * Sayısal alanlar (Fiyat, Kilo, Ebatlar) için kesin doğrulama ve filtreleme.
     * Harf, sembol, boşluk veya geçersiz karakterleri tamamen engeller.
     * Yalnızca rakamlara (0-9) ve en fazla bir adet ondalık ayraca (. veya ,) izin verir.
     * @param input Kullanıcının yazdığı veya yapıştırdığı ham metin.
     * @param maxLength Maksimum hane sayısı.
     * @return Sadece geçerli pozitif ondalıklı sayı dizgisi.
     */
    fun filterDecimalInput(input: String, maxLength: Int = MAX_NUMERIC_LENGTH): String {
        if (input.isBlank()) return ""

        val result = StringBuilder()
        var hasDecimalPoint = false

        for (ch in input) {
            if (ch.isDigit()) {
                if (result.length < maxLength) {
                    result.append(ch)
                }
            } else if ((ch == '.' || ch == ',') && !hasDecimalPoint) {
                if (result.length < maxLength) {
                    // İlk karakter olarak virgül/nokta girilirse başına "0" ekle (örn: ".5" -> "0.5")
                    if (result.isEmpty()) {
                        result.append("0")
                    }
                    result.append(ch)
                    hasDecimalPoint = true
                }
            }
            // Rakam veya ilk ondalık ayraç dışındaki tüm karakterler (harfler, semboller) filtrelenir.
        }

        return result.toString()
    }

    /**
     * Fiyat ve kilo gibi ondalıklı (Double) değerlerin kurallara uygunluğunu denetler.
     * @param value Kullanıcıdan gelen ham sayısal veri.
     * @return Mantıksal olarak doğruysa (pozitif ve reel bir sayı) kendisini, değilse 0.0 döner.
     */
    fun validatePositiveDouble(value: Double?): Double {
        if (value == null || value < 0.0 || value.isNaN() || value.isInfinite()) {
            return 0.0
        }
        return value
    }

    /**
     * GPS koordinatlarının (Enlem ve Boylam) dünya standartlarındaki geçerliliğini doğrular.
     */
    fun isValidCoordinate(lat: Double, lng: Double): Boolean {
        return (lat in -90.0..90.0) && (lng in -180.0..180.0)
    }
}