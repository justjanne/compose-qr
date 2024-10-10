package de.justjanne.composeqr

import kotlin.test.Test
import kotlin.test.assertEquals

class QrCanvasTest {
    @Test
    fun testCanvasPainter() {
        assertEquals(
            listOf(
                Pair(20, 20), Pair(19, 20), Pair(20, 19), Pair(19, 19),
                Pair(20, 18), Pair(19, 18), Pair(20, 17), Pair(19, 17),

                Pair(20, 16), Pair(19, 16), Pair(20, 15), Pair(19, 15),
                Pair(20, 14), Pair(19, 14), Pair(20, 13), Pair(19, 13),

                Pair(20, 12), Pair(19, 12), Pair(20, 11), Pair(19, 11),
                Pair(20, 10), Pair(19, 10), Pair(20, 9), Pair(19, 9),

                Pair(18, 9), Pair(17, 9), Pair(18, 10), Pair(17, 10),
                Pair(18, 11), Pair(17, 11), Pair(18, 12), Pair(17, 12),

                Pair(18, 13), Pair(17, 13), Pair(18, 14), Pair(17, 14),
                Pair(18, 15), Pair(17, 15), Pair(18, 16), Pair(17, 16),

                Pair(18, 17), Pair(17, 17), Pair(18, 18), Pair(17, 18),
                Pair(18, 19), Pair(17, 19), Pair(18, 20), Pair(17, 20),

                Pair(16, 20), Pair(15, 20), Pair(16, 19), Pair(15, 19),
                Pair(16, 18), Pair(15, 18), Pair(16, 17), Pair(15, 17),

                Pair(16, 16), Pair(15, 16), Pair(16, 15), Pair(15, 15),
                Pair(16, 14), Pair(15, 14), Pair(16, 13), Pair(15, 13),

                Pair(16, 12), Pair(15, 12), Pair(16, 11), Pair(15, 11),
                Pair(16, 10), Pair(15, 10), Pair(16, 9), Pair(15, 9),

                Pair(14, 9), Pair(13, 9), Pair(14, 10), Pair(13, 10),
                Pair(14, 11), Pair(13, 11), Pair(14, 12), Pair(13, 12),

                Pair(14, 13), Pair(13, 13), Pair(14, 14), Pair(13, 14),
                Pair(14, 15), Pair(13, 15), Pair(14, 16), Pair(13, 16),

                Pair(14, 17), Pair(13, 17), Pair(14, 18), Pair(13, 18),
                Pair(14, 19), Pair(13, 19), Pair(14, 20), Pair(13, 20),

                Pair(12, 20), Pair(11, 20), Pair(12, 19), Pair(11, 19),
                Pair(12, 18), Pair(11, 18), Pair(12, 17), Pair(11, 17),

                Pair(12, 16), Pair(11, 16), Pair(12, 15), Pair(11, 15),
                Pair(12, 14), Pair(11, 14), Pair(12, 13), Pair(11, 13),

                Pair(12, 12), Pair(11, 12), Pair(12, 11), Pair(11, 11),
                Pair(12, 10), Pair(11, 10), Pair(12, 9), Pair(11, 9),

                Pair(12, 8), Pair(11, 8), Pair(12, 7), Pair(11, 7),
                Pair(12, 5), Pair(11, 5), Pair(12, 4), Pair(11, 4),

                Pair(12, 3), Pair(11, 3), Pair(12, 2), Pair(11, 2),
                Pair(12, 1), Pair(11, 1), Pair(12, 0), Pair(11, 0),

                Pair(10, 0), Pair(9, 0), Pair(10, 1), Pair(9, 1),
                Pair(10, 2), Pair(9, 2), Pair(10, 3), Pair(9, 3),

                Pair(10, 4), Pair(9, 4), Pair(10, 5), Pair(9, 5),
                Pair(10, 7), Pair(9, 7), Pair(10, 8), Pair(9, 8),

                Pair(10, 9), Pair(9, 9), Pair(10, 10), Pair(9, 10),
                Pair(10, 11), Pair(9, 11), Pair(10, 12), Pair(9, 12),

                Pair(10, 13), Pair(9, 13), Pair(10, 14), Pair(9, 14),
                Pair(10, 15), Pair(9, 15), Pair(10, 16), Pair(9, 16),

                Pair(10, 17), Pair(9, 17), Pair(10, 18), Pair(9, 18),
                Pair(10, 19), Pair(9, 19), Pair(10, 20), Pair(9, 20),

                Pair(8, 12), Pair(7, 12), Pair(8, 11), Pair(7, 11),
                Pair(8, 10), Pair(7, 10), Pair(8, 9), Pair(7, 9),

                Pair(5, 9), Pair(4, 9), Pair(5, 10), Pair(4, 10),
                Pair(5, 11), Pair(4, 11), Pair(5, 12), Pair(4, 12),

                Pair(3, 12), Pair(2, 12), Pair(3, 11), Pair(2, 11),
                Pair(3, 10), Pair(2, 10), Pair(3, 9), Pair(2, 9),

                Pair(1, 9), Pair(0, 9), Pair(1, 10), Pair(0, 10),
                Pair(1, 11), Pair(0, 11), Pair(1, 12), Pair(0, 12),
            ),
            QrCanvas.dataPositions(1).toList()
        )
    }
}
