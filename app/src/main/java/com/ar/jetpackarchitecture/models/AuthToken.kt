package com.ar.jetpackarchitecture.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = AccountProperties::class,
            parentColumns = ["pk"], //Where the info comes
            childColumns = ["account_pk"], // where is it stored
            onDelete = CASCADE
        )
    ]
)
data class AuthToken(
    @PrimaryKey
    var account_pk : Int? = -1,

    @Expose
    var token : String? = null,


)