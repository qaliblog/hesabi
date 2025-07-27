package com.qali.hesabi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY id DESC") // Now includes items and date
    fun getAllPurchases(): Flow<List<Purchase>>

    @Insert
    suspend fun insert(purchase: Purchase)

    @androidx.room.Update
    suspend fun update(purchase: Purchase)

    @androidx.room.Delete
    suspend fun delete(purchase: Purchase)
}