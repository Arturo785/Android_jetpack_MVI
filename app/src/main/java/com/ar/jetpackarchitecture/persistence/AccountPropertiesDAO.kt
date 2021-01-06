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
    fun insertAndReplace(accountProperties: AccountProperties) : Long // gives the row where was inserted

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties) : Long

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchByPk(pk : Int) : LiveData<AccountProperties>

    @Query("SELECT * FROM account_properties WHERE email = :email")
    fun searchByEmail(email : String) : AccountProperties?

    @Query("UPDATE account_properties SET email = :email, username = :username WHERE pk =:pk")
    fun updateAccountProperties(pk : Int, email: String, username : String)

}