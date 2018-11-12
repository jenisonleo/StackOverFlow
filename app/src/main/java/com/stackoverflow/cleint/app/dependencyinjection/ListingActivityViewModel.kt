package com.stackoverflow.cleint.app.dependencyinjection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stackoverflow.cleint.app.authentication.di.AuthComponentProvider

class ListingActivityViewModel(context: Context):ViewModel() {
    val commonComponent: CommonComponent

    init {
        commonComponent = DaggerCommonComponent.builder().commonModule(CommonModule(context))
            .authComponent(AuthComponentProvider.getAuthComponent()).build()
    }

}

class ListingActivityViewModelFactory(val context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListingActivityViewModel(context) as T
    }

}