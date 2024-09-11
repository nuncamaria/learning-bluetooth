package com.nuncamaria.learningbluetooth.ui

import androidx.lifecycle.ViewModel
import com.nuncamaria.learningbluetooth.data.LearningBtRepository

class LearningBtViewModel(private val repository: LearningBtRepository) : ViewModel() {

val appTitle = "Learning Bluetooth"
}