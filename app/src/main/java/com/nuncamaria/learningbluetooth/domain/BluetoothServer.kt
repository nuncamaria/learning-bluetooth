package com.nuncamaria.learningbluetooth.domain

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
object BluetoothServer {

    // hold reference to app context to run the chat server
    private var app: Application? = null

    private val bluetoothManager by lazy {
        app?.getSystemService(BluetoothManager::class.java)
    }

    // If the app is installed on an emulator without bluetooth then the app will crash
    // on launch since installing via Android Studio bypasses the <uses-feature> flags
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices = _pairedDevices.asStateFlow()

    init {
        updatePairedDevices()
    }

    fun startServer(app: Application) {
        //    si inicializara el bluetoothManager como lateinit tendría que poner esto aquí:
        //    bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        if (!hasPermission(android.Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        // and if we had the scan permission lets:
        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    fun stopServer() {
    }

    private fun updatePairedDevices() {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)) {
            bluetoothAdapter?.bondedDevices.also {
                _pairedDevices.update { it }
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return app?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}