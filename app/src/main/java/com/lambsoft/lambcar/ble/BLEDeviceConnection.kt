package com.lambsoft.lambcar.ble


import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID


val LAMBCAR_BLE_SERVICE: UUID = UUID.fromString("39d50000-9668-4b22-927c-f57eb67f8a77")
val DIRECTION_CHARACTERISTIC_UUID: UUID = UUID.fromString("39d50001-9668-4b22-927c-f57eb67f8a77")
val SPEED_CHARACTERISTIC_UUID: UUID = UUID.fromString("39d50002-9668-4b22-927c-f57eb67f8a77")
val TURN_CHARACTERISTIC_UUID: UUID = UUID.fromString("39d50003-9668-4b22-927c-f57eb67f8a77")

@Suppress("DEPRECATION")
class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice
) {
    val isConnected = MutableStateFlow(false)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    @OptIn(ExperimentalStdlibApi::class)
    private val callback = object: BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val connected = newState == BluetoothGatt.STATE_CONNECTED
            if (connected) {
                //read the list of services
                services.value = gatt.services
            }
            isConnected.value = connected
        }

        @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            services.value = gatt.services
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }
    }

    private var gatt: BluetoothGatt? = null

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connect() {
        gatt = bluetoothDevice.connectGatt(context, false, callback)
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverServices() {
        gatt?.discoverServices()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeDirection(value: Byte) {
        val service = gatt?.getService(LAMBCAR_BLE_SERVICE)
        val characteristic = service?.getCharacteristic(DIRECTION_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            characteristic.value = byteArrayOf(value)
            val success = gatt?.writeCharacteristic(characteristic)
            Log.v("bluetooth", "Write direction status: $success")
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeSpeed(value: Byte) {
        val service = gatt?.getService(LAMBCAR_BLE_SERVICE)
        val characteristic = service?.getCharacteristic(SPEED_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            characteristic.value = byteArrayOf(value)
            val success = gatt?.writeCharacteristic(characteristic)
            Log.v("bluetooth", "Write speed status: $success")
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeTurn(value: Byte) {
        val service = gatt?.getService(LAMBCAR_BLE_SERVICE)
        val characteristic = service?.getCharacteristic(TURN_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            characteristic.value = byteArrayOf(value)
            val success = gatt?.writeCharacteristic(characteristic)
            Log.v("bluetooth", "Write turn status: $success")
        }
    }


}