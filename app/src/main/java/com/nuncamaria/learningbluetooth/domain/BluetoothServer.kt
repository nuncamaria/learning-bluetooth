package com.nuncamaria.learningbluetooth.domain

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothServer {
    val isDeviceConnected: StateFlow<Boolean>

    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    val errors: SharedFlow<String>

    // Start and stop looking for devices
    fun startServer()
    fun stopServer()

    // Pair and unpair devices
    // Needs to be a flow because we want to keep on listening events as long as we have Bluetooth connection established
    fun startConnection(): Flow<DeviceConnectionResult>
    fun pairToDevice(device: BluetoothDevice): Flow<DeviceConnectionResult>
    fun closeConnection()

    // Release device
    fun unregisterReceiver()
}