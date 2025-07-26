package com.qali.hesabi.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.qali.hesabi.data.converters.ListConverter

@Entity(tableName = "purchases")
@TypeConverters(ListConverter::class)
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val total: Double,
    val items: List<PurchaseItem> = emptyList(),
    val date: Long = System.currentTimeMillis()
)

data class PurchaseItem(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val barcode: String
)
