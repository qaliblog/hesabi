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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import android.print.PrintManager
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.graphics.pdf.PdfDocument
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import java.io.FileOutputStream

@Composable
fun ProductCard(
    product: Product,
    onEdit: (Product) -> Unit = {},
    onDelete: (Product) -> Unit = {}
) {
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
    val showDialog = remember { mutableStateOf(false) }
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
                .fillMaxWidth()
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
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Center barcode
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BarcodeView(barcode = product.barcode)
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Center action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onEdit(product) }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "Edit Product",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showDialog.value = true }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                        contentDescription = "Delete Product",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
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
                ) {
                    when (downloadStatus) {
                        DownloadStatus.Idle -> Icon(Icons.Filled.ArrowDownward, contentDescription = "دانلود بارکد", tint = MaterialTheme.colorScheme.onPrimary)
                        DownloadStatus.Downloading -> Icon(Icons.Filled.ArrowDownward, contentDescription = "در حال دانلود", tint = MaterialTheme.colorScheme.onPrimary)
                        DownloadStatus.Success -> Icon(Icons.Filled.Check, contentDescription = "دانلود شد", tint = MaterialTheme.colorScheme.tertiary)
                        DownloadStatus.Error -> Icon(Icons.Filled.Error, contentDescription = "خطا", tint = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    coroutineScope.launch {
                        val barcodeEncoder = BarcodeEncoder()
                        val bitmap = barcodeEncoder.encodeBitmap(product.barcode, BarcodeFormat.CODE_128, 400, 150)
                        shareBitmap(context, bitmap, "${product.name}-barcode.png")
                    }
                }) {
                    Icon(Icons.Filled.Share, contentDescription = "اشتراک گذاری بارکد", tint = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    coroutineScope.launch {
                        val barcodeEncoder = BarcodeEncoder()
                        val bitmap = barcodeEncoder.encodeBitmap(product.barcode, BarcodeFormat.CODE_128, 400, 150)
                        printBitmap(context, bitmap, "بارکد محصول")
                    }
                }) {
                    Icon(Icons.Filled.Print, contentDescription = "چاپ بارکد", tint = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("تایید حذف") },
            text = { Text("آیا از حذف این محصول مطمئن هستید؟") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDialog.value = false
                    onDelete(product)
                }) {
                    Text("حذف")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDialog.value = false }) {
                    Text("انصراف")
                }
            }
        )
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

// Utility functions for sharing and printing
private fun shareBitmap(context: android.content.Context, bitmap: Bitmap, fileName: String) {
    val cachePath = java.io.File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = java.io.File(cachePath, fileName)
    val fileOutputStream = java.io.FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    fileOutputStream.close()
    val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    val shareIntent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        putExtra(android.content.Intent.EXTRA_STREAM, uri)
        type = "image/png"
        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, "اشتراک گذاری بارکد"))
}
private fun printBitmap(context: android.content.Context, bitmap: Bitmap, jobName: String) {
    val printManager = context.getSystemService(android.content.Context.PRINT_SERVICE) as PrintManager
    val printAdapter = object : PrintDocumentAdapter() {
        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback?,
            extras: android.os.Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onLayoutCancelled()
                return
            }
            val info = PrintDocumentInfo.Builder("barcode.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build()
            callback?.onLayoutFinished(info, true)
        }
        override fun onWrite(
            pages: Array<out android.print.PageRange>?,
            destination: ParcelFileDescriptor?,
            cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback?
        ) {
            val pdfDocument = PrintedPdfDocument(context, PrintAttributes.Builder().build())
            val page = pdfDocument.startPage(0)
            val canvas = page.canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)
            try {
                pdfDocument.writeTo(FileOutputStream(destination?.fileDescriptor))
                callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback?.onWriteFailed(e.toString())
            } finally {
                pdfDocument.close()
            }
        }
    }
    printManager.print(jobName, printAdapter, null)
}