package com.nuncamaria.learningbluetooth.domain

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothServer {

    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    // Start and stop looking for devices
    fun startServer()
    fun stopServer()

    // Pair and unpair devices
    // Needs to be a flow because we want to keep on listening events as long as we have Bluetooth connection established
    fun startConnection(): Flow<DeviceConnectionResult>
    fun pairToDevice(device: BluetoothDevice): Flow<DeviceConnectionResult>
    fun closeConnection(): Flow<DeviceConnectionResult>

    // Release device
    fun unregisterReceiver()
}