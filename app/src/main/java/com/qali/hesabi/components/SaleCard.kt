package com.qali.hesabi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qali.hesabi.data.Sale
import androidx.compose.foundation.border
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.os.Build
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Print
import android.print.PrintManager
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.graphics.pdf.PdfDocument
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import java.io.FileOutputStream
import android.print.PageRange
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.addPath
import androidx.compose.ui.graphics.Color

val CustomPrintIcon = ImageVector.Builder(
    name = "CustomPrint", defaultWidth = 24f, defaultHeight = 24f, viewportWidth = 24f, viewportHeight = 24f
).apply {
    addPath(
        pathData = PathBuilder().apply {
            moveTo(6f, 9f)
            lineTo(18f, 9f)
            lineTo(18f, 4f)
            lineTo(6f, 4f)
            close()
            moveTo(19f, 8f)
            horizontalLineTo(20f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, 1f)
            verticalLineTo(19f)
            arcToRelative(1f, 1f, 0f, false, true, -1f, 1f)
            horizontalLineTo(5f)
            arcToRelative(1f, 1f, 0f, false, true, -1f, -1f)
            verticalLineTo(9f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, -1f)
            horizontalLineTo(6f)
            verticalLineTo(2f)
            horizontalLineTo(18f)
            verticalLineTo(8f)
            close()
        }.getNodes(),
        fill = Color.Black
    )
}.build()

val CustomShareIcon = ImageVector.Builder(
    name = "CustomShare", defaultWidth = 24f, defaultHeight = 24f, viewportWidth = 24f, viewportHeight = 24f
).apply {
    addPath(
        pathData = PathBuilder().apply {
            moveTo(18f, 16.08f)
            curveTo(17.24f, 16.08f, 16.56f, 16.38f, 16.05f, 16.85f)
            lineTo(8.91f, 12.7f)
            curveTo(8.96f, 12.47f, 9f, 12.24f, 9f, 12f)
            curveTo(9f, 11.76f, 8.96f, 11.53f, 8.91f, 11.3f)
            lineTo(15.96f, 7.19f)
            curveTo(16.5f, 7.69f, 17.21f, 8f, 18f, 8f)
            curveTo(19.66f, 8f, 21f, 6.66f, 21f, 5f)
            curveTo(21f, 3.34f, 19.66f, 2f, 18f, 2f)
            curveTo(16.34f, 2f, 15f, 3.34f, 15f, 5f)
            curveTo(15f, 5.24f, 15.04f, 5.47f, 15.09f, 5.7f)
            lineTo(8.04f, 9.81f)
            curveTo(7.5f, 9.31f, 6.79f, 9f, 6f, 9f)
            curveTo(4.34f, 9f, 3f, 10.34f, 3f, 12f)
            curveTo(3f, 13.66f, 4.34f, 15f, 6f, 15f)
            curveTo(6.79f, 15f, 7.5f, 14.69f, 8.04f, 14.19f)
            lineTo(15.14f, 18.36f)
            curveTo(15.09f, 18.56f, 15.06f, 18.78f, 15.06f, 19f)
            curveTo(15.06f, 20.1f, 15.96f, 21f, 17.06f, 21f)
            curveTo(18.16f, 21f, 19.06f, 20.1f, 19.06f, 19f)
            curveTo(19.06f, 17.9f, 18.16f, 17f, 17.06f, 17f)
            close()
        }.getNodes(),
        fill = Color.Black
    )
}.build()

@Composable
fun SaleCard(
    sale: Sale,
    onEdit: (Sale) -> Unit = {},
    onDelete: (Sale) -> Unit = {},
    jalaliDate: String = ""
) {
    val dateString = jalaliDate.ifEmpty {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("fa"))
        dateFormat.format(Date(sale.date))
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(ComposeColor(0xFFFAFAFA), ComposeColor(0xFFE0E0E0)),
                        tileMode = TileMode.Clamp
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "رسید فروش",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor(0xFF1976D2),
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "خریدار: ${sale.buyerName}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        color = ComposeColor(0xFF333333),
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "تاریخ: $dateString",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF333333),
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (sale.products.isNotEmpty()) {
                    Text(
                        text = "محصولات:",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ComposeColor(0xFF1976D2),
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    sale.products.forEach { item ->
                        Text(
                            text = "${item.productName}  |  تعداد: ${item.quantity}  |  قیمت واحد: ${item.price} تومان",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 15.sp,
                                color = ComposeColor(0xFF333333),
                                textAlign = TextAlign.Right
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "جمع کل: ${sale.total} تومان",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor(0xFFD32F2F),
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onEdit(sale) }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                            contentDescription = "Edit Sale",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "Delete Sale",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val bitmap = generateReceiptBitmapStyled(sale, dateString)
                            val success = saveSaleReceiptBitmap(context, bitmap, "sale-receipt-${sale.id}.png")
                            Toast.makeText(context, if (success) "رسید ذخیره شد" else "خطا در ذخیره رسید", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowDownward, contentDescription = "دانلود رسید", tint = ComposeColor(0xFF1976D2))
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val bitmap = generateReceiptBitmapStyled(sale, dateString)
                            shareBitmap(context, bitmap, "sale-receipt-${sale.id}.png")
                        }
                    }) {
                        Icon(CustomShareIcon, contentDescription = "اشتراک گذاری رسید", tint = ComposeColor(0xFF388E3C))
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val bitmap = generateReceiptBitmapStyled(sale, dateString)
                            printBitmap(context, bitmap, "رسید فروش")
                        }
                    }) {
                        Icon(CustomPrintIcon, contentDescription = "چاپ رسید", tint = ComposeColor(0xFF6D4C41))
                    }
                }
            }
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("تایید حذف") },
            text = { Text("آیا از حذف این فروش مطمئن هستید؟") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDialog.value = false
                    onDelete(sale)
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

fun generateReceiptBitmapStyled(sale: Sale, dateString: String): Bitmap {
    val width = 800
    val height = 300 + sale.products.size * 60
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    // Set background to white
    val backgroundPaint = Paint().apply {
        color = AndroidColor.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
    // Draw black border
    val borderPaint = Paint().apply {
        color = AndroidColor.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    canvas.drawRect(8f, 8f, width - 8f, height - 8f, borderPaint)
    // Title
    val titlePaint = Paint().apply {
        color = AndroidColor.BLACK
        textSize = 44f
        isFakeBoldText = true
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }
    val itemPaint = Paint().apply {
        color = AndroidColor.DKGRAY
        textSize = 32f
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }
    val totalPaint = Paint().apply {
        color = AndroidColor.BLACK
        textSize = 36f
        isFakeBoldText = true
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }
    val labelPaint = Paint().apply {
        color = AndroidColor.DKGRAY
        textSize = 32f
        isFakeBoldText = true
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }
    var y = 80f
    canvas.drawText("رسید فروش", width - 40f, y, titlePaint)
    y += 60f
    canvas.drawText("خریدار: ${sale.buyerName}", width - 40f, y, itemPaint)
    y += 40f
    canvas.drawText("تاریخ: $dateString", width - 40f, y, itemPaint)
    y += 40f
    canvas.drawText("محصولات:", width - 40f, y, labelPaint)
    y += 40f
    sale.products.forEach {
        val itemText = "${it.productName}  |  تعداد: ${it.quantity}  |  قیمت واحد: ${it.price} تومان"
        canvas.drawText(itemText, width - 40f, y, itemPaint)
        y += 40f
    }
    y += 20f
    canvas.drawText("جمع کل: ${sale.total} تومان", width - 40f, y, totalPaint)
    return bitmap
}

fun saveSaleReceiptBitmap(context: android.content.Context, bitmap: Bitmap, fileName: String): Boolean {
    return try {
        val resolver = context.contentResolver
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/HesabiReceipts")
            } else {
                val pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
                put(MediaStore.MediaColumns.DATA, "$pictures/HesabiReceipts/$fileName")
            }
        }
        val uri = resolver.insert(imageCollection, contentValues) ?: return false
        val outputStream = resolver.openOutputStream(uri) ?: return false
        outputStream.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
        }
        // For Android 9 and below, trigger media scan
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            scanIntent.data = uri
            context.sendBroadcast(scanIntent)
        }
        true
    } catch (e: Exception) {
        Log.e("SaleCard", "Error saving bitmap", e)
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
    context.startActivity(android.content.Intent.createChooser(shareIntent, "اشتراک گذاری رسید"))
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
            val info = PrintDocumentInfo.Builder("receipt.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build()
            callback?.onLayoutFinished(info, true)
        }
        override fun onWrite(
            pages: Array<PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal,
            callback: WriteResultCallback
        ) {
            val pdfDocument = PrintedPdfDocument(context, PrintAttributes.Builder().build())
            val page = pdfDocument.startPage(0)
            val canvas = page.canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)
            try {
                pdfDocument.writeTo(FileOutputStream(destination.fileDescriptor))
                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback.onWriteFailed(e.toString())
            } finally {
                pdfDocument.close()
            }
        }
    }
    printManager.print(jobName, printAdapter, null)
}