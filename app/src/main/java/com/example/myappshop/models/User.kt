package com.example.myappshop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
class User constructor(
    val id:String = "",
    val firstName: String = "",
    val lastName: String  = "",
    val emaiAdd :String = "",
    val image: String = "",
    val mobile: String   = "",
    val gender: String = "",
    val birthDay: String = "",
    val address: String = "",
    val fcmToken:String = "",
    val profileCompleted:Int = 0,
    val isAdministrator:Int = 0,
):Parcelable