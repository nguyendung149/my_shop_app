package com.example.myappshop.ui.activities.users

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityDangnhapBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.ui.activities.DashboardActivity
import com.example.myappshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class DangNhap : BaseActivity(), View.OnClickListener {
    private var binding: ActivityDangnhapBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangnhapBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding?.signup?.setOnClickListener(this)
        binding?.signin?.setOnClickListener(this)
        binding?.getPass?.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0?.id) {
                binding?.getPass?.id -> {
                    startActivity(Intent(this@DangNhap, ForgotPasswordActivity::class.java))
                }

                binding?.signin?.id -> {
                    login()

                }

                binding?.signup?.id -> {
                    startActivity(Intent(this@DangNhap, DangKy::class.java))

                }

            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.etUserAccount?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding?.etPassword?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            else -> {
               // showErrorSnackBar("Your details are valid", false)
                true

            }
        }
    }

    private fun login() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding?.etUserAccount?.text.toString().trim { it <= ' ' }
            val passWord: String = binding?.etPassword?.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, passWord)
                .addOnCompleteListener { task ->
                    run {

                        if (task!!.isSuccessful) {
                           FirestoreClass().getUserDetails(this@DangNhap)


                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }

                    }
                }

        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()


        if(user.profileCompleted == 0){
            val intent:Intent = Intent(this@DangNhap, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else {
            startActivity(Intent(this@DangNhap, DashboardActivity::class.java))
        }
        finish()
    }

}