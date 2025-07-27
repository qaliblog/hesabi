package com.qali.hesabi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qali.hesabi.data.Sale
import com.qali.hesabi.data.SaleDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SaleViewModel(private val saleDao: SaleDao) : ViewModel() {

    val allSales: Flow<List<Sale>> = saleDao.getAllSales()

    fun insert(sale: Sale) = viewModelScope.launch {
        saleDao.insert(sale)
    }

    fun update(sale: Sale) = viewModelScope.launch {
        saleDao.update(sale)
    }

    fun delete(sale: Sale) = viewModelScope.launch {
        saleDao.delete(sale)
    }
}

class SaleViewModelFactory(private val saleDao: SaleDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SaleViewModel(saleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}