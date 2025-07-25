package com.hesabi.util

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun RequestStoragePermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted() else onDenied()
    }
    val shouldRequest = remember { mutableStateOf(false) }

    LaunchedEffect(shouldRequest.value) {
        if (shouldRequest.value) {
            launcher.launch(permission)
            shouldRequest.value = false
        }
    }

    LaunchedEffect(Unit) {
        PermissionRequester.request = { shouldRequest.value = true }
    }
}

object PermissionRequester {
    var request: (() -> Unit)? = null
}