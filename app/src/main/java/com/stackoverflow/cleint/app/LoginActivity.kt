package com.stackoverflow.cleint.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stackoverflow.cleint.app.authentication.AuthenticationFragment
import com.stackoverflow.cleint.app.authentication.LoginStatusInterface

class LoginActivity : AppCompatActivity(), LoginStatusInterface {
    companion object {
        val skipkey="isskipSingnin"
        val newlogin="newlogin"
    }
    override fun skipSignin() {
        val intent=Intent(this,ListingActivity::class.java)
        intent.putExtra(skipkey,true)
        startActivity(intent)
        finish()
    }

    override fun loginSuccessful() {
        val intent=Intent(this,ListingActivity::class.java)
        intent.putExtra(newlogin,true)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(supportFragmentManager.findFragmentByTag(AuthenticationFragment.TAG)==null){
            val authFragment=AuthenticationFragment()
            supportFragmentManager.beginTransaction().add(R.id.container,authFragment,AuthenticationFragment.TAG).commit()
        }
    }


}

