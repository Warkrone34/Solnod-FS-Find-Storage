package com.onyxera.fs.util

/**
 * Güvenlik ve Veri Doğrulama Modülü.
 * Uygulamaya girilen tüm verileri, veritabanına inmeden önce temizler ve doğrular.
 * XSS, bellek taşırma (Buffer Overflow) ve mantıksal hataları engeller.
 */
object InputValidator {

    /**
     * Kullanıcıdan alınan metinsel ifadeleri temizler ve güvenli hale getirir.
     * * @param input Kullanıcı metni.
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
     * Fiyat ve kilo gibi ondalıklı (Double) değerlerin kurallara uygunluğunu denetler.
     * * @param value Kullanıcıdan gelen ham sayısal veri.
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
     * * @param lat Enlem değeri (-90 ile 90 derece).
     * @param lng Boylam değeri (-180 ile 180 derece).
     * @return Sensör hatası veya manipülasyon yoksa true döner.
     */
    fun isValidCoordinate(lat: Double, lng: Double): Boolean {
        return (lat in -90.0..90.0) && (lng in -180.0..180.0)
    }
}