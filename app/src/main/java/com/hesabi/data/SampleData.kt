package com.hesabi.data

object SampleData {
    val products = listOf(
        Product(1, "شیر پرچرب", 25000, 10, "6261234567890"),
        Product(2, "نان بربری", 8000, 20, "6269876543210"),
        Product(3, "پنیر لیقوان", 60000, 5, "6261122334455")
    )

    val sales = listOf(
        com.hesabi.data.Sale(
            id = 1,
            buyerName = "علی رضایی",
            items = listOf(
                com.hesabi.data.SaleItem(products[0], 2, 50000),
                com.hesabi.data.SaleItem(products[1], 1, 8000)
            ),
            total = 58000,
            receiptPath = null
        ),
        com.hesabi.data.Sale(
            id = 2,
            buyerName = "مریم احمدی",
            items = listOf(
                com.hesabi.data.SaleItem(products[2], 1, 60000)
            ),
            total = 60000,
            receiptPath = null
        )
    )

    val purchases = listOf(
        com.hesabi.data.Purchase(
            id = 1,
            items = listOf(
                com.hesabi.data.PurchaseItem(products[0], 5, 125000),
                com.hesabi.data.PurchaseItem(products[2], 2, 120000)
            ),
            total = 245000,
            receiptPath = null
        ),
        com.hesabi.data.Purchase(
            id = 2,
            items = listOf(
                com.hesabi.data.PurchaseItem(products[1], 10, 80000)
            ),
            total = 80000,
            receiptPath = null
        )
    )

    val walletTransactions = listOf(
        com.hesabi.data.WalletTransaction(1, 200000, com.hesabi.data.TransactionType.INCOME, "واریز فروش روزانه"),
        com.hesabi.data.WalletTransaction(2, 50000, com.hesabi.data.TransactionType.EXPENSE, "خرید مواد اولیه"),
        com.hesabi.data.WalletTransaction(3, 100000, com.hesabi.data.TransactionType.INCOME, "دریافت از مشتری")
    )
}