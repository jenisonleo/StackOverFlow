package com.stackoverflow.cleint.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(StackEntity::class),version = 1)
@TypeConverters(DataTypeConverter::class)
abstract class StackDataBase():RoomDatabase(){

    abstract fun getStackDao():StackDao
}