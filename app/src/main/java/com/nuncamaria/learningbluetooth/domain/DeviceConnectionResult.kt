package com.nuncamaria.learningbluetooth.domain

sealed interface DeviceConnectionResult {

    data object Connected : DeviceConnectionResult
    data object Disconnected : DeviceConnectionResult
    data class Error(val message: String) : DeviceConnectionResult
}