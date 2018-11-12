package com.stackoverflow.cleint.app.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.stackoverflow.cleint.app.database.StackDataBase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class CommonModule(val context: Context){

    @Provides
    @CommonScope
    fun provideOkhttpInstance():OkHttpClient{
        return OkHttpClient()
    }

    @Provides
    @CommonScope
    fun provideStackDb():StackDataBase{
        return Room.databaseBuilder(context,StackDataBase::class.java,"stackdb").allowMainThreadQueries().build()//NO I18N
    }

    @Provides
    @CommonScope
    fun provideContext():Context{
        return context
    }
}