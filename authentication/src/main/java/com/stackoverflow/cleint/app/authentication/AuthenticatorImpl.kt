package com.stackoverflow.cleint.app.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.stackoverflow.cleint.app.authentication.api.AuthenticationApi
@SuppressLint("ApplySharedPref")
class AuthenticatorImpl(context: Context):AuthenticationApi{

    val fileName="authdata"//NO I18N
    val authKey="stackAuth"//NO I18N
    val preference:SharedPreferences
    init {
        preference=context.getSharedPreferences(fileName,Context.MODE_PRIVATE)
    }

    override fun isSignedIn(): Boolean {
        return if(preference.getString(authKey, null)==null) false else true
    }

    override fun getOauthToken(): String {
        val string = preference.getString(authKey, null)
        return string?:throw Exception("get authtoken called before login")//NO I18N
    }
    override fun writeOauthoken(token: String) {
        val editor = preference.edit()
        editor.putString(authKey,token)
        editor.commit()
    }

    override fun doLogout() {
        val edit = preference.edit()
        edit.remove(authKey)
        edit.commit()
    }
}