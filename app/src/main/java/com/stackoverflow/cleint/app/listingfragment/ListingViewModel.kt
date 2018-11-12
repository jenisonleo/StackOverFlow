package com.stackoverflow.cleint.app.listingfragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.JsonParser
import com.stackoverflow.cleint.app.authentication.api.AuthenticationApi
import com.stackoverflow.cleint.app.database.DataSource
import com.stackoverflow.cleint.app.database.QuestionIdObject
import com.stackoverflow.cleint.app.database.SortType
import com.stackoverflow.cleint.app.database.StackEntity
import com.stackoverflow.cleint.app.dependencyinjection.CommonComponent
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import javax.inject.Inject

class ListingViewModel(commonComponent: CommonComponent) :ViewModel(){

    @Inject
    lateinit var okHttpClient:OkHttpClient
    @Inject
    lateinit var authenticationApi: AuthenticationApi
    val dataSource:DataSource
    var mainFlipperIndex:Int=0
    var logoutDialogShowing=false

    init {
        commonComponent.inject(this)
        dataSource=DataSource(commonComponent)
    }

    private fun fetchQuestions(key:String,baseurl:String,sortType: SortType,pageIndex:Int,isPersonal:Boolean){
        var url=baseurl+"questions?site=stackoverflow"+"&key="+key+"&filter=!OfZjfMY3eWzEtMb(pv5mYAM-sznYJt5lkv0M_PFhgVj&order=desc"
        if(isPersonal){
            url=url.plus("&access_token="+authenticationApi.getOauthToken())
        }
        when(sortType){
            SortType.Activity->url=url.plus("&sort=activity")
            SortType.Date->url=url.plus("&sort=creation")
            SortType.Hot->url=url.plus("&sort=hot")
            SortType.Votes->url=url.plus("&sort=votes")
        }
        url=url.plus("&page="+pageIndex.toString())
        Log.e("url"," "+url)
        val request=Request.Builder()
        request.url(url)
        okHttpClient.newCall(request.build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val objec = JsonParser()
                    val parse = objec.parse(response.body()?.string())
                    val get = parse.asJsonObject.get("items").asJsonArray
                    val dataList = mutableListOf<StackEntity>()
                    for (i in 0..(get.size() - 1)) {
                        val element = get.get(i)
                        val questionID: Long = element.asJsonObject.get("question_id").asLong
                        val title = element.asJsonObject.get("title").asString
                        val upvotes = element.asJsonObject.get("up_vote_count").asLong
                        val tagElement = element.asJsonObject.get("tags").asJsonArray
                        val tag = mutableListOf<String>()
                        for (j in 0..(tagElement.size() - 1)) {
                            tag.add(tagElement.get(j).asString)
                        }
                        val name = element.asJsonObject.get("owner").asJsonObject.get("display_name").asString
                        val time = element.asJsonObject.get("creation_date").asLong
                        val lastactivity = element.asJsonObject.get("last_activity_date").asLong

                        val stackEntity = StackEntity(
                            QuestionIdObject(questionID, isPersonal, sortType),
                            title,
                            upvotes,
                            tag,
                            name,
                            time,
                            lastactivity,
                            sortType
                        )
                        dataList.add(stackEntity)
                    }
                    dataSource.writedataToDb(dataList)
                    if (dataList.size > 0) {
                        dataSource.updatePagesLoaded(parse.asJsonObject.get("page").asInt, sortType, isPersonal)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }


        })
    }

    fun getPublicData(clentkey:String,baseUrl:String,sortType: SortType,isPersonal:Boolean): LiveData<PagedList<StackEntity>> {
        val sortString:String
        when(sortType){
            SortType.Votes->sortString="upvotes"
            SortType.Activity->sortString="lastactivitytime"
            SortType.Date->sortString="askedtime"
            SortType.Hot->sortString="lastactivitytime"
        }

        val livePagedListBuilder:LivePagedListBuilder<Int,StackEntity> =LivePagedListBuilder<Int,StackEntity>(dataSource.stackdb.getStackDao().allQuestions(sortType,if(isPersonal)"%true%" else "%false%",sortString),30)
        livePagedListBuilder.setBoundaryCallback(object :PagedList.BoundaryCallback<StackEntity>(){
            override fun onItemAtEndLoaded(itemAtEnd: StackEntity) {
                Log.e("end"," reached")
                fetchQuestions(clentkey,baseUrl,sortType,dataSource.getPagesLoaded(sortType,isPersonal)+1,isPersonal)
            }
        })

        return livePagedListBuilder.build()
    }

    fun doIntialChecksforMine(clentkey:String,baseUrlMine:String,baseUrlPublic:String){
        fetchType(clentkey,baseUrlMine,SortType.Date,true)
        fetchType(clentkey,baseUrlMine,SortType.Activity,true)
        fetchType(clentkey,baseUrlMine,SortType.Votes,true)

    }
    fun doIntialChecksforPublic(clentkey:String,baseUrlMine:String,baseUrlPublic:String){
        fetchType(clentkey,baseUrlPublic,SortType.Date,false)
        fetchType(clentkey,baseUrlPublic,SortType.Activity,false)
        fetchType(clentkey,baseUrlPublic,SortType.Votes,false)
        fetchType(clentkey,baseUrlPublic,SortType.Hot,false)

    }

    private fun fetchType(clentkey:String,baseUrl:String,sortType: SortType,isPersonal:Boolean){
        if(dataSource.getPagesLoaded(sortType,isPersonal)==0){
            fetchQuestions(clentkey,baseUrl,sortType,1,isPersonal)
        }
    }


    fun logout(baseUrl:String): Single<Unit> {
        logoutDialogShowing=true
        return Single.create<Unit> {
            val ref=authenticationApi.getOauthToken()
            okHttpClient.newCall(Request.Builder().url(baseUrl+"access-tokens/${ref}/invalidate").build()).enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    logoutDialogShowing=false
                    GlobalScope.launch(Dispatchers.Main) {
                        it.onError(e)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try{
                        val string = response.body()?.string()
                        val objec=JsonParser()
                        val key = objec.parse(string).asJsonObject.get("items").asJsonArray.get(0).asJsonObject.get("access_token").asString?:throw Exception("invalid response")//NO I18N
                        if(key.equals(ref)){
                            authenticationApi.doLogout()
                            dataSource.clearTable()
                            it.onSuccess(Unit)
                        }else{
                            throw Exception("invalid response")
                        }
                        logoutDialogShowing=false
                    }catch (e:Exception){
                        GlobalScope.launch(Dispatchers.Main) {
                            it.onError(e)
                        }
                    }
                }

            })
        }
    }

}
