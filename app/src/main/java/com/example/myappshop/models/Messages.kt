package com.example.myappshop.models

data class Messages constructor(
    val sender: String? = "",
    val receiver: String? = "",
    val message:String? = "",
    val time:String? = ""

) {
    val id:String get() = "${sender}-${receiver}-${message}-${time}"
}