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
}