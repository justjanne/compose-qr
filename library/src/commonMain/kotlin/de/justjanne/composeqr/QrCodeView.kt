package de.justjanne.composeqr

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun QrCodeView(
    qrCode: QrCode,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    val painter = remember { QrCodePainter(qrCode) }
    Image(painter, contentDescription, modifier)
}
