package com.nuncamaria.learningbluetooth.di

import com.nuncamaria.learningbluetooth.data.LearningBtRepository
import com.nuncamaria.learningbluetooth.data.LearningBtRepositoryImpl
import com.nuncamaria.learningbluetooth.ui.LearningBtViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<LearningBtRepository> { LearningBtRepositoryImpl() }
    viewModel { LearningBtViewModel(get()) }
}