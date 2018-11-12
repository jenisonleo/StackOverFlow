package com.stackoverflow.cleint.app.authentication

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ViewFlipper
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.stackoverflow.cleint.app.authentication.api.AuthenticationApi
import com.stackoverflow.cleint.app.authentication.di.AuthComponentProvider
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.DisplayMetrics
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.webkit.CookieManager
import android.webkit.WebSettings
import java.net.CookieStore


class AuthenticationFragment(): Fragment(){
    lateinit var viewmodel:AuthViewModel
    @Inject
    lateinit var authenticationApi: AuthenticationApi
    companion object {
        const val TAG = "AuthenticationFragment"//NO I18N

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!(activity is LoginStatusInterface)){
            throw Exception("calling acitvity should implement LoginStatusInterface")
        }
        AuthComponentProvider.getAuthComponent().inject(this)
        viewmodel=ViewModelProviders.of(this).get(AuthViewModel::class.java)
        retainInstance=true
        val disppsbale = viewmodel.authsuccessSubject.subscribeBy(onNext = {
            when(it.first) {
                AuthType.AUTHCODE -> {
                    //todo show loader
                    if(view!=null){
                        (view as ViewFlipper).findViewById<ViewFlipper>(R.id.login_flipper).displayedChild=2
                        viewmodel.currentFlipperChild=2
                    }
                    val res=context?.resources?:throw Exception()
                    viewmodel.generateOauth(it.second,res.getString(R.string.clientid),res.getString(R.string.clientsecret),res.getString(R.string.redirecturi),res.getString(R.string.newoauthapi))
                }
                AuthType.AUTHTOKEN -> {
                    authenticationApi.writeOauthoken(it.second)
                    GlobalScope.launch(Dispatchers.Main) {
                        (activity as LoginStatusInterface).loginSuccessful()
                    }
                }
            }
        }, onError = {

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.authentication_layout, container, false)
        val webview = inflate.findViewById<WebView>(R.id.login_webview)
        webview.getSettings().javaScriptEnabled = true
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webview.webViewClient=viewmodel.webClient
        val bannerTitle = inflate.findViewById<TextView>(R.id.banner_title)
        val wordtoSpan = SpannableString(bannerTitle.text)
        wordtoSpan.setSpan(AbsoluteSizeSpan(150), 0, bannerTitle.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        wordtoSpan.setSpan(StyleSpan(Typeface.BOLD), 5, bannerTitle.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        bannerTitle.setText(wordtoSpan)
        val context=getContext()?:throw Exception()
        webview.loadUrl(context.resources.getString(R.string.oauthapi)+"client_id="+context.resources.getString(R.string.clientid)+"&redirect_uri="+context.resources.getString(R.string.redirecturi)+"&scope=no_expiry")
        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val findViewById = view.findViewById<ViewFlipper>(R.id.login_flipper)

        findViewById.displayedChild=viewmodel.currentFlipperChild
        val displayMetrics = DisplayMetrics()
        (activity?:throw Exception()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val tasnsIn = TranslateAnimation(width.toFloat(),0f,0f,0f)
        tasnsIn.interpolator = AccelerateInterpolator()
        tasnsIn.duration = 300
        val tasnsOut = TranslateAnimation(0f,width.toFloat(),0f,0f)
        tasnsOut.interpolator = AccelerateInterpolator()
        tasnsOut.duration=300
        findViewById.inAnimation=tasnsIn
        findViewById.outAnimation=tasnsOut
        view.findViewById<CardView>(R.id.signin).setOnClickListener {
            viewmodel.currentFlipperChild=1
            findViewById.displayedChild=1
        }
        view.findViewById<TextView>(R.id.skip_signin).setOnClickListener {
            (activity as LoginStatusInterface).skipSignin()
        }

    }
}

interface LoginStatusInterface{
    fun loginSuccessful()
    fun skipSignin()
}