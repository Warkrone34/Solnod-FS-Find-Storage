package com.onyxera.fs

import com.onyxera.fs.util.InputValidator
import com.onyxera.fs.util.SolnodConstants
import org.junit.Assert.*
import org.junit.Test

/**
 * Solnod F&S Temel Birim Testleri.
 */
class ExampleUnitTest {

    @Test
    fun testCurrencySymbols() {
        assertEquals("₺", SolnodConstants.getCurrencySymbol("TL"))
        assertEquals("$", SolnodConstants.getCurrencySymbol("USD"))
        assertEquals("€", SolnodConstants.getCurrencySymbol("EUR"))
        assertEquals("£", SolnodConstants.getCurrencySymbol("GBP"))
        assertEquals("¥", SolnodConstants.getCurrencySymbol("JPY"))
        assertEquals("₺", SolnodConstants.getCurrencySymbol(null))
        assertEquals("₺", SolnodConstants.getCurrencySymbol(""))
    }

    @Test
    fun testSanitizeText() {
        val dirtyInput = "<script>alert('test')</script>Deniz Malzemesi<b>Acil</b>"
        val cleanOutput = InputValidator.sanitizeText(dirtyInput)
        assertEquals("alert('test')Deniz MalzemesiAcil", cleanOutput)
    }

    @Test
    fun testValidatePositiveDouble() {
        assertEquals(150.5, InputValidator.validatePositiveDouble(150.5), 0.001)
        assertEquals(0.0, InputValidator.validatePositiveDouble(-25.0), 0.001)
        assertEquals(0.0, InputValidator.validatePositiveDouble(null), 0.001)
        assertEquals(0.0, InputValidator.validatePositiveDouble(Double.NaN), 0.001)
    }

    @Test
    fun testShipEntityCreation() {
        val ship = com.onyxera.fs.data.ShipEntity(
            id = 1,
            name = "Test Gemi",
            details = "IMO: 1234567, Çağrı İşareti: TC01"
        )
        assertEquals("Test Gemi", ship.name)
        assertEquals("IMO: 1234567, Çağrı İşareti: TC01", ship.details)
        assertTrue(ship.createdAt > 0L)
    }

    @Test
    fun testFilterDecimalInput() {
        // Harfleri ve geçersiz karakterleri tamamen temizler
        assertEquals("123", InputValidator.filterDecimalInput("123abc"))
        assertEquals("12345", InputValidator.filterDecimalInput("12a3b4c5"))
        assertEquals("", InputValidator.filterDecimalInput("sadece yazi"))

        // Geçerli ondalıklı sayıları korur
        assertEquals("150.75", InputValidator.filterDecimalInput("150.75"))
        assertEquals("150,75", InputValidator.filterDecimalInput("150,75"))

        // Birden fazla ondalık ayracı engeller
        assertEquals("12.3456", InputValidator.filterDecimalInput("12.34.56"))

        // Baştaki virgül/noktaya otomatik sıfır ekler (.5 -> 0.5)
        assertEquals("0.5", InputValidator.filterDecimalInput(".5"))

        // Karakter sınırını uygular
        assertEquals("1234567890", InputValidator.filterDecimalInput("123456789099999", 10))
    }

    @Test
    fun testLimitText() {
        val longText = "Bu metin çok uzun bir açıklama içermektedir ve belirli bir karakterle sınırlandırılmalıdır."
        val limited = InputValidator.limitText(longText, 20)
        assertEquals(20, limited.length)
        assertEquals(longText.take(20), limited)

        val shortText = "Kısa metin"
        assertEquals(shortText, InputValidator.limitText(shortText, 50))
    }

    @Test
    fun testSecurityUtilsSha256() {
        // Standart NIST SHA-256 boş metin test vektörü
        val emptyHash = com.onyxera.fs.util.SecurityUtils.sha256("")
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", emptyHash)

        // Bilinen "hello" kelimesi SHA-256 test vektörü
        val helloHash = com.onyxera.fs.util.SecurityUtils.sha256("hello")
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", helloHash)

        // Geçici dosya SHA-256 bütünlük doğrulaması
        val tempFile = java.io.File.createTempFile("solnod_test", ".txt")
        tempFile.writeText("hello", Charsets.UTF_8)
        val fileHash = com.onyxera.fs.util.SecurityUtils.sha256(tempFile)
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", fileHash)
        tempFile.delete()
    }
}