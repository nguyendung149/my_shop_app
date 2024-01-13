package com.example.myappshop.ui.activities.users

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityChangePasswordBinding
import com.example.myappshop.ui.activities.BaseActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()
        binding.btnChangePasswordActivity.setOnClickListener {
            updatePassword()
        }
    }

    private fun validateChangePasswordDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding.etOldPassword.text.toString()
                    .trim { it <= ' ' }) || binding.etOldPassword.text.toString()
                .trim { it <= ' ' }.length < 8 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_old_password), true)
                false
            }

            TextUtils.isEmpty(
                binding.etNewPassword.text.toString()
                    .trim { it <= ' ' }) || binding.etNewPassword.text.toString()
                .trim { it <= ' ' }.length < 8 -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_a_new_password), true
                )
                false
            }

            TextUtils.isEmpty(
                binding.etConfirmNewPassword.text.toString()
                    .trim { it <= ' ' }) || binding.etConfirmNewPassword.text.toString()
                .trim { it <= ' ' }.length < 8 -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password), true
                )
                false
            }

            binding.etNewPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmNewPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }

            else -> {
                true
                //showErrorSnackBar(resources.getString(R.string.text_register_successful), false)

            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.changePasswordViewToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.changePasswordViewToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updatePassword() {
        if (validateChangePasswordDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val oldPassword = binding.etOldPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()

            var currentUser = FirebaseAuth.getInstance().currentUser!!
            var authCredential =
                currentUser.email?.let { EmailAuthProvider.getCredential(it, oldPassword) }
            currentUser.reauthenticate(authCredential!!)
                .addOnSuccessListener {
                    hideProgressDialog()
                    currentUser.updatePassword(newPassword).addOnCompleteListener {
                        showErrorSnackBar(
                            "Password was changed successfully",
                            false
                        )
                    }
                    val intent = android.content.Intent(
                        this@ChangePasswordActivity,
                        com.example.myappshop.ui.activities.users.DangNhap::class.java
                    )
                    intent.flags =
                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK or android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    android.os.Handler().postDelayed(
                        fun() {
                            startActivity(intent)
                        },
                        5000
                    )
                }
                .addOnFailureListener { exception ->
                    hideProgressDialog()
                    showErrorSnackBar(
                        exception!!.message.toString(),
                        true
                    )

                }

        }
    }
}