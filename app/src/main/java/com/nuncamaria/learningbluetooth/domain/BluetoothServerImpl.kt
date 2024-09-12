package com.nuncamaria.learningbluetooth.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    fun release() {
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
