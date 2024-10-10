package de.justjanne.composeqr

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

private const val scale = 10f

class QrCodePainter(
    code: QrCode
) : Painter() {
    private val qrCanvas = QrCanvas.from(code)
    private val padding = 4f
    private val paddedSize = qrCanvas.size + padding * 2
    override val intrinsicSize = Size(paddedSize * scale, paddedSize * scale)

    override fun DrawScope.onDraw() {
        val scale = size.minDimension / paddedSize
        drawRect(
            Color.White,
            topLeft = Offset.Zero,
            size = Size(paddedSize * scale, paddedSize * scale)
        )
        for (y in 0 until qrCanvas.size) {
            for (x in 0 until qrCanvas.size) {
                if (qrCanvas.get(x, y)) {
                    drawRect(
                        Color.Black,
                        topLeft = Offset((x + padding) * scale, (y + padding) * scale),
                        size = Size(scale, scale)
                    )
                }
            }
        }
    }
}
