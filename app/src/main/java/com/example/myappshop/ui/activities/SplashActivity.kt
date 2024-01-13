package com.example.myappshop.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myappshop.databinding.ActivitySplashBinding
import com.example.myappshop.ui.activities.users.DangNhap

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        Handler().postDelayed(
            fun() {
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                finish()
            }, 3000
        )


    }

}