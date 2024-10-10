package de.justjanne.composeqr

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun QrCodePreviewAlphanumeric() {
    QrCodeView(
        QrCodeBuilder(
            errorLevel = QrCode.ErrorCorrectionLevel.L,
            maskPattern = QrCode.MaskPattern.PATTERN_6,
            chunks = listOf(
                QrCodeBuilder.Chunk.Alphanumeric("HTTPS://I.K8R.EU/FCQ16Q"),
                QrCodeBuilder.Chunk.Terminator,
            ),
        ).build(),
        contentDescription = null,
    )
}

@Preview
@Composable
fun QrCodePreviewNumeric() {
    QrCodeView(
        QrCodeBuilder(
            errorLevel = QrCode.ErrorCorrectionLevel.L,
            maskPattern = QrCode.MaskPattern.PATTERN_6,
            chunks = listOf(
                QrCodeBuilder.Chunk.Numeric(listOf(0, 1, 2, 3, 4, 5, 6, 7)),
                QrCodeBuilder.Chunk.Terminator,
            ),
        ).build(),
        contentDescription = null,
    )
}

@Preview
@Composable
fun QrCodePreviewBytesShort() {
    QrCodeView(
        QrCodeBuilder(
            errorLevel = QrCode.ErrorCorrectionLevel.L,
            maskPattern = QrCode.MaskPattern.PATTERN_6,
            chunks = listOf(
                QrCodeBuilder.Chunk.Bytes(
                    "Still alive.".encodeToByteArray()
                ),
                QrCodeBuilder.Chunk.Terminator,
            ),
        ).build(),
        contentDescription = null,
    )
}

@Preview
@Composable
fun QrCodePreviewBytesLong() {
    QrCodeView(
        QrCodeBuilder(
            errorLevel = QrCode.ErrorCorrectionLevel.L,
            maskPattern = QrCode.MaskPattern.PATTERN_6,
            chunks = listOf(
                QrCodeBuilder.Chunk.Bytes(
                    "This was a triumph.".encodeToByteArray()
                ),
                QrCodeBuilder.Chunk.Terminator,
            ),
        ).build(),
        contentDescription = null,
    )
}

@Preview
@Composable
fun QrCodePreviewBytes2() {
    QrCodeView(
        QrCodeBuilder(
            errorLevel = QrCode.ErrorCorrectionLevel.H,
            maskPattern = QrCode.MaskPattern.PATTERN_3,
            chunks = listOf(
                QrCodeBuilder.Chunk.Bytes(
                    (
                        "Je vraagt me waarom, maar ik ben niet dom\n" +
                            "Laat me maar vliegen in m'n luchtballon\n" +
                            "En dan denk ik soms: is iedereen stom?\n" +
                            "Laat me maar vliegen in m'n luchtballon\n" +
                            "Je vraagt me waarom, maar ik ben niet dom\n" +
                            "Laat me maar vliegen in m'n luchtballon\n" +
                            "En dan denk ik soms: is iedereen stom?\n" +
                            "Laat me maar vliegen in m'n luchtballon"
                        ).encodeToByteArray()
                ),
                QrCodeBuilder.Chunk.Terminator,
            ),
        ).build(),
        contentDescription = null,
    )
}
