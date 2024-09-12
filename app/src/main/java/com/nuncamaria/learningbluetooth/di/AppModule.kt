package com.nuncamaria.learningbluetooth.di

import com.nuncamaria.learningbluetooth.data.LearningBtRepository
import com.nuncamaria.learningbluetooth.data.LearningBtRepositoryImpl
import com.nuncamaria.learningbluetooth.domain.BluetoothServer
import com.nuncamaria.learningbluetooth.domain.BluetoothServerImpl
import com.nuncamaria.learningbluetooth.ui.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<BluetoothServer> { BluetoothServerImpl(androidApplication()) }
    single<LearningBtRepository> { LearningBtRepositoryImpl() }
    viewModel { MainViewModel(get()) }
}