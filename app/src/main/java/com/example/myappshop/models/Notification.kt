package com.example.myappshop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Notification constructor(
    var user_id : String = "",
    var user_name: String ="",
    var owner_id : String = "",
    var product_id: String = "",
    var time: Long = 0,
    var user_image: String = "",
    var id: String = "",
) : Parcelable {
}