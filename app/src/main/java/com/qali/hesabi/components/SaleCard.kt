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

@Composable
fun SaleCard(sale: Sale) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("fa"))
    val dateString = dateFormat.format(Date(sale.date))
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "خریدار: ${sale.buyerName}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "تاریخ: $dateString", style = MaterialTheme.typography.bodySmall)
                    Text(text = "جمع کل: ${sale.total} تومان", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        val bitmap = generateReceiptBitmap(sale, dateString)
                        val success = saveSaleReceiptBitmap(context, bitmap, "sale-receipt-${sale.id}.png")
                        Toast.makeText(context, if (success) "رسید ذخیره شد" else "خطا در ذخیره رسید", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(Icons.Filled.ArrowDownward, contentDescription = "دانلود رسید")
                }
            }
            
            if (sale.products.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "محصولات:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                sale.products.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.productName} (${item.quantity} عدد)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${item.price * item.quantity} تومان",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

fun generateReceiptBitmap(sale: Sale, dateString: String): Bitmap {
    val width = 600
    val height = 300 + sale.products.size * 60
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = AndroidColor.BLACK
        textSize = 32f
        isAntiAlias = true
    }
    canvas.drawColor(AndroidColor.WHITE)
    var y = 50
    canvas.drawText("رسید فروش", 220f, y.toFloat(), paint)
    y += 50
    canvas.drawText("خریدار: ${sale.buyerName}", 30f, y.toFloat(), paint)
    y += 40
    canvas.drawText("تاریخ: $dateString", 30f, y.toFloat(), paint)
    y += 40
    canvas.drawText("محصولات:", 30f, y.toFloat(), paint)
    y += 40
    sale.products.forEach {
        canvas.drawText("${it.productName} (${it.quantity} عدد) - ${it.price * it.quantity} تومان", 30f, y.toFloat(), paint)
        y += 40
    }
    y += 20
    paint.textSize = 36f
    canvas.drawText("جمع کل: ${sale.total} تومان", 30f, y.toFloat(), paint)
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