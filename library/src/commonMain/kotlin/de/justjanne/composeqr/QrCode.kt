package de.justjanne.composeqr

data class QrCode(
    val header: Header,
    val data: Bitstream,
) {
    enum class ErrorCorrectionLevel(val value: Int) {
        L(0b01),
        M(0b00),
        Q(0b11),
        H(0b10)
    }

    enum class MaskPattern(val value: Int) {
        PATTERN_0(0),
        PATTERN_1(1),
        PATTERN_2(2),
        PATTERN_3(3),
        PATTERN_4(4),
        PATTERN_5(5),
        PATTERN_6(6),
        PATTERN_7(7),
    }

    data class Header(
        val errorLevel: ErrorCorrectionLevel,
        val version: Int,
        val maskPattern: MaskPattern,
    ) {
        fun encodeFormat(): Bitstream {
            val header = (errorLevel.value shl 3) or maskPattern.value
            var checksum = header shl 10
            while (G15.countLeadingZeroBits() >= checksum.countLeadingZeroBits()) {
                checksum = checksum xor (G15 shl (G15.countLeadingZeroBits() - checksum.countLeadingZeroBits()))
            }
            val data = header shl 10 or checksum
            return data.toBinaryLE(14).zip(mask, Boolean::xor)
        }
        fun encodeVersion(): Bitstream {
            var checksum = version shl 12
            println(version)
            while (G18.countLeadingZeroBits() >= checksum.countLeadingZeroBits()) {
                checksum = checksum xor (G18 shl (G18.countLeadingZeroBits() - checksum.countLeadingZeroBits()))
            }
            val data = version shl 12 or checksum
            return data.toBinaryLE(18)
        }

        companion object {
            private val mask = sequenceOf(
                false, true, false, false, true,
                false, false, false, false, false,
                true, false, true, false, true,
            )

            private const val G15 = 0b10100110111
            private const val G18 = 0b1111100100101
        }
    }
}
