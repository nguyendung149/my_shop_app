package com.example.myappshop.ui.activities.users

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityForgotPasswordBinding
import com.example.myappshop.ui.activities.BaseActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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
        setupActionBar()
    }
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarForgotPassword)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        binding.toolbarForgotPassword.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.buttonSendEmail.setOnClickListener {
            val emailAdd: String = binding.editTextEmail.text.toString().trim { it <= ' ' }
            if (emailAdd.isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAdd)
                    .addOnCompleteListener { task ->
                        run {
                            hideProgressDialog()
                            if (task.isSuccessful) {
                                Toast.makeText(this@ForgotPasswordActivity,
                                    resources.getString(R.string.text_send_mail_successful),
                                    Toast.LENGTH_LONG
                                ).show()

                                finish()
                            } else {
                                showErrorSnackBar(task.exception!!.message.toString(), true)
                            }

                        }
                    }
            }

        }

    }


}