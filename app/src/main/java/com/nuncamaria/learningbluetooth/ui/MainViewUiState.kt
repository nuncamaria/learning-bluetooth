package com.nuncamaria.learningbluetooth.ui

import android.bluetooth.BluetoothDevice

data class MainViewUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList()
)
