package com.nuncamaria.learningbluetooth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuncamaria.learningbluetooth.domain.BluetoothServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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

    init {
        // to update the ui
        bluetoothServer.isDeviceConnected.onEach { isConnected ->
            _state.update { it.copy(isDeviceConnected = isConnected) }
        }.launchIn(viewModelScope)

        bluetoothServer.errors.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)
    }

    fun startScan() {
        bluetoothServer.startServer()
    }

    fun stopScan() {
        bluetoothServer.stopServer()
    }
}