package de.justjanne.composeqr

internal class QrCanvas(
    val size: Int,
    val data: BooleanArray = BooleanArray(size * size)
) {
    fun get(x: Int, y: Int) = data[y * size + x]
    fun set(x: Int, y: Int, value: Boolean) {
        data[y * size + x] = value
    }

    fun set(x: Int, y: Int, source: QrCanvas) {
        for (i in 0 until source.size) {
            for (j in 0 until source.size) {
                if (x + i in 0 until size && y + j in 0 until size) {
                    set(x + i, y + j, source.get(i, j))
                }
            }
        }
    }

    private fun set(
        positions: Sequence<Pair<Int, Int>>,
        data: Bitstream,
        mask: QrCode.MaskPattern? = null,
    ) {
        for ((position, bit) in positions.zip(data)) {
            val (x, y) = position
            if (mask != null) {
                set(x, y, bit xor mask(mask, x, y))
            } else {
                set(x, y, bit)
            }
        }
    }

    private fun render(code: QrCode) {
        for (x in 0 until size) {
            set(x, 6, x % 2 == 0)
        }
        for (y in 0 until size) {
            set(6, y, y % 2 == 0)
        }
        for ((x, y) in actualAlignmentPatternPositions(code.header.version)) {
            set(x - 2, y - 2, mark2)
        }
        set(-1, -1, mark1)
        set(-1, size - 8, mark1)
        set(size - 8, -1, mark1)
        set(formatPositions1(), code.header.encodeFormat())
        set(formatPositions2(code.header.version), code.header.encodeFormat())
        set(versionInfoPositions1(code.header.version), code.header.encodeVersion())
        set(versionInfoPositions2(code.header.version), code.header.encodeVersion())
        set(8, size - 8, true)
        set(dataPositions(code.header.version), code.data, code.header.maskPattern)
    }

    companion object {
        private fun canvasSize(version: Int) = 21 + (version - 1) * 4

        private val mark1 = QrCanvas(
            9,
            booleanArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 1, 1, 1, 1, 1, 1, 1, 0,
                0, 1, 0, 0, 0, 0, 0, 1, 0,
                0, 1, 0, 1, 1, 1, 0, 1, 0,
                0, 1, 0, 1, 1, 1, 0, 1, 0,
                0, 1, 0, 1, 1, 1, 0, 1, 0,
                0, 1, 0, 0, 0, 0, 0, 1, 0,
                0, 1, 1, 1, 1, 1, 1, 1, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
            )
        )

        private val mark2 = QrCanvas(
            5,
            booleanArrayOf(
                1, 1, 1, 1, 1,
                1, 0, 0, 0, 1,
                1, 0, 1, 0, 1,
                1, 0, 0, 0, 1,
                1, 1, 1, 1, 1,
            )
        )

        fun from(code: QrCode): QrCanvas = QrCanvas(canvasSize(code.header.version)).apply { render(code) }

        private fun Int.plusMinus(range: Int): IntRange = this - range..this + range

        private fun invalidPosition(version: Int, x: Int, y: Int): Boolean {
            val size = canvasSize(version)
            val timingPattern = x == 6 || y == 6
            val formatPatternTopLeft = x < 9 && y < 9
            val formatPatternTopRight = x >= size - 8 && y < 9
            val formatPatternBottomLeft = x < 9 && y >= size - 8
            val formatPattern = formatPatternTopLeft || formatPatternTopRight || formatPatternBottomLeft
            val alignmentPatterns = actualAlignmentPatternPositions(version)
            val alignmentPattern = alignmentPatterns.any { (i, j) ->
                x in i.plusMinus(2) && y in j.plusMinus(2)
            }
            val versionInformationTopRight = version >= 7 && x >= size - 8 - 3 && y < 6
            val versionInformationBottomLeft = version >= 7 && x < 6 && y >= size - 8 - 3
            val versionInformation = versionInformationTopRight || versionInformationBottomLeft
            return formatPattern || timingPattern || alignmentPattern || versionInformation
        }

        enum class Direction(val value: Int) {
            Up(-1),
            Down(1);

            fun invert() = when (this) {
                Up -> Down
                Down -> Up
            }
        }

        internal fun dataPositions(version: Int): Sequence<Pair<Int, Int>> = sequence {
            val size = canvasSize(version)
            var x = size - 1
            var y = size - 1
            var direction = Direction.Up
            for (i in 0 until QrCodeBuilder.totalCodewords(version) * 8) {
                require(x >= 0)
                require(y >= 0)
                yield(Pair(x, y))
                val columnAlignment = if (x < 6) 1 else 0
                fun next() {
                    if (x % 2 == columnAlignment) {
                        x -= 1
                    } else if ((y + direction.value) in 0 until size) {
                        x += 1
                        y += direction.value
                    } else {
                        x -= 1
                        direction = direction.invert()
                    }
                }
                do {
                    next()
                } while (invalidPosition(version, x, y))
            }
        }

        private fun formatPositions1(): Sequence<Pair<Int, Int>> = sequence {
            for (i in 0..8) {
                if (i != 6) {
                    yield(Pair(8, i))
                }
            }
            for (i in 7 downTo 0) {
                if (i != 6) {
                    yield(Pair(i, 8))
                }
            }
        }

        private fun formatPositions2(version: Int): Sequence<Pair<Int, Int>> = sequence {
            val size = canvasSize(version)
            for (i in 1..8) {
                yield(Pair(size - i, 8))
            }
            for (i in 7 downTo 1) {
                yield(Pair(8, size - i))
            }
        }

        private fun versionInfoPositions1(version: Int): Sequence<Pair<Int, Int>> = sequence {
            if (version >= 7) {
                val size = canvasSize(version)
                for (y in 0..5) {
                    for (x in 0..2) {
                        yield(Pair(size - 11 + x, y))
                    }
                }
            }
        }

        private fun versionInfoPositions2(version: Int): Sequence<Pair<Int, Int>> = sequence {
            if (version >= 7) {
                val size = canvasSize(version)
                for (x in 0..5) {
                    for (y in 0..2) {
                        yield(Pair(x, size - 11 + y))
                    }
                }
            }
        }

        private fun mask(maskPattern: QrCode.MaskPattern, x: Int, y: Int) = when (maskPattern) {
            QrCode.MaskPattern.PATTERN_0 -> (y + x) % 2 == 0
            QrCode.MaskPattern.PATTERN_1 -> y % 2 == 0
            QrCode.MaskPattern.PATTERN_2 -> x % 3 == 0
            QrCode.MaskPattern.PATTERN_3 -> (y + x) % 3 == 0
            QrCode.MaskPattern.PATTERN_4 -> ((y / 2) + (x / 3)) % 2 == 0
            QrCode.MaskPattern.PATTERN_5 -> (y * x) % 2 + (y * x) % 3 == 0
            QrCode.MaskPattern.PATTERN_6 -> ((y * x) % 2 + (y * x) % 3) % 2 == 0
            QrCode.MaskPattern.PATTERN_7 -> ((y + x) % 2 + (y * x) % 3) % 2 == 0
        }

        private fun actualAlignmentPatternPositions(version: Int): Sequence<Pair<Int, Int>> = sequence {
            val alignmentPatterns = getAlignmentPatternPositions(version)
            for (y in alignmentPatterns.indices) {
                for (x in alignmentPatterns.indices) {
                    val overlapsTopLeft = x == 0 && y == 0
                    val overlapsTopRight = x == alignmentPatterns.size - 1 && y == 0
                    val overlapsBottomLeft = x == 0 && y == alignmentPatterns.size - 1
                    if (!overlapsTopLeft && !overlapsTopRight && !overlapsBottomLeft) {
                        yield(Pair(alignmentPatterns[x], alignmentPatterns[y]))
                    }
                }
            }
        }

        private fun getAlignmentPatternPositions(version: Int): IntArray {
            if (version == 1) return intArrayOf()
            else {
                val numAlign: Int = version / 7 + 2
                val step: Int = (version * 8 + numAlign * 3 + 5) / (numAlign * 4 - 4) * 2
                val result = IntArray(numAlign)
                result[0] = 6
                var i = result.size - 1
                var pos: Int = canvasSize(version) - 7
                while (i >= 1) {
                    result[i] = pos
                    i--
                    pos -= step
                }
                return result
            }
        }
    }
}
