package com.lambsoft.lambcar.ui.screens


import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lambsoft.lambcar.ble.LAMBCAR_BLE_SERVICE

@Composable
fun DeviceScreen(
    unselectDevice: () -> Unit,
    isDeviceConnected: Boolean,
    discoveredCharacteristics: Map<String, List<String>>,
    connect: () -> Unit,
    discoverServices: () -> Unit,
    writeDirection: (Byte) -> Unit,
    writeSpeed: (Byte) -> Unit,
    writeTurn: (Byte) -> Unit
) {
    val foundTargetService = discoveredCharacteristics.contains(LAMBCAR_BLE_SERVICE.toString())

    Column(
        Modifier.scrollable(rememberScrollState(), Orientation.Vertical)
    ) {
        Button(onClick = connect) {
            Text("1. Connect")
        }
        Text("Device connected: $isDeviceConnected")
        Button(onClick = discoverServices, enabled = isDeviceConnected) {
            Text("2. Discover Services")
        }
        LazyColumn {
            items(discoveredCharacteristics.keys.sorted()) { serviceUuid ->
                Text(text = serviceUuid, fontWeight = FontWeight.Black)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    discoveredCharacteristics[serviceUuid]?.forEach {
                        Text(it)
                    }
                }
            }
        }

        var direction by remember { mutableStateOf(true) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Toggle Direction")
            Checkbox(
                checked = direction,
                onCheckedChange = { direction = it },
                enabled = isDeviceConnected && foundTargetService
            )
        }

        Button(onClick = {
            if (direction) {
                writeDirection(0)
            } else {
                writeDirection(1)
            }}, enabled = isDeviceConnected && foundTargetService) {
            Text("Set direction")
        }

        var speedSliderValue by remember { mutableFloatStateOf(0f) }
        val speedValue = speedSliderValue.toInt()

        Slider(
            value = speedSliderValue,
            onValueChange = { speedSliderValue = it },
            valueRange = 0f..255f,
            steps = 256
        )

        Text("Speed Value: $speedValue")

        Button(onClick = {
            writeSpeed(speedValue.toByte())
            }, enabled = isDeviceConnected && foundTargetService) {
            Text("Set speed")
        }

        var turnSliderValue by remember { mutableFloatStateOf(0f) }
        val turnValue = turnSliderValue.toInt()

        Slider(
            value = turnSliderValue,
            onValueChange = { turnSliderValue = it },
            valueRange = 0f..180f,
            steps = 180
        )

        Text("Turn Value: $turnValue")

        Button(onClick = {
            writeTurn(turnValue.toByte())
        }, enabled = isDeviceConnected && foundTargetService) {
            Text("Set turn")
        }

        OutlinedButton(modifier = Modifier.padding(top = 40.dp),  onClick = unselectDevice) {
            Text("Disconnect")
        }
    }
}
