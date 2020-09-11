package com.example.consumerapp.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id : Int = 0,
    var login: String = "",
    var html_url: String = "",
    var avatar_url : String = "",
    var FavStatus : Int = 0
): Parcelable
