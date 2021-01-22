package com.ar.jetpackarchitecture.models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "account_properties")
data class AccountProperties(

    @Expose // this is for retrofit
    @PrimaryKey(autoGenerate = false)
    var pk: Int,

    @Expose
    var email: String,

    @Expose
    var username: String,

) : Parcelable