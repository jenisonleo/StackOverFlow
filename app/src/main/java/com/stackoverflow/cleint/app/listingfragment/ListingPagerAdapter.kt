package com.stackoverflow.cleint.app.listingfragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stackoverflow.cleint.app.R
import com.stackoverflow.cleint.app.database.SortType
import com.stackoverflow.cleint.app.database.StackEntity


class ListingPagerAdapter(
    val context: Context,
    val viewModel: ListingViewModel,
    val lifecycleOwner: LifecycleOwner,
    val isPersonal: Boolean
):PagerAdapter(){
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun getCount()=if(isPersonal) 3 else 4

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflate = LayoutInflater.from(container.context).inflate(R.layout.pager_item, container, false)
        val recyclerView=inflate.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter=ListingAdapter(context,object : DiffUtil.ItemCallback<StackEntity>() {
            override fun areItemsTheSame(oldItem: StackEntity, newItem: StackEntity): Boolean {
                return oldItem.questionId.questionId.equals(newItem.questionId.questionId)
            }

            override fun areContentsTheSame(oldItem: StackEntity, newItem: StackEntity): Boolean {
                return oldItem.questionId.questionId.equals(newItem.questionId.questionId)
            }

        })
        var sortType:SortType?=null
        when(position){
            0-> sortType=SortType.Activity
            1->sortType=SortType.Date
            2->sortType=SortType.Votes
            3->sortType=SortType.Hot
        }
        if(sortType==null){
            throw Exception()
        }
        viewModel.getPublicData(context.resources.getString(R.string.clientkey),if (isPersonal) context.resources.getString(R.string.baseurlmine) else context.resources.getString(R.string.baseurl),sortType,isPersonal).observe(lifecycleOwner, Observer {
            Log.e("data got"," "+it.size)
            adapter.submitList(it)
        })
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(container.context)
        container.addView(inflate)
        return inflate
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val res=context.resources
        when(position){
            0-> return res.getString(R.string.activity_sort)
            1->return res.getString(R.string.creation_sort)
            2->return res.getString(R.string.votes_sort)
            3->return res.getString(R.string.hot_sort)
        }
        return "title"
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}