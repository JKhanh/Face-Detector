package com.stp.faced.di

import com.stp.faced.CameraViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CameraViewModel() }
}