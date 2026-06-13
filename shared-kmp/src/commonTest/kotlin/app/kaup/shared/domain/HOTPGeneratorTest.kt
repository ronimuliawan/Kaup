package app.kaup.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HOTPGeneratorTest {

    // Test Vector Secret from RFC 4226: "12345678901234567890"
    private val rfcSecret = "12345678901234567890".encodeToByteArray()

    @Test
    fun `generates exact RFC 4226 test vectors`() {
        // RFC 4226 Appendix D expected values
        val expectedCodes = listOf(
            "755224", "287082", "359152", "969429", 
            "338314", "254676", "287922", "162583", 
            "399871", "520489"
        )

        for (i in expectedCodes.indices) {
            val code = HOTPGenerator.generateCode(rfcSecret, i.toLong(), digits = 6)
            assertEquals(expectedCodes[i], code, "Mismatch at counter $i")
        }
    }

    @Test
    fun `validates exact code on current counter`() {
        val input = "755224" // Counter 0
        val matchedCounter = HOTPGenerator.validateCode(rfcSecret, 0L, input, lookAheadWindow = 5)
        assertEquals(0L, matchedCounter)
    }

    @Test
    fun `validates drifted code within window`() {
        // Let's say device is at counter 0, but manager generated code for counter 4 ("338314")
        val input = "338314"
        val matchedCounter = HOTPGenerator.validateCode(rfcSecret, 0L, input, lookAheadWindow = 5)
        assertEquals(4L, matchedCounter)
    }

    @Test
    fun `rejects drifted code outside window`() {
        // Device at counter 0, code is for counter 9 ("520489")
        val input = "520489"
        val matchedCounter = HOTPGenerator.validateCode(rfcSecret, 0L, input, lookAheadWindow = 5)
        assertNull(matchedCounter)
    }
    
    @Test
    fun `rejects consumed code`() {
        // If a code was consumed, the counter has moved forward.
        // Device is at counter 5, input is code for counter 4 ("338314")
        val input = "338314"
        val matchedCounter = HOTPGenerator.validateCode(rfcSecret, 5L, input, lookAheadWindow = 5)
        assertNull(matchedCounter)
    }
}
