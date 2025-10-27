package com.qali.hesabi.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

@Composable
fun BarcodeView(barcode: String) {
    val barcodeEncoder = BarcodeEncoder()
    val bitmap = barcodeEncoder.encodeBitmap(barcode, BarcodeFormat.CODE_128, 400, 150)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Barcode",
        modifier = Modifier.size(width = 120.dp, height = 48.dp)
    )
}