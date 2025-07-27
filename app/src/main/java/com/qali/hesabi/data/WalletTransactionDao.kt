package com.qali.hesabi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletTransactionDao {
    @Query("SELECT * FROM wallet_transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<WalletTransaction>>

    @Insert
    suspend fun insert(transaction: WalletTransaction)

    @androidx.room.Update
    suspend fun update(transaction: WalletTransaction)

    @androidx.room.Delete
    suspend fun delete(transaction: WalletTransaction)

    @Query("SELECT * FROM wallet_transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Int): WalletTransaction?
}