package com.hesabi.data

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val quantity: Int,
    val barcode: String
)

data class Sale(
    val id: Int,
    val buyerName: String,
    val items: List<SaleItem>,
    val total: Int,
    val receiptPath: String?
)

data class SaleItem(
    val product: Product,
    val quantity: Int,
    val price: Int
)

data class Purchase(
    val id: Int,
    val items: List<PurchaseItem>,
    val total: Int,
    val receiptPath: String?
)

data class PurchaseItem(
    val product: Product,
    val quantity: Int,
    val price: Int
)

data class WalletTransaction(
    val id: Int,
    val amount: Int,
    val type: TransactionType,
    val description: String
)

enum class TransactionType {
    INCOME, EXPENSE
}