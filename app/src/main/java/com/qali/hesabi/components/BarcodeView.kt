package com.qali.hesabi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BarcodeView(barcode: String) {
    // Placeholder for barcode rendering. Replace with actual barcode generation logic.
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 48.dp)
            .background(Color.Gray, MaterialTheme.shapes.medium)
    )
}