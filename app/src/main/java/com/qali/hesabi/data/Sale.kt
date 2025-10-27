package com.qali.hesabi.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.qali.hesabi.data.converters.ListConverter

@Entity(tableName = "sales")
@TypeConverters(ListConverter::class)
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val buyerName: String,
    val total: Double,
    val products: List<SaleItem> = emptyList(),
    val date: Long = System.currentTimeMillis()
)

data class SaleItem(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val barcode: String
)
