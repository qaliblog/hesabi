package com.qali.hesabi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qali.hesabi.data.WalletTransaction
import com.qali.hesabi.data.WalletTransactionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WalletTransactionViewModel(private val dao: WalletTransactionDao) : ViewModel() {
    val allTransactions: Flow<List<WalletTransaction>> = dao.getAllTransactions()
    fun insert(transaction: WalletTransaction) = viewModelScope.launch {
        dao.insert(transaction)
    }
}

class WalletTransactionViewModelFactory(private val dao: WalletTransactionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletTransactionViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}