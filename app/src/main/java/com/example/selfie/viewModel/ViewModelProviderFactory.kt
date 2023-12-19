package com.example.selfie.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.selfie.repository.Repository

class ViewModelProviderFactory(val repository: Repository,val context:Context):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SelfieViewModel(repository,context) as T
    }

}