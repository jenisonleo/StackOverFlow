package com.stackoverflow.cleint.app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "stacktable")
data class StackEntity(
    @PrimaryKey
    @ColumnInfo(name = "questionId")
    var questionId: QuestionIdObject,
    @ColumnInfo(name = "questionTitle")
    var questionTitle:String,
    @ColumnInfo(name = "upvotes")
    var upvotes:Long,
    @ColumnInfo(name = "tags")
    var tags:List<String>,
    @ColumnInfo(name = "username")
    var uName:String,
    @ColumnInfo(name = "askedtime")
    var askedTime:Long,
    @ColumnInfo(name = "sorttype")
    var sortType:SortType
)


enum class SortType{
    Activity,Hot,Votes,Date
}

data class QuestionIdObject(val questionId: Long,val isOwned:Boolean,val sortType: SortType)