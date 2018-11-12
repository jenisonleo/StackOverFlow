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

    @Query("SELECT * FROM stacktable where sorttype =:sortType AND questionId LIKE '%true%'")
    fun allQuestionsMine(sortType: SortType):DataSource.Factory<Int,StackEntity>
    @Query("SELECT * FROM stacktable where sorttype =:sortType AND questionId LIKE '%false%'")
    fun allQuestionsPublic(sortType: SortType):DataSource.Factory<Int,StackEntity>

    @Query("DELETE FROM stacktable")
    fun deleteTable()
}