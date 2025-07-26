package com.qali.hesabi.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.qali.hesabi.data.Product
import com.qali.hesabi.components.BarcodeView
import com.qali.hesabi.util.rememberRequestStoragePermission
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.LaunchedEffect
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.io.OutputStream
import android.util.Log
import android.widget.Toast

import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush

@Composable
fun ProductCard(product: Product) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val requestPermission = rememberRequestStoragePermission()
    var downloadStatus by remember { mutableStateOf<DownloadStatus>(DownloadStatus.Idle) }
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
        )
    )
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(cardGradient, MaterialTheme.shapes.large)
            .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
            .padding(bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "قیمت: ${product.price} تومان",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "تعداد: ${product.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BarcodeView(barcode = product.barcode)
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                downloadStatus = DownloadStatus.Downloading
                                try {
                                    requestPermission()
                                    val barcodeEncoder = BarcodeEncoder()
                                    val bitmap = barcodeEncoder.encodeBitmap(product.barcode, BarcodeFormat.CODE_128, 400, 150)
                                    val success = saveBitmap(context, bitmap, "${product.name}-barcode.png")
                                    if (success) {
                                        downloadStatus = DownloadStatus.Success
                                        Toast.makeText(context, "بارکد با موفقیت دانلود شد", Toast.LENGTH_SHORT).show()
                                    } else {
                                        downloadStatus = DownloadStatus.Error
                                        Toast.makeText(context, "خطا در دانلود بارکد", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e("ProductCard", "Error downloading barcode", e)
                                    downloadStatus = DownloadStatus.Error
                                    Toast.makeText(context, "خطا در دانلود بارکد: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(4.dp)
                    ) {
                        when (downloadStatus) {
                            DownloadStatus.Idle -> Icon(Icons.Filled.ArrowDownward, contentDescription = "دانلود بارکد", tint = MaterialTheme.colorScheme.onPrimary)
                            DownloadStatus.Downloading -> Icon(Icons.Filled.ArrowDownward, contentDescription = "در حال دانلود", tint = MaterialTheme.colorScheme.onPrimary)
                            DownloadStatus.Success -> Icon(Icons.Filled.Check, contentDescription = "دانلود شد", tint = MaterialTheme.colorScheme.tertiary)
                            DownloadStatus.Error -> Icon(Icons.Filled.Error, contentDescription = "خطا", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

sealed class DownloadStatus {
    object Idle : DownloadStatus()
    object Downloading : DownloadStatus()
    object Success : DownloadStatus()
    object Error : DownloadStatus()
}

private fun saveBitmap(context: android.content.Context, bitmap: Bitmap, fileName: String): Boolean {
    return try {
        Log.d("ProductCard", "Saving bitmap with name: $fileName")
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri == null) {
            Log.e("ProductCard", "Failed to create new MediaStore record.")
            return false
        }

        val outputStream: OutputStream? = resolver.openOutputStream(uri)
        if (outputStream == null) {
            Log.e("ProductCard", "Failed to get output stream.")
            return false
        }
        
        outputStream.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            Log.d("ProductCard", "Bitmap saved successfully.")
        }
        true
    } catch (e: Exception) {
        Log.e("ProductCard", "Error saving bitmap", e)
        false
    }
}