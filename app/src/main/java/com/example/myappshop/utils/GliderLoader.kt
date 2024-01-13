package com.example.myappshop.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myappshop.R
import java.io.IOException

class GliderLoader(val context: Context) {
    fun loadUserPicture(image: Any,imageView:ImageView){
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun loadProductPicture(image: Any, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(imageView)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

}