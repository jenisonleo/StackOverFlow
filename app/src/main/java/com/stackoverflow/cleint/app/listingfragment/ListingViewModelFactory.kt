package com.stackoverflow.cleint.app.listingfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stackoverflow.cleint.app.dependencyinjection.CommonComponent

class ListingViewModelFactory(val commonComponent: CommonComponent):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListingViewModel(commonComponent) as T
    }
}