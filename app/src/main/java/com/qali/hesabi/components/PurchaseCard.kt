package com.qali.hesabi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Download
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
import com.qali.hesabi.data.Purchase

import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import com.qali.hesabi.data.PurchaseItem
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.qali.hesabi.util.ReceiptUtils
import kotlinx.coroutines.launch
import android.widget.Toast
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

@Composable
fun PurchaseCard(
    purchase: Purchase,
    onEdit: (Purchase) -> Unit = {},
    onDelete: (Purchase) -> Unit = {},
    jalaliDate: String = ""
) {
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
                        colors = listOf(Color(0xFFFAFAFA), Color(0xFFE0E0E0)),
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
                    text = "رسید خرید",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (jalaliDate.isNotEmpty()) {
                    Text(
                        text = "تاریخ: $jalaliDate",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                purchase.items.forEach { item ->
                    Text(
                        text = "${item.productName}  |  تعداد: ${item.quantity}  |  قیمت واحد: ${item.price} تومان",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "جمع کل: ${purchase.total} تومان",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onEdit(purchase) }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                            contentDescription = "Edit Purchase",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "Delete Purchase",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val filePath = ReceiptUtils.saveReceiptAsPng(context, purchase)
                                if (filePath != null) {
                                    Toast.makeText(context, "رسید در پوشه Pictures/HesabiReceipts ذخیره شد", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "خطا در ذخیره رسید", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = "دانلود رسید",
                            tint = Color(0xFF1976D2)
                        )
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val filePath = ReceiptUtils.saveReceiptAsPng(context, purchase)
                            if (filePath != null) {
                                val bitmap = android.graphics.BitmapFactory.decodeFile(filePath)
                                shareBitmap(context, bitmap, "purchase-receipt.png")
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "اشتراک گذاری رسید", tint = Color(0xFF388E3C))
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val filePath = ReceiptUtils.saveReceiptAsPng(context, purchase)
                            if (filePath != null) {
                                val bitmap = android.graphics.BitmapFactory.decodeFile(filePath)
                                printBitmap(context, bitmap, "رسید خرید")
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Print, contentDescription = "چاپ رسید", tint = Color(0xFF6D4C41))
                    }
                }
            }
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("تایید حذف") },
            text = { Text("آیا از حذف این خرید مطمئن هستید؟") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDialog.value = false
                    onDelete(purchase)
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

// Utility functions for sharing and printing
private fun shareBitmap(context: android.content.Context, bitmap: android.graphics.Bitmap, fileName: String) {
    val cachePath = java.io.File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = java.io.File(cachePath, fileName)
    val fileOutputStream = java.io.FileOutputStream(file)
    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, fileOutputStream)
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
private fun printBitmap(context: android.content.Context, bitmap: android.graphics.Bitmap, jobName: String) {
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