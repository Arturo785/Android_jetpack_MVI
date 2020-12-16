package com.ar.jetpackarchitecture.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ar.jetpackarchitecture.models.AccountProperties
import com.ar.jetpackarchitecture.models.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAuthTokenDAO() : AuthTokenDAO

    abstract fun getAccountPropertiesDAO() : AccountPropertiesDAO

    companion object{

        // static property in kotlin
        const val DATABASE_NAME = "app_db"


    }

}