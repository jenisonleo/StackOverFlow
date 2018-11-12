package com.stackoverflow.cleint.app.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StackDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addQuestions(questions: List<StackEntity>)

    @Query("SELECT * FROM stacktable where sorttype =:sortType AND questionId LIKE :isOwned ORDER BY :sort DESC")
    fun allQuestions(sortType: SortType, isOwned:String,sort:String):DataSource.Factory<Int,StackEntity>

    @Query("DELETE FROM stacktable")
    fun deleteTable()
}