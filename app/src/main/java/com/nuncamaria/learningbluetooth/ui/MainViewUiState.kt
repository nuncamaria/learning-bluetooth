package com.nuncamaria.learningbluetooth.ui

import android.bluetooth.BluetoothDevice

data class MainViewUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isLoading: Boolean = false, // to show a spinner
    val isConnected: Boolean = false,
    val errorMessage: String? = null
)
