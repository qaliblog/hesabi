package com.qali.hesabi.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.navigation.NavController
import androidx.compose.ui.unit.dp
import com.qali.hesabi.data.WalletTransaction
import com.qali.hesabi.data.TransactionType
import com.qali.hesabi.ui.WalletTransactionViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AddWalletTransactionScreen(navController: NavController, walletTransactionViewModel: WalletTransactionViewModel, transactionId: Int? = null) {
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(0) } // 0: INCOME, 1: EXPENSE
    var description by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }
    var transactionDate by remember { mutableStateOf(System.currentTimeMillis()) } // Add this line

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            val transaction = withContext(Dispatchers.IO) { walletTransactionViewModel.getTransactionById(transactionId) }
            transaction?.let {
                amount = it.amount.toInt().toString()
                type = if (it.type == TransactionType.INCOME) 0 else 1
                description = it.description
                isEdit = true
                transactionDate = it.date // preserve date for edit
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isEdit) "ویرایش تراکنش" else "افزودن تراکنش جدید", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() } },
            label = { Text("مبلغ (تومان)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = type == 0,
                onClick = { type = 0 }
            )
            Text("درآمد")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = type == 1,
                onClick = { type = 1 }
            )
            Text("هزینه")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("توضیحات") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val amt = amount.toDoubleOrNull() ?: 0.0
                if (amt > 0.0 && description.isNotBlank()) {
                    coroutineScope.launch {
                        if (isEdit && transactionId != null) {
                            walletTransactionViewModel.update(
                                WalletTransaction(
                                    id = transactionId,
                                    amount = amt,
                                    type = if (type == 0) TransactionType.INCOME else TransactionType.EXPENSE,
                                    description = description,
                                    date = transactionDate // use preserved date
                                )
                            )
                        } else {
                            // date will default to now
                            walletTransactionViewModel.insert(
                                WalletTransaction(
                                    amount = amt,
                                    type = if (type == 0) TransactionType.INCOME else TransactionType.EXPENSE,
                                    description = description
                                )
                            )
                        }
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره تراکنش")
        }
    }
}