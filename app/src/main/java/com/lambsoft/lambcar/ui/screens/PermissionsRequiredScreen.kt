package com.lambsoft.lambcar.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

val ALL_BLE_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

fun haveAllPermissions(context: Context) =
    ALL_BLE_PERMISSIONS
        .all { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

@Composable
fun PermissionsRequiredScreen(onPermissionGranted: () -> Unit) {
    Box {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { granted ->
                if (granted.values.all { it }) {
                    onPermissionGranted()
                }
            }
            Button(onClick = { launcher.launch(ALL_BLE_PERMISSIONS) }) {
                Text("Grant Permission")
            }
        }
    }
}