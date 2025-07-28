package com.qali.hesabi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.height
import com.qali.hesabi.data.TransactionType
import com.qali.hesabi.data.WalletTransaction
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.qali.hesabi.util.JalaliUtils
import java.util.Date

@Composable
fun WalletCard(
    transaction: WalletTransaction,
    onEdit: (WalletTransaction) -> Unit = {},
    onDelete: (WalletTransaction) -> Unit = {}
) {
    val color = when (transaction.type) {
        TransactionType.INCOME -> Color(0xFF43A047)
        TransactionType.EXPENSE -> Color(0xFFD32F2F)
        else -> Color.Gray
    }
    val showDialog = remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = transaction.description, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = if (transaction.type == TransactionType.INCOME) "درآمد" else "هزینه", color = color)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "تاریخ: " + JalaliUtils.toJalaliString(Date(transaction.date)), style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "${transaction.amount} تومان", color = color, style = MaterialTheme.typography.titleLarge)
            Row {
                IconButton(onClick = { onEdit(transaction) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Transaction",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showDialog.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Transaction",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("تایید حذف") },
            text = { Text("آیا از حذف این تراکنش مطمئن هستید؟") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDialog.value = false
                    onDelete(transaction)
                }) {
                    Text("حذف")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDialog.value = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}