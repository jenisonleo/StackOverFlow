package com.stackoverflow.cleint.app.authentication.di

import com.stackoverflow.cleint.app.authentication.AuthenticationFragment
import com.stackoverflow.cleint.app.authentication.api.AuthenticationApi
import dagger.Component

@Component(modules = arrayOf(AuthModule::class))
@AuthScope
interface AuthComponent{
    fun provideAuthApi():AuthenticationApi
    fun inject(authenticationFragment: AuthenticationFragment)


}