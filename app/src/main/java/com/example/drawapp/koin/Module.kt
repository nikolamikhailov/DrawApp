package com.example.drawapp.koin

import android.content.ContentResolver
import com.example.drawapp.ViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        ViewModel(get())
    }
    single { androidApplication().contentResolver }
}
