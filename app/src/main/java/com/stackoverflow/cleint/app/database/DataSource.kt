package com.stackoverflow.cleint.app.database

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.stackoverflow.cleint.app.dependencyinjection.CommonComponent
import javax.inject.Inject

class DataSource(commonComponent: CommonComponent) {
    val fileName="pagedata"//NO I18N
    @Inject
    lateinit var stackdb:StackDataBase
    @Inject
    lateinit var context:Context
    val preference:SharedPreferences
    init {
        commonComponent.inject(this)
        preference=context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun writedataToDb(dataList:List<StackEntity>){
        stackdb.beginTransaction()
        stackdb.getStackDao().addQuestions(dataList)
        stackdb.setTransactionSuccessful()
        stackdb.endTransaction()
    }

    fun getPagesLoaded(sortType: SortType,isPersonal:Boolean):Int{
       return preference.getInt(getString(sortType,isPersonal),0)
    }

    @SuppressLint("ApplySharedPref")
    fun updatePagesLoaded(pageSize:Int, sortType: SortType, isPersonal: Boolean){
        if(pageSize<=0)throw Exception()
        val edit = preference.edit()
        edit.putInt(getString(sortType,isPersonal),pageSize)
        edit.commit()
    }

    private fun getString(sortType: SortType,isPersonal:Boolean)=sortType.name+"_"+isPersonal.toString()


    fun clearTable(){
        stackdb.beginTransaction()
        stackdb.getStackDao().deleteTable()
        stackdb.setTransactionSuccessful()
        stackdb.endTransaction()
        val edit = preference.edit()
        edit.clear()
        edit.commit()
    }

}