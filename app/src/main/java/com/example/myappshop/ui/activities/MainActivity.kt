package com.example.myappshop.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityMainBinding
import com.example.myappshop.utils.Constants

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val sharedPreferences = this.getSharedPreferences(Constants.MYSHOPAPP_PREFERENCES, Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!
        binding!!.tvMain.text = "Hello $userName."

    }
}