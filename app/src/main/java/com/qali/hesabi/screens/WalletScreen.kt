package com.qali.hesabi.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.navigation.NavController
import com.qali.hesabi.components.WalletCard
import com.qali.hesabi.navigation.Screen
import com.qali.hesabi.ui.WalletTransactionViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import com.qali.hesabi.util.JalaliUtils
import java.util.Date
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.foundation.layout.Row

@Composable
fun WalletScreen(navController: NavController, walletTransactionViewModel: WalletTransactionViewModel) {
    val transactions by walletTransactionViewModel.allTransactions.collectAsState(initial = emptyList())
    val overallAmount = transactions.sumOf { if (it.type.name == "INCOME") it.amount else -it.amount }
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.background
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp)
    ) {
        androidx.compose.material3.Card(
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "کیف پول",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(20.dp)
            )
        }
        androidx.compose.material3.Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "موجودی کل: ${overallAmount} تومان",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (transactions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "هنوز تراکنشی ثبت نشده است",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(transactions) { transaction ->
                    WalletCard(
                        transaction = transaction,
                        onEdit = { navController.navigate(Screen.AddWalletTransaction.route + "/${transaction.id}") },
                        onDelete = { walletTransactionViewModel.delete(it) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (transactions.isNotEmpty()) {
            DailyExpensesChart(transactions)
            Spacer(modifier = Modifier.height(24.dp))
        }
        Button(
            onClick = { navController.navigate(Screen.AddWalletTransaction.route) },
            modifier = Modifier
                .align(Alignment.End)
                .height(56.dp)
                .width(220.dp),
            shape = MaterialTheme.shapes.medium,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("افزودن تراکنش جدید", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun DailyExpensesChart(transactions: List<com.qali.hesabi.data.WalletTransaction>) {
    // Group by Jalali date
    val grouped = transactions.groupBy { com.qali.hesabi.util.JalaliUtils.toJalaliString(java.util.Date(it.date)) }
    val dailyData = grouped.map { (date, txs) ->
        val income = txs.filter { it.type == com.qali.hesabi.data.TransactionType.INCOME }.sumOf { it.amount }
        val expense = txs.filter { it.type == com.qali.hesabi.data.TransactionType.EXPENSE }.sumOf { it.amount }
        val net = income - expense
        Triple(date, income, expense)
    }.sortedBy { it.first }
    val netList = dailyData.map { it.second - it.third }
    val maxAbsNet = (netList.map { kotlin.math.abs(it) }.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    val barWidth = 36.dp
    val barSpacing = 28.dp
    val chartHeight = 180.dp
    val scrollState = rememberScrollState()
    if (dailyData.isEmpty()) {
        Text("هیچ داده‌ای برای نمایش وجود ندارد", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        return
    }
    Column(Modifier.fillMaxWidth()) {
        Text("سود/زیان روزانه", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            Modifier
                .horizontalScroll(scrollState)
                .fillMaxWidth()
        ) {
            dailyData.forEach { (date, income, expense) ->
                val net = income - expense
                val barColor = if (net >= 0) Color(0xFF43A047) else Color(0xFFD32F2F)
                val incomeBar = if (maxAbsNet > 0) (income / maxAbsNet * chartHeight.value).toFloat() else 0f
                val expenseBar = if (maxAbsNet > 0) (expense / maxAbsNet * chartHeight.value).toFloat() else 0f
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = barSpacing / 2)
                ) {
                    // Net profit/loss label
                    Text(
                        text = if (net >= 0) "+${net.toInt()}" else net.toInt().toString(),
                        color = barColor,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                    Box(
                        modifier = Modifier
                            .height(chartHeight)
                            .width(barWidth),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Expense bar (red, behind)
                        if (expense > 0) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(expenseBar.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(Color(0xFFD32F2F), shape = RoundedCornerShape(6.dp))
                            )
                        }
                        // Income bar (green, in front)
                        if (income > 0) {
                            Box(
                                Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(incomeBar.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(Color(0xFF43A047), shape = RoundedCornerShape(6.dp))
                            )
                        }
                    }
                    // Date label
                    Text(
                        text = date,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
        // Axis labels
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("درآمد", color = Color(0xFF43A047), style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
            Text("هزینه", color = Color(0xFFD32F2F), style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
        }
    }
}