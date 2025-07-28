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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.patrykandpatrick.vico.compose.chart.line.LineChart
import com.patrykandpatrick.vico.compose.axis.rememberAxis
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.compose.component.shape.ShapeComponent
import com.patrykandpatrick.vico.compose.component.shape.Shapes
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.Axis
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart as MPLineChartView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.graphics.Color as AndroidColor

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
fun MPLineChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MPLineChartView(context).apply {
                this.setDrawGridBackground(false)
                this.axisRight.isEnabled = false
                this.description.isEnabled = false
                this.legend.isEnabled = false
                this.xAxis.position = XAxis.XAxisPosition.BOTTOM
                this.xAxis.setDrawGridLines(true)
                this.axisLeft.setDrawGridLines(true)
                this.xAxis.labelRotationAngle = 90f
                this.xAxis.granularity = 1f
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { idx, pair -> Entry(idx.toFloat(), pair.second) }
            val dataSet = LineDataSet(entries, "Net").apply {
                color = AndroidColor.BLUE
                setCircleColor(AndroidColor.RED)
                lineWidth = 2f
                circleRadius = 4f
                setDrawValues(false)
            }
            chart.data = LineData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first })
            chart.invalidate()
        }
    )
}

@Composable
fun DailyExpensesChart(transactions: List<com.qali.hesabi.data.WalletTransaction>) {
    var chartMode by remember { mutableStateOf("روزانه") }
    val modes = listOf("لحظه‌ای", "روزانه", "ماهانه")
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("سود/زیان ${chartMode}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Spacer(Modifier.weight(1f))
            Row {
                modes.forEach { mode ->
                    Button(
                        onClick = { chartMode = mode },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (chartMode == mode)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(mode)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        val netByGroup = when (chartMode) {
            "لحظه‌ای" -> {
                // Sort by date, calculate running net, label with time
                var runningNet = 0.0
                transactions.sortedBy { it.date }.map { tx ->
                    runningNet += if (tx.type == com.qali.hesabi.data.TransactionType.INCOME) tx.amount else -tx.amount
                    val timeLabel = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(tx.date))
                    timeLabel to runningNet.toFloat()
                }
            }
            "ماهانه" -> {
                val grouped = transactions.groupBy {
                    val date = java.util.Date(it.date)
                    val jalali = com.qali.hesabi.util.JalaliUtils.toJalaliString(date)
                    jalali.substring(0, 7) // yyyy/MM
                }
                grouped.map { (date, txs) ->
                    val income = txs.filter { it.type == com.qali.hesabi.data.TransactionType.INCOME }.sumOf { it.amount }
                    val expense = txs.filter { it.type == com.qali.hesabi.data.TransactionType.EXPENSE }.sumOf { it.amount }
                    val net = income - expense
                    date to net.toFloat()
                }.sortedBy { it.first }
            }
            else -> { // روزانه
                val grouped = transactions.groupBy {
                    com.qali.hesabi.util.JalaliUtils.toJalaliString(java.util.Date(it.date))
                }
                grouped.map { (date, txs) ->
                    val income = txs.filter { it.type == com.qali.hesabi.data.TransactionType.INCOME }.sumOf { it.amount }
                    val expense = txs.filter { it.type == com.qali.hesabi.data.TransactionType.EXPENSE }.sumOf { it.amount }
                    val net = income - expense
                    date to net.toFloat()
                }.sortedBy { it.first }
            }
        }

        if (netByGroup.isEmpty()) {
            Text("هیچ داده‌ای برای نمایش وجود ندارد", style = MaterialTheme.typography.bodyMedium)
        } else {
            MPLineChart(
                data = netByGroup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
        }
    }
}