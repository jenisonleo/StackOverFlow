package com.stackoverflow.cleint.app.authentication.di

import android.content.Context

object AuthComponentProvider{

    private var authComponent:AuthComponent?=null
    fun init(context: Context){
        if(authComponent==null){
            authComponent=DaggerAuthComponent.builder().authModule(AuthModule(context)).build()
        }
    }
    fun getAuthComponent(): AuthComponent {
        return authComponent?:throw Exception("call init before getter")
    }
}