package com.ar.jetpackarchitecture.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ar.jetpackarchitecture.models.AccountProperties
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.models.BlogPost

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPost::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAuthTokenDAO() : AuthTokenDAO

    abstract fun getAccountPropertiesDAO() : AccountPropertiesDAO

    abstract fun getBlogPostDAO() : BlogPostDAO

    companion object{
        // static property in kotlin
        const val DATABASE_NAME = "app_db"
    }

}