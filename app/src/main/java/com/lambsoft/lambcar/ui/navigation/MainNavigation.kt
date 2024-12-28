package com.lambsoft.lambcar.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lambsoft.lambcar.ui.screens.DeviceScreen
import com.lambsoft.lambcar.ui.screens.PermissionsRequiredScreen
import com.lambsoft.lambcar.ui.screens.ScanningScreen
import com.lambsoft.lambcar.ui.screens.haveAllPermissions
import com.lambsoft.lambcar.ui.viewmodel.LambcarViewModel

@SuppressLint("MissingPermission")
@Composable
fun MainNavigation(viewModel: LambcarViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var allPermissionsGranted by remember {
        mutableStateOf (haveAllPermissions(context))
    }

    if (!allPermissionsGranted) {
        PermissionsRequiredScreen { allPermissionsGranted = true }
    }
    else if (uiState.activeDevice == null) {
        ScanningScreen(
            isScanning = uiState.isScanning,
            foundDevices = uiState.foundDevices,
            startScanning = viewModel::startScanning,
            stopScanning = viewModel::stopScanning,
            selectDevice = { device ->
                viewModel.stopScanning()
                viewModel.setActiveDevice(device)
            }
        )
    }
    else {
        DeviceScreen(
            unselectDevice = {
                viewModel.disconnectActiveDevice()
                viewModel.setActiveDevice(null)
            },
            isDeviceConnected = uiState.isDeviceConnected,
            discoveredCharacteristics = uiState.discoveredCharacteristics,
            connect = viewModel::connectActiveDevice,
            writeDirection = viewModel::writeDirection,
            writeSpeed = viewModel::writeSpeed,
            writeTurn = viewModel::writeTurn
        )
    }
}