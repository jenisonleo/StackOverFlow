package com.stackoverflow.cleint.app.listingfragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import com.stackoverflow.cleint.app.R
import com.stackoverflow.cleint.app.database.StackEntity
import kotlinx.android.synthetic.main.recyclerview_adapter.view.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class ListingAdapter(val context: Context,callback:DiffUtil.ItemCallback<StackEntity>): PagedListAdapter<StackEntity,ListingHolder>(callback){

    val dateFormat:SimpleDateFormat
    var toast: Toast?=null
    var resources:Resources
    init {
        dateFormat = SimpleDateFormat("MM/dd/yyyy")
        resources=context.resources
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingHolder {
        val t=LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_adapter,parent,false)
        return ListingHolder(t)
    }


    override fun onBindViewHolder(holder: ListingHolder, position: Int) {
        val itemm = getItem(position)
        holder.titleContainer.setText(itemm?.questionTitle)
        holder.upVoteContainer.text = itemm?.upvotes.toString()
        holder.nameContainer.setText(itemm?.uName)
        val date = Date((itemm?:throw Exception()).askedTime*1000L)
        holder.timeContainer.setText(dateFormat.format(date))
        for(i in 0..(itemm.tags.size-1)){
            val textView= TextView(holder.itemView.context)
            val p=FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,100)
            p.setMargins(15,15,15,15)
            textView.background=context.resources.getDrawable(R.drawable.tag_bg,null)
            textView.setText(itemm.tags.get(i))
            textView.gravity=Gravity.CENTER
            textView.layoutParams=p
            textView.setOnClickListener{
                if(toast!=null){
                    toast?.cancel()
                }
                toast=Toast.makeText(holder.itemView.context,itemm.tags.get(i),Toast.LENGTH_SHORT)
                toast?.show()
            }
            holder.tagContainer.addView(textView)
        }
    }

}


class ListingHolder(view: View):RecyclerView.ViewHolder(view){
    val titleContainer:TextView
    val upVoteContainer:TextView
    val nameContainer:TextView
    val timeContainer:TextView
    val tagContainer:FlexboxLayout
    init {
        titleContainer = view.title_container
        upVoteContainer=view.upvote_container
        nameContainer=view.name_container
        timeContainer=view.time_container
        tagContainer=view.tag_container
        tagContainer.flexWrap=FlexWrap.WRAP
        tagContainer.justifyContent=JustifyContent.CENTER
    }
}