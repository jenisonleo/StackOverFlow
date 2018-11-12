package com.stackoverflow.cleint.app.database

import android.util.Log
import androidx.room.TypeConverter

class DataTypeConverter{

    @TypeConverter
    fun sortTypeToString(sortType: SortType):String{
        return sortType.name
    }

    @TypeConverter
    fun stringToSortType(sorttype:String):SortType{
        if(sorttype.equals(SortType.Votes.name)){
            return SortType.Votes
        }else if(sorttype.equals(SortType.Activity.name)){
            return SortType.Activity
        }else if(sorttype.equals(SortType.Date.name)){
            return SortType.Date
        }else{
            return SortType.Hot
        }
    }
    @TypeConverter
    fun questionIdObject(questionIdObject: QuestionIdObject):String{
        val str= questionIdObject.questionId.toString(10)+"_"+questionIdObject.isOwned.toString()+"_"+questionIdObject.sortType.name
        return str
    }
    @TypeConverter
    fun objecttoQuestionId(questionId: String):QuestionIdObject{
        val split = questionId.split("_")
        if(split.size!=3){
            throw Exception("irrelevant data")//NO I18N
        }
        return QuestionIdObject(split.get(0).toLong(),split.get(1).toBoolean(), SortType.valueOf(split.get(2)))
    }

    @TypeConverter
    fun listToString(data:List<String>):String{
        var tag:String=""
        if(data.size>0){
            for(j in 0..(data.size-1)){
                tag=tag.plus(data.get(j))
                if(j!=(data.size-1)){
                    tag=tag.plus(",")
                }
            }
        }
        return tag
    }

    @TypeConverter
    fun stringtoList(data:String):List<String>{
        val list= mutableListOf<String>()
        if(data.equals("")){
            return list
        }else {
            return data.split(",")
        }
    }
}