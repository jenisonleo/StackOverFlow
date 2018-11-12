package com.stackoverflow.cleint.app.authentication.api

interface AuthenticationApi{
    fun isSignedIn():Boolean=false

    fun getOauthToken():String

    fun writeOauthoken(token:String)

    fun doLogout()
}