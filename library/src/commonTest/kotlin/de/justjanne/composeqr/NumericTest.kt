package de.justjanne.composeqr

import kotlin.test.Test
import kotlin.test.assertEquals

class NumericTest {
    @Test
    fun testNumericChunk() {
        assertEquals(
            booleanArrayOf(
                0, 0, 0, 1,
                0, 0, 0, 0, 0, 0, 1, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 1, 0, 0,
                0, 1, 0, 1, 0, 1, 1, 0, 0, 1,
                1, 0, 0, 0, 0, 1, 1
            ).toList(),
            QrCodeBuilder.Chunk.Numeric(listOf(0, 1, 2, 3, 4, 5, 6, 7)).encode(1).toList()
        )
    }

    @Test
    fun testNumericCodeL() {
        assertEquals(
            listOf<UByte>(
                16u, 32u, 12u, 86u, 97u, 128u,
                236u, 17u, 236u, 17u, 236u, 17u, 236u, 17u, 236u, 17u, 236u, 17u, 236u,
            ),
            QrCodeBuilder(
                errorLevel = QrCode.ErrorCorrectionLevel.L,
                maskPattern = QrCode.MaskPattern.PATTERN_6,
                chunks = listOf(
                    QrCodeBuilder.Chunk.Numeric(listOf(0, 1, 2, 3, 4, 5, 6, 7)),
                    QrCodeBuilder.Chunk.Terminator,
                ),
            ).data(1).toList()
        )
    }

    @Test
    fun testNumericCodeH() {
        assertEquals(
            listOf<UByte>(
                16u, 32u, 12u, 86u, 97u, 128u,
                236u, 17u, 236u,
            ),
            QrCodeBuilder(
                errorLevel = QrCode.ErrorCorrectionLevel.H,
                maskPattern = QrCode.MaskPattern.PATTERN_6,
                chunks = listOf(
                    QrCodeBuilder.Chunk.Numeric(listOf(0, 1, 2, 3, 4, 5, 6, 7)),
                    QrCodeBuilder.Chunk.Terminator,
                ),
            ).data(1).toList(),
        )
    }

    @Test
    fun testNumericCodeEcc() {
        assertEquals(
            listOf<UByte>(
                16u, 32u, 12u, 86u, 97u, 128u,
                236u, 17u, 236u, 17u, 236u, 17u, 236u, 17u, 236u, 17u, 236u, 17u, 236u,
                83u, 85u, 151u, 103u, 16u, 5u, 132u,
            ),
            QrCodeBuilder(
                errorLevel = QrCode.ErrorCorrectionLevel.L,
                maskPattern = QrCode.MaskPattern.PATTERN_6,
                chunks = listOf(
                    QrCodeBuilder.Chunk.Numeric(listOf(0, 1, 2, 3, 4, 5, 6, 7)),
                    QrCodeBuilder.Chunk.Terminator,
                ),
            ).codewords(1).toList()
        )
    }
}
