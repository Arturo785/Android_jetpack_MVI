package com.ar.jetpackarchitecture.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ar.jetpackarchitecture.models.AccountProperties

@Dao
interface AccountPropertiesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReplace(accountProperties: AccountProperties) : Long // gives the row where was inserted

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(accountProperties: AccountProperties) : Long

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    suspend fun searchByPk(pk : Int) : AccountProperties

    @Query("SELECT * FROM account_properties WHERE email = :email")
    suspend fun searchByEmail(email : String) : AccountProperties?

    @Query("UPDATE account_properties SET email = :email, username = :username WHERE pk =:pk")
    suspend fun updateAccountProperties(pk : Int, email: String, username : String)

}