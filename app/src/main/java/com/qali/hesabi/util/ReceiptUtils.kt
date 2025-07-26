package com.qali.hesabi.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Environment
import com.qali.hesabi.data.Purchase
import com.qali.hesabi.data.PurchaseItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ReceiptUtils {
    suspend fun saveReceiptAsPng(context: Context, purchase: Purchase): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Create a bitmap with fixed dimensions for the receipt
                val width = 800
                val height = 600
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                // Set background
                val backgroundPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#FAFAFA")
                    style = Paint.Style.FILL
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

                // Create gradient background effect
                val gradientPaint = Paint().apply {
                    shader = android.graphics.LinearGradient(
                        0f, 0f, 0f, height.toFloat(),
                        android.graphics.Color.parseColor("#FAFAFA"),
                        android.graphics.Color.parseColor("#E0E0E0"),
                        android.graphics.Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gradientPaint)

                // Set up text paint
                val titlePaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#1976D2")
                    textSize = 44f
                    isFakeBoldText = true
                    textAlign = Paint.Align.RIGHT
                }

                val itemPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#333333")
                    textSize = 32f
                    textAlign = Paint.Align.RIGHT
                }

                val totalPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#D32F2F")
                    textSize = 36f
                    isFakeBoldText = true
                    textAlign = Paint.Align.RIGHT
                }

                // Draw receipt content
                var yPosition = 80f

                // Title
                canvas.drawText("رسید خرید", width - 40f, yPosition, titlePaint)
                yPosition += 60f

                // Items
                purchase.items.forEach { item ->
                    val itemText = "${item.productName}  |  تعداد: ${item.quantity}  |  قیمت واحد: ${item.price} تومان"
                    canvas.drawText(itemText, width - 40f, yPosition, itemPaint)
                    yPosition += 40f
                }

                // Total
                yPosition += 20f
                canvas.drawText("جمع کل: ${purchase.total} تومان", width - 40f, yPosition, totalPaint)

                // Save to file
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "receipt_${timestamp}.png"
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val receiptDir = File(picturesDir, "HesabiReceipts")
                if (!receiptDir.exists()) {
                    receiptDir.mkdirs()
                }

                val file = File(receiptDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                bitmap.recycle()

                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}