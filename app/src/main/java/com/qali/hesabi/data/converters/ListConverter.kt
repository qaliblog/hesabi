package com.qali.hesabi.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qali.hesabi.data.SaleItem
import com.qali.hesabi.data.PurchaseItem

class ListConverter {
    @TypeConverter
    fun fromSaleItemList(value: List<SaleItem>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toSaleItemList(value: String): List<SaleItem> {
        val listType = object : TypeToken<List<SaleItem>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromPurchaseItemList(value: List<PurchaseItem>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPurchaseItemList(value: String): List<PurchaseItem> {
        val listType = object : TypeToken<List<PurchaseItem>>() {}.type
        return Gson().fromJson(value, listType)
    }
}