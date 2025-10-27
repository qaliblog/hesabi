package com.qali.hesabi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qali.hesabi.data.Purchase
import com.qali.hesabi.data.PurchaseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PurchaseViewModel(private val purchaseDao: PurchaseDao) : ViewModel() {
    val allPurchases: Flow<List<Purchase>> = purchaseDao.getAllPurchases()
    fun insert(purchase: Purchase) = viewModelScope.launch {
        purchaseDao.insert(purchase)
    }
}

class PurchaseViewModelFactory(private val purchaseDao: PurchaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PurchaseViewModel(purchaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}