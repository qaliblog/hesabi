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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonItem
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

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
    var chartMode by remember { mutableStateOf("روزانه") }
    val modes = listOf("روزانه", "ماهانه")
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("سود/زیان ${chartMode}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Spacer(Modifier.weight(1f))
            SegmentedButton(
                selectedIndex = modes.indexOf(chartMode),
                onItemSelected = { chartMode = modes[it] },
                items = modes
            )
        }
        Spacer(Modifier.height(8.dp))
        if (chartMode == "روزانه") {
            LineChart(transactions, groupByMonth = false)
        } else {
            LineChart(transactions, groupByMonth = true)
        }
    }
}

@Composable
fun LineChart(transactions: List<com.qali.hesabi.data.WalletTransaction>, groupByMonth: Boolean) {
    // Group by Jalali date (day or month)
    val grouped = transactions.groupBy {
        val date = java.util.Date(it.date)
        if (groupByMonth) {
            val jalali = com.qali.hesabi.util.JalaliUtils.toJalaliString(date)
            jalali.substring(0, 7) // yyyy/MM
        } else {
            com.qali.hesabi.util.JalaliUtils.toJalaliString(date)
        }
    }
    val netByGroup = grouped.map { (date, txs) ->
        val income = txs.filter { it.type == com.qali.hesabi.data.TransactionType.INCOME }.sumOf { it.amount }
        val expense = txs.filter { it.type == com.qali.hesabi.data.TransactionType.EXPENSE }.sumOf { it.amount }
        val net = income - expense
        date to net
    }.sortedBy { it.first }
    if (netByGroup.isEmpty()) {
        Text("هیچ داده‌ای برای نمایش وجود ندارد", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        return
    }
    val maxNet = netByGroup.maxOf { it.second }
    val minNet = netByGroup.minOf { it.second }
    val chartHeight = 180.dp
    val chartWidth = (netByGroup.size * 60).dp.coerceAtLeast(300.dp)
    val scrollState = rememberScrollState()
    Box(
        Modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .height(chartHeight)
                .width(chartWidth)
        ) {
            val points = netByGroup.mapIndexed { idx, pair ->
                val x = idx * (size.width / (netByGroup.size - 1).coerceAtLeast(1))
                val y = size.height - ((pair.second - minNet) / (maxNet - minNet + 1e-6).toFloat() * size.height)
                Offset(x, y)
            }
            if (points.size > 1) {
                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = if (netByGroup[i + 1].second >= 0) Color(0xFF43A047) else Color(0xFFD32F2F),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 4f
                    )
                }
            }
            // Draw points
            points.forEachIndexed { idx, pt ->
                drawCircle(
                    color = if (netByGroup[idx].second >= 0) Color(0xFF43A047) else Color(0xFFD32F2F),
                    radius = 7f,
                    center = pt
                )
            }
        }
        // X axis labels
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .padding(top = chartHeight + 4.dp)
                .width(chartWidth),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            netByGroup.forEach { (date, _) ->
                Text(
                    text = if (groupByMonth) date.replace("/", "-") else date.substring(5),
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray,
                    maxLines = 1
                )
            }
        }
    }
}