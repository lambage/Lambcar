package com.lambsoft.lambcar.ui.screens


import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lambsoft.lambcar.R
import com.lambsoft.lambcar.ble.LAMBCAR_BLE_SERVICE

@Composable
fun LandscapeOnlyComposable(content: @Composable() () -> Unit) {
    val activity = LocalContext.current as? Activity

    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    unselectDevice: () -> Unit,
    isDeviceConnected: Boolean,
    discoveredCharacteristics: Map<String, List<String>>,
    connect: () -> Unit,
    writeDirection: (Byte) -> Unit,
    writeSpeed: (Byte) -> Unit,
    writeTurn: (Byte) -> Unit
) {
    val foundTargetService = discoveredCharacteristics.contains(LAMBCAR_BLE_SERVICE.toString())

    if (!isDeviceConnected) {
        connect()
    }

    LandscapeOnlyComposable(){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isDeviceConnected) {
                Text("Unable to connect, try again...")
            } else if (!foundTargetService) {
                Text("Connecting to service...")
            } else {
                SteeringWheelSlider(imageResource = R.drawable.steering_wheel, writeTurn = writeTurn)
            }
            OutlinedButton(modifier = Modifier.padding(top = 40.dp),  onClick = unselectDevice) {
                Text("Disconnect")
            }
        }
    }

    /*
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
     */
}

@Composable
fun SteeringWheelSlider(
    modifier: Modifier = Modifier,
    imageResource: Int, // or use a URL String if fetching from network
    writeTurn: (Byte) -> Unit,
) {
    var rotationAngle by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        rotationAngle = (rotationAngle + dragAmount.x / 5).coerceIn(-90f, 90f)
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageResource)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Steering Wheel",
            modifier = Modifier
                .size(200.dp)
                .rotate(rotationAngle),
            contentScale = ContentScale.Fit
        )
    }
    //Text(text = "Rotation: ${rotationAngle.toInt()}")
    val turnAngle = (rotationAngle + 90).toInt().toByte()
    writeTurn(turnAngle)
}
