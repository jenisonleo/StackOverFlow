package com.stackoverflow.cleint.app.authentication

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import io.reactivex.subjects.PublishSubject

class AuthClient(val emitter: PublishSubject<Pair<AuthType, String>>) :WebViewClient(){
    override fun onPageFinished(view: WebView?, url: String?) {
        view?.clearCache(true)
        super.onPageFinished(view, url)
        if(url!=null && url.contains("https://stackoverflow.com/oauth/login_success")){
            val split = url.split("?")
            if(split.size==2){
                val params=split.get(1)
                var breaker:Boolean=false
                params.split("&").forEach {
                    if(it.contains("code")){
                        val code = it.split("=")[1]
                        Log.e("values"," "+code)
                        breaker=true
                        emitter.onNext(Pair(AuthType.AUTHCODE,code))
                    }
                }
                if(!breaker){
                    emitter.onError(Throwable("unrefereenced auth key"))//NO I18N
                }
            }else{
                emitter.onError(Throwable("unrefereenced auth key"))//NO I18N
            }
        }
    }
}