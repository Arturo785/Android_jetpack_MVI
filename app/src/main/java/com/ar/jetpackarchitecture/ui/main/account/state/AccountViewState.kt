package com.ar.jetpackarchitecture.ui.main.account.state

import android.os.Parcelable
import com.ar.jetpackarchitecture.models.AccountProperties
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "ACCOUNT_VIEW_STATE_BUNDLE_KEY"

@Parcelize
class AccountViewState(
    var accountProperties: AccountProperties? = null
) : Parcelable {

}