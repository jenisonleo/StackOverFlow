package com.stackoverflow.cleint.app.listingfragment

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.stackoverflow.cleint.app.LoginActivity
import com.stackoverflow.cleint.app.R
import com.stackoverflow.cleint.app.dependencyinjection.ListingActivityViewModel
import com.stackoverflow.cleint.app.dependencyinjection.ListingActivityViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.listing_fragment.view.*
import kotlin.Exception

class ListingFragment: Fragment(){

    lateinit var viewModel:ListingViewModel
    companion object {
        const val TAG = "ListingFragment"//NO I18N
        const val onlogin="onlogin"//NO I18N
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!(activity is ListingInterface)){
            throw Exception("calling activity should implement ListingInterface.kt")//NO I18N
        }
        retainInstance=true
        setHasOptionsMenu(true)
        createViewModel()
        //do safety clearance for new Login
        if(arguments!=null && arguments!!.getBoolean(onlogin,false)){
            viewModel.dataSource.clearTable()
        }
        doIntialFetch()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.listing_fragment, container, false)
        val mainFlipper=view.main_flipper
        mainFlipper.displayedChild=viewModel.mainFlipperIndex
        //setup personal view
        if(!viewModel.authenticationApi.isSignedIn()){
            view.own_flipper.displayedChild=1
        }
        val personalPager=view.own_listing_pager
        personalPager.offscreenPageLimit=2
        personalPager.adapter=ListingPagerAdapter(context?: throw Exception("bounded activity is null. Couldn't create fragment"),viewModel,this,true)//NO I18N
        val personalTab=view.own_listing_tab
        personalTab.setupWithViewPager(personalPager)

        //setup public view
        val publicPager = view.findViewById<ViewPager>(R.id.public_listing_pager)
        publicPager.offscreenPageLimit=3
        publicPager.adapter=ListingPagerAdapter(context?: throw Exception("bounded activity is null. Couldn't create fragment"),viewModel,this,false)//NO I18N
        val publicTab = view.findViewById<TabLayout>(R.id.public_listing_tab)
        publicTab.setupWithViewPager(publicPager)


        view.sign_in_prompt.setOnClickListener {
            (activity as ListingInterface).loginRequested()
        }
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_personal->{
                    viewModel.mainFlipperIndex=0
                    mainFlipper.displayedChild=0
                    animate(mainFlipper,mainFlipper.width/4)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.item_public->{
                    viewModel.mainFlipperIndex=1
                    mainFlipper.displayedChild=1
                    animate(mainFlipper,((mainFlipper.width*3)/4))
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(viewModel.logoutDialogShowing){
            alertDialog=createDialog()
            alertDialog?.show()
        }
        view.swiperefresh.setOnRefreshListener {
            viewModel.dataSource.clearTable()
            doIntialFetch()
            view.swiperefresh.isRefreshing=false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_bar_menu, menu);
        if(!viewModel.authenticationApi.isSignedIn()){
            menu?.findItem(R.id.logout)?.setEnabled(false)
            menu?.findItem(R.id.logout)?.icon?.alpha=125
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    var alertDialog:AlertDialog?=null
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.logout->{
                alertDialog=createDialog()
                alertDialog?.show()
                viewModel.logout(context?.resources?.getString(R.string.baseurl)?:throw Exception()).subscribeOn(AndroidSchedulers.mainThread()).subscribeBy(onSuccess = {
                    val activity=activity
                    if(activity!=null) {
                        alertDialog?.dismiss()
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                        activity.finish()
                    }
                },onError = {
                    it.printStackTrace()
                    if(activity!=null) {
                        alertDialog?.dismiss()
                        Toast.makeText(context, context?.resources?.getString(R.string.logout_error), Toast.LENGTH_LONG)
                            .show()
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item);
    }

    fun createViewModel(){
        val commonComponent = ViewModelProviders.of(activity ?: throw Exception("bounded activity is null. Couldn't create fragment"),ListingActivityViewModelFactory(context?: throw Exception("bounded activity is null. Couldn't create fragment"))).get(ListingActivityViewModel::class.java).commonComponent//NO I18N
        viewModel = ViewModelProviders.of(this, ListingViewModelFactory(commonComponent)).get(ListingViewModel::class.java)
    }

    private fun doIntialFetch(){
        val context=context
        if(context==null){
            throw Exception()
        }
        if(!viewModel.authenticationApi.isSignedIn()){
            viewModel.doIntialChecksforPublic(context.resources.getString(R.string.clientkey),context.resources.getString(R.string.baseurlmine),context.resources.getString(R.string.baseurl))
        }else{
            viewModel.doIntialChecksforMine(context.resources.getString(R.string.clientkey),context.resources.getString(R.string.baseurlmine),context.resources.getString(R.string.baseurl))
            viewModel.doIntialChecksforPublic(context.resources.getString(R.string.clientkey),context.resources.getString(R.string.baseurlmine),context.resources.getString(R.string.baseurl))
        }
    }


    private fun createDialog(): AlertDialog {
        val builder: AlertDialog.Builder = (activity?:throw Exception()).let {
            AlertDialog.Builder(it)
        }

        builder.setMessage(R.string.logout_message)
            .setTitle(R.string.logout_dialog_tile)
        return builder.create()
    }

    private fun animate(view:View,w:Int){
        val h=view.height
        val circularAnimation=ViewAnimationUtils.createCircularReveal(view,w,h,0.toFloat(),h.toFloat())
        circularAnimation.duration=300
        circularAnimation.start()
    }

}

interface ListingInterface{
    fun loginRequested()
}