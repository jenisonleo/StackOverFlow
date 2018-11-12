package com.stackoverflow.cleint.app.authentication.di

import android.content.Context
import com.stackoverflow.cleint.app.authentication.AuthenticatorImpl
import com.stackoverflow.cleint.app.authentication.api.AuthenticationApi
import dagger.Module
import dagger.Provides

@Module
class AuthModule(val context: Context) {

    @Provides
    @AuthScope
    fun provideAuthApi():AuthenticationApi{
        return AuthenticatorImpl(context)
    }
}