package de.justjanne.composeqr


@OptIn(ExperimentalUnsignedTypes::class)
data class QrCodeBuilder(
    val errorLevel: QrCode.ErrorCorrectionLevel,
    val maskPattern: QrCode.MaskPattern,
    val chunks: List<Chunk>,
) {
    internal fun data(version: Int): CodewordStream =
        chunks.asSequence()
            .flatMap { it.encode(version) }
            .plus(bitstreamPadding)
            .windowed(8, 8, false)
            .map { it.toUByte() }
            .plus(paddingStream)
            .take(dataCodewords(version, errorLevel))

    fun codewords(version: Int): CodewordStream = sequence {
        val blockCount: Int = eccBlocks(version, errorLevel)
        val eccPerBlock: Int = eccPerBlock(version, errorLevel)
        val dataCodewords = dataCodewords(version, errorLevel)
        val shortBlockCount = blockCount - dataCodewords % blockCount
        val blockSize = dataCodewords / blockCount

        val blockSizes = sequence {
            for (i in 0 until blockCount) {
                if (i < shortBlockCount) {
                    yield(blockSize)
                } else {
                    yield(blockSize + 1)
                }
            }
        }

        val data = data(version).iterator()
        val rawBlocks = blockSizes.map {
            data.take(it).toUByteArray()
        }.toList()

        for (i in 0 until blockSize + 1) {
            for (block in rawBlocks) {
                if (block.size > i) {
                    yield(block[i])
                }
            }
        }


        val divisor: UByteArray = ReedSolomon.computeDivisor(eccPerBlock)
        val eccBlocks = rawBlocks.map {
            ReedSolomon.computeRemainder(it, divisor)
        }

        for (i in 0 until eccPerBlock) {
            for (block in eccBlocks) {
                yield(block[i])
            }
        }
    }

    private fun bitstreamLength(version: Int) = chunks.sumOf { it.size(version) }

    private fun determineVersion(): Int {
        for (version in 1..40) {
            if (bitstreamLength(version) < dataCodewords(version, errorLevel) * 8) {
                return version
            }
        }
        throw IllegalArgumentException("Too much data")
    }

    fun build(): QrCode {
        val version = determineVersion()
        return QrCode(
            QrCode.Header(errorLevel, version, maskPattern),
            codewords(version).toBitstream()
        )
    }

    sealed class Chunk {
        abstract fun size(version: Int): Int
        abstract fun encode(version: Int): Bitstream

        data class Numeric(val data: List<Int>) : Chunk() {
            private fun sizeBits(version: Int): Int = (version - 10) / 17 * 2 + 10
            private fun digitBits(digits: Int): Int = digits * 3 + 1

            override fun size(version: Int): Int {
                val modeBits = 4
                val dataBits = (data.size / 3) * 10
                val dataTail = digitBits(data.size % 3)
                return modeBits + sizeBits(version) + dataBits + dataTail
            }

            override fun encode(version: Int): Bitstream = sequence {
                yield(0, 0, 0, 1)
                val sizeBits = data.size.toBinaryBE(sizeBits(version))
                yieldAll(sizeBits)
                val dataBits = data.asSequence()
                    .windowed(3, 3, true)
                    .map {
                        it.reduce { acc, i -> acc * 10 + i }
                            .toBinaryBE(digitBits(it.size))
                    }
                    .flatten()
                yieldAll(dataBits)
            }
        }

        data class Alphanumeric(val data: String) : Chunk() {
            private fun sizeBits(version: Int): Int = (version - 10) / 17 * 2 + 9
            private fun letterBits(digits: Int): Int = digits * 5 + 1

            override fun size(version: Int): Int {
                val modeBits = 4
                val dataBits = (data.length / 2) * 11
                val dataTail = letterBits(data.length % 2)
                return modeBits + sizeBits(version) + dataBits + dataTail
            }

            override fun encode(version: Int): Bitstream = sequence {
                yield(0, 0, 1, 0)
                val sizeBits = data.length.toBinaryBE(sizeBits(version))
                yieldAll(sizeBits)
                val dataBits = data.asSequence()
                    .map { ALPHABET.indexOf(it) }
                    .windowed(2, 2, true)
                    .map {
                        it.reduce { acc, i -> acc * 45 + i }
                            .toBinaryBE(letterBits(it.size))
                    }
                    .flatten()
                yieldAll(dataBits)
            }

            companion object {
                private const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:"
            }
        }

        data class Bytes(val data: ByteArray) : Chunk() {
            private fun sizeBits(version: Int): Int = if (version < 10) 8 else 16

            override fun size(version: Int): Int {
                val modeBits = 4
                val dataBits = data.size * 8
                return modeBits + sizeBits(version) + dataBits
            }

            override fun encode(version: Int): Bitstream = sequence {
                yield(0, 1, 0, 0)
                val sizeBits = data.size.toBinaryBE(sizeBits(version))
                yieldAll(sizeBits)
                val dataBits = data.asSequence()
                    .flatMap { it.toUByte().toBinaryBE() }
                yieldAll(dataBits)
            }
        }

        data object Terminator : Chunk() {
            override fun size(version: Int): Int = 4

            override fun encode(version: Int): Bitstream = sequence {
                yield(0, 0, 0, 0)
            }
        }
    }

    companion object {
        private val totalCodewords = arrayOf(
            0,
            26, 44, 70, 100, 134, 172, 196, 242,
            292, 346, 404, 466, 532, 581, 655, 733,
            815, 901, 991, 1085, 1156, 1258, 1364, 1474,
            1588, 1706, 1828, 1921, 2051, 2185, 2323, 2465,
            2611, 2761, 2876, 3034, 3196, 3362, 3532, 3706,
        )

        internal fun totalCodewords(version: Int) = totalCodewords[version]

        private val eccPerBlock = mapOf(
            QrCode.ErrorCorrectionLevel.L to arrayOf(
                -1,
                7, 10, 15, 20, 26, 18, 20, 24,
                30, 18, 20, 24, 26, 30, 22, 24,
                28, 30, 28, 28, 28, 28, 30, 30,
                26, 28, 30, 30, 30, 30, 30, 30,
                30, 30, 30, 30, 30, 30, 30, 30
            ),
            QrCode.ErrorCorrectionLevel.M to arrayOf(
                -1,
                10, 16, 26, 18, 24, 16, 18, 22,
                22, 26, 30, 22, 22, 24, 24, 28,
                28, 26, 26, 26, 26, 28, 28, 28,
                28, 28, 28, 28, 28, 28, 28, 28,
                28, 28, 28, 28, 28, 28, 28, 28
            ),
            QrCode.ErrorCorrectionLevel.Q to arrayOf(
                -1,
                13, 22, 18, 26, 18, 24, 18, 22,
                20, 24, 28, 26, 24, 20, 30, 24,
                28, 28, 26, 30, 28, 30, 30, 30,
                30, 28, 30, 30, 30, 30, 30, 30,
                30, 30, 30, 30, 30, 30, 30, 30
            ),
            QrCode.ErrorCorrectionLevel.H to arrayOf(
                -1,
                17, 28, 22, 16, 22, 28, 26, 26,
                24, 28, 24, 28, 22, 24, 24, 30,
                28, 28, 26, 28, 30, 24, 30, 30,
                30, 30, 30, 30, 30, 30, 30, 30,
                30, 30, 30, 30, 30, 30, 30, 30
            ),
        )

        private val eccBlocks = mapOf(
            QrCode.ErrorCorrectionLevel.L to arrayOf(
                -1,
                1, 1, 1, 1, 1, 2, 2, 2,
                2, 4, 4, 4, 4, 4, 6, 6,
                6, 6, 7, 8, 8, 9, 9, 10,
                12, 12, 12, 13, 14, 15, 16, 17,
                18, 19, 19, 20, 21, 22, 24, 25
            ),
            QrCode.ErrorCorrectionLevel.M to arrayOf(
                -1,
                1, 1, 1, 2, 2, 4, 4, 4,
                5, 5, 5, 8, 9, 9, 10, 10,
                11, 13, 14, 16, 17, 17, 18, 20,
                21, 23, 25, 26, 28, 29, 31, 33,
                35, 37, 38, 40, 43, 45, 47, 49
            ),
            QrCode.ErrorCorrectionLevel.Q to arrayOf(
                -1,
                1, 1, 2, 2, 4, 4, 6, 6,
                8, 8, 8, 10, 12, 16, 12, 17,
                16, 18, 21, 20, 23, 23, 25, 27,
                29, 34, 34, 35, 38, 40, 43, 45,
                48, 51, 53, 56, 59, 62, 65, 68
            ),
            QrCode.ErrorCorrectionLevel.H to arrayOf(
                -1,
                1, 1, 2, 4, 4, 4, 5, 6,
                8, 8, 11, 11, 16, 16, 18, 16,
                19, 21, 25, 25, 25, 34, 30, 32,
                35, 37, 40, 42, 45, 48, 51, 54,
                57, 60, 63, 66, 70, 74, 77, 81
            ),
        )

        internal fun eccBlocks(version: Int, errorLevel: QrCode.ErrorCorrectionLevel) =
            eccBlocks[errorLevel]!![version]

        internal fun eccPerBlock(version: Int, errorLevel: QrCode.ErrorCorrectionLevel) =
            eccPerBlock[errorLevel]!![version]

        internal fun eccCodewords(version: Int, errorLevel: QrCode.ErrorCorrectionLevel) =
            eccBlocks(version, errorLevel) * eccPerBlock(version, errorLevel)

        internal fun dataCodewords(version: Int, errorLevel: QrCode.ErrorCorrectionLevel) =
            totalCodewords(version) - eccCodewords(version, errorLevel)

        private val paddingStream: CodewordStream = sequence {
            while (true) {
                yield(236u)
                yield(17u)
            }
        }

        private val bitstreamPadding = sequenceOf(false, false, false, false, false, false, false)
    }
}
