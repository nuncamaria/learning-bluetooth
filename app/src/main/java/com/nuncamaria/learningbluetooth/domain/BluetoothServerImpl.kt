package com.nuncamaria.learningbluetooth.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import java.io.IOException

@SuppressLint("MissingPermission")
class BluetoothServerImpl(private val ctx: Context) : BluetoothServer {

    private val bluetoothManager by lazy {
        ctx.getSystemService(BluetoothManager::class.java)
    }

    // If the app is installed on an emulator without bluetooth then the app will crash
    // on launch since installing via Android Studio bypasses the <uses-feature> flags
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val scannedDevices = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val pairedDevices = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            if (device in devices) devices else devices
        }
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    init {
        updatePairedDevices()
    }

    override fun startServer() {
        //    si inicializara el bluetoothManager como lateinit tendría que poner esto aquí:
        //    bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        if (!hasPermission(android.Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        ctx.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        // and if we had the scan permission lets:
        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopServer() {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        bluetoothAdapter?.cancelDiscovery()
    }


    // Two main functions to start Bluetooth are startConnection() and pairToDevice(device)
    override fun startConnection(): Flow<DeviceConnectionResult> = flow {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)) {
            throw SecurityException("Error: No BLUETOOTH_CONNECT permission, ::startConnection")
        }

        // After check the permission, we need to use the bluetoothAdapter to launch the server
        currentServerSocket =
            bluetoothAdapter?.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID)

        // Now we want to keep on looking for connections as long as we want to accept connections
        var shouldLoop = true
        while (shouldLoop) {
            currentClientSocket = try {
                currentServerSocket?.accept() // this line returns a BluetoothSocket, por eso igualamos nuestro currentClientSocket a este resultado, necesitamos saber si la conexión se ha aceptado o ha de cerrarse
            } catch (e: IOException) {
                shouldLoop = false
                null
            }
            emit(DeviceConnectionResult.Connected)
            currentClientSocket?.let {
                currentServerSocket?.close()
            }
        }
    }.onCompletion {
        closeConnection()
    }.flowOn(Dispatchers.IO) // IO Thread is the right one for a Bluetooth connection

    override fun pairToDevice(device: BluetoothDevice): Flow<DeviceConnectionResult> = flow {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)) {
            throw SecurityException("Error: No BLUETOOTH_CONNECT permission, ::pairToDevice")
        }

        currentClientSocket = device.createRfcommSocketToServiceRecord(SERVICE_UUID)
        stopServer()

        currentClientSocket?.let { socket ->
            try {
                socket.connect()
                emit(DeviceConnectionResult.Connected)
            } catch (e: IOException) {
                socket.close()
                currentClientSocket = null
                emit(DeviceConnectionResult.Error("Error: Connection interrupted"))
            }
        }
    }.onCompletion {
        closeConnection()
    }.flowOn(Dispatchers.IO) // IO Thread is the right one for a Bluetooth connection

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun unregisterReceiver() {
        ctx.unregisterReceiver(foundDeviceReceiver)
    }

    private fun updatePairedDevices() {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }

        bluetoothAdapter?.bondedDevices.also {
            _pairedDevices.update { it }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}
