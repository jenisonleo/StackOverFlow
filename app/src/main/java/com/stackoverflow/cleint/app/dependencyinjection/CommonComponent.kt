package com.stackoverflow.cleint.app.dependencyinjection

import com.stackoverflow.cleint.app.authentication.di.AuthComponent
import com.stackoverflow.cleint.app.database.DataSource
import com.stackoverflow.cleint.app.listingfragment.ListingViewModel
import dagger.Component
import okhttp3.OkHttpClient

@Component(modules = arrayOf(CommonModule::class),dependencies = arrayOf(AuthComponent::class))
@CommonScope
interface CommonComponent{

    fun provideOkhttp():OkHttpClient
    fun inject(listingViewModel: DataSource)
    fun inject(listingViewModel: ListingViewModel)
}