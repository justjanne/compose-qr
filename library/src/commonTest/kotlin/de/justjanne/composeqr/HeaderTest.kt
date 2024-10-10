package de.justjanne.composeqr

import kotlin.test.Test
import kotlin.test.assertEquals

class HeaderTest {
    @Test
    fun testVersionEncoding7() {
        assertEquals(
            booleanArrayOf(
                0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0,
            ).toList(),
            QrCode.Header(
                QrCode.ErrorCorrectionLevel.L,
                7,
                QrCode.MaskPattern.PATTERN_6
            ).encodeVersion().toList()
        )
    }

    @Test
    fun testVersionEncoding23() {
        assertEquals(
            booleanArrayOf(
                0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0,
            ).toList(),
            QrCode.Header(
                QrCode.ErrorCorrectionLevel.L,
                23,
                QrCode.MaskPattern.PATTERN_6
            ).encodeVersion().toList()
        )
    }
}
