package com.nuncamaria.learningbluetooth.ui

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuncamaria.learningbluetooth.domain.BluetoothServer
import com.nuncamaria.learningbluetooth.domain.DeviceConnectionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel(private val bluetoothServer: BluetoothServer) : ViewModel() {

    val appTitle = "Learning Bluetooth"

    private var deviceConnectionJob: Job? = null

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
            _state.update { it.copy(isConnected = isConnected) }
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

    fun connectToDevice(device: BluetoothDevice) {
        _state.update { it.copy(isLoading = true) }
        deviceConnectionJob = bluetoothServer
            .pairToDevice(device)
            .listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothServer.closeConnection()
        _state.update { it.copy(isLoading = false, isConnected = false) }
    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(isLoading = true) }
        deviceConnectionJob = bluetoothServer
            .startConnection()
            .listen()
    }

    private fun Flow<DeviceConnectionResult>.listen(): Job = onEach { result ->
        when (result) {
            DeviceConnectionResult.Connected -> {
                _state.update {
                    it.copy(
                        isConnected = true,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }

            DeviceConnectionResult.Disconnected -> TODO()
            is DeviceConnectionResult.Error -> {
                _state.update {
                    it.copy(
                        isConnected = false,
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }.catch { cause: Throwable ->
        bluetoothServer.closeConnection()
        _state.update {
            it.copy(
                isConnected = false,
                isLoading = false
            )
        }
    }.launchIn(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        bluetoothServer.unregisterReceiver()
    }
}