package de.justjanne.composeqr

typealias Bitstream = Sequence<Boolean>
typealias CodewordStream = Sequence<UByte>

internal fun Int.toBinaryBE(digits: Int): Bitstream = sequence {
    for (i in digits - 1 downTo 0) {
        yield(and(1 shl i) != 0)
    }
}

internal fun Int.toBinaryLE(digits: Int): Bitstream = sequence {
    for (i in 0 until digits) {
        yield(and(1 shl i) != 0)
    }
}

internal fun CodewordStream.toBitstream() = flatMap { it.toBinaryBE() }

internal fun UByte.toBinaryBE(): Bitstream = toInt().toBinaryBE(8)

internal fun Iterable<Boolean>.toUByte(): UByte =
    fold(0u) { acc: UInt, bit: Boolean -> acc shl 1 or if (bit) 1u else 0u }.toUByte()

internal suspend fun SequenceScope<Boolean>.yield(vararg bits: Int) {
    for (bit in bits) {
        yield(bit != 0)
    }
}

internal inline fun booleanArrayOf(vararg values: Int): BooleanArray = values.map { it != 0 }.toBooleanArray()

internal fun <T> Iterator<T>.take(count: Int): List<T> {
    val buffer = mutableListOf<T>()
    for (i in 0 until count) {
        if (!hasNext()) break
        buffer.add(next())
    }
    return buffer
}
