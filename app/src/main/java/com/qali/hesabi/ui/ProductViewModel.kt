package com.qali.hesabi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qali.hesabi.data.Product
import com.qali.hesabi.data.ProductDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(private val productDao: ProductDao) : ViewModel() {

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    fun insert(product: Product) = viewModelScope.launch {
        try {
            productDao.insert(product)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun update(product: Product) = viewModelScope.launch {
        productDao.update(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        productDao.delete(product)
    }

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)
}

class ProductViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
