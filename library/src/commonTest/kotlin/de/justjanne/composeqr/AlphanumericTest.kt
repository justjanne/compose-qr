package de.justjanne.composeqr

import kotlin.test.Test
import kotlin.test.assertEquals

class AlphanumericTest {
    @Test
    fun testAlphanumericChunk() {
        assertEquals(
            booleanArrayOf(
                0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 1,
                0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0,
                1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1,
                0, 0, 0, 0, 1, 0,
            ).toList(),
            QrCodeBuilder.Chunk.Alphanumeric("AC-42").encode(1).toList()
        )
    }
}
