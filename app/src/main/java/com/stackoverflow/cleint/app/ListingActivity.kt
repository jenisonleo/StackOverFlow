package com.stackoverflow.cleint.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stackoverflow.cleint.app.authentication.di.AuthComponentProvider
import com.stackoverflow.cleint.app.listingfragment.ListingFragment
import com.stackoverflow.cleint.app.listingfragment.ListingInterface

class ListingActivity: AppCompatActivity(),ListingInterface{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AuthComponentProvider.init(this.applicationContext)
        if(intent.hasExtra(LoginActivity.skipkey) && intent.extras.getBoolean(LoginActivity.skipkey,false)){
            openFragment()
        }else if(!AuthComponentProvider.getAuthComponent().provideAuthApi().isSignedIn()){
            launchLogin()
        }else{
            openFragment()
        }
    }

    fun openFragment(){
        if(supportFragmentManager.findFragmentByTag(ListingFragment.TAG)==null) {
            val listingFragment=ListingFragment()
            if(intent.hasExtra(LoginActivity.newlogin) && intent.extras.getBoolean(LoginActivity.newlogin,false)){
                val bundle=Bundle()
                bundle.putBoolean(ListingFragment.onlogin,true)
                listingFragment.arguments=bundle
            }
            supportFragmentManager.beginTransaction().add(R.id.container, listingFragment, ListingFragment.TAG).commit()
        }
    }

    fun launchLogin(){
        val intent= Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun loginRequested() {
        launchLogin()
    }

}