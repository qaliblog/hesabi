package com.qali.hesabi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY id DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Insert
    suspend fun insert(sale: Sale)

    @androidx.room.Update
    suspend fun update(sale: Sale)

    @androidx.room.Delete
    suspend fun delete(sale: Sale)
}