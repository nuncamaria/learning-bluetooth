package com.nuncamaria.learningbluetooth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuncamaria.learningbluetooth.domain.BluetoothServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val bluetoothServer: BluetoothServer) : ViewModel() {

    val appTitle = "Learning Bluetooth"

    private val _state = MutableStateFlow(MainViewUiState())
    val state = combine(
        bluetoothServer.scannedDevices,
        bluetoothServer.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun startScan() {
        bluetoothServer.startServer()
    }

    fun stopScan() {
        bluetoothServer.stopServer()
    }
}