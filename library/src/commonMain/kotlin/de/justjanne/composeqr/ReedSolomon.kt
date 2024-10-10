package de.justjanne.composeqr

@OptIn(ExperimentalUnsignedTypes::class)
internal object ReedSolomon {
    fun computeDivisor(degree: Int): UByteArray {
        require(degree in 1..255) { "Degree out of range" }
        val result = UByteArray(degree)
        result[degree - 1] = 1u
        var root: UByte = 1u
        for (i in 0 until degree) {
            for (j in result.indices) {
                result[j] = multiply(result[j].toUInt(), root.toUInt())
                if (j + 1 < result.size) {
                    result[j] = result[j] xor result[j + 1]
                }
            }
            root = multiply(root.toUInt(), 2u)
        }
        return result
    }

    fun computeRemainder(data: UByteArray, divisor: UByteArray): UByteArray {
        val result = UByteArray(divisor.size)
        for (byte in data) {
            val factor = (byte xor result[0])
            result.shiftLeft()
            for ((i, div) in divisor.withIndex()) {
                result[i] = result[i] xor multiply(div.toUInt(), factor.toUInt())
            }
        }
        return result
    }

    private fun multiply(x: UInt, y: UInt): UByte {
        require(x shr 8 == 0u)
        require(y shr 8 == 0u)
        var z = 0u
        for (i in 7 downTo 0) {
            z = (z shl 1 xor ((z shr 7) * 0x011Du))
            if ((y and (1u shl i)) != 0u) {
                z = z xor x
            }
        }
        require(z shr 8 == 0u)
        return z.toUByte()
    }

    private fun UByteArray.shiftLeft() {
        copyInto(this, destinationOffset = 0, startIndex = 1)
        set(size - 1, 0u)
    }
}
