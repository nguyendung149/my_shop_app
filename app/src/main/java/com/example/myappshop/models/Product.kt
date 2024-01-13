package com.example.myappshop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product (
    val user_id : String = "",
    val user_name: String = "",
    val title:String = "",
    val price:String = "",
    val description:String = "",
    val stock_quantity:String = "",
    val image: String = "",
    var id:String = "",
    var type: String = ""
): Parcelable {
}