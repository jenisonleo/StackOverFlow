package com.stackoverflow.cleint.app.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import java.io.IOException

class AuthViewModel():ViewModel(){


    var currentFlipperChild:Int=0
    var webClient:AuthClient
    var authsuccessSubject:PublishSubject<Pair<AuthType,String>>
    init{
        authsuccessSubject = PublishSubject.create<Pair<AuthType,String>>()
        webClient=AuthClient(authsuccessSubject)
    }

    fun generateOauth(code:String,clientId:String,clientSecret:String,redirectUri:String,newOauthApi:String){
        val cliwent= OkHttpClient()
        val builder = FormBody.Builder()
        builder.add("client_id",clientId)
        builder.add("client_secret",clientSecret)
        builder.add("code",code)
        builder.add("redirect_uri",redirectUri)
        cliwent.newCall(Request.Builder().url(newOauthApi).post(builder.build()).build())
            .enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {

                }
                override fun onResponse(call: Call, response: Response) {
                    val authtoken=String( response.body()?.bytes()?:throw Exception("unable to parse response")).split("=").get(1)
                    Log.e("authtoken"," "+authtoken)
                    authsuccessSubject.onNext(Pair(AuthType.AUTHTOKEN,authtoken))
                }

            })
    }

}
enum class AuthType{
    AUTHCODE,AUTHTOKEN
}