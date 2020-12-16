package com.ar.jetpackarchitecture.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ar.jetpackarchitecture.models.AuthToken

@Dao
interface AuthTokenDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authToken: AuthToken) : Long

    /*For logging out*/
    @Query("UPDATE auth_token SET token = null WHERE account_pk = :pk")
    fun nullifyToken (pk : Int) : Int
    
}