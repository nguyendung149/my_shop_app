package com.example.myappshop.ui.activities.users

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityDangKyBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DangKy : BaseActivity() {
    private lateinit var binding: ActivityDangKyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangKyBinding.inflate(layoutInflater)
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
        binding.btndangki.setOnClickListener {
            registerUserAccount()
        }
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this@DangKy, DangNhap::class.java))
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.searchViewToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.searchViewToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(binding.etLastname.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPass1.text.toString().trim { it <= ' ' })
                    || binding.etPass1.text.toString().trim { it <= ' ' }.length < 8 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.etPass2.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            binding.etPass1.text.toString().trim { it <= ' ' } != binding.etPass2.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }

            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }

            else -> {
                //showErrorSnackBar(resources.getString(R.string.text_register_successful), false)
                true
            }
        }
    }

    private fun registerUserAccount() {
        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val passWord: String = binding.etPass1.text.toString().trim { it <= ' ' }
            //Tạo instance và tạo đăng ký bằng password and email
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passWord)
                .addOnCompleteListener { task ->
                    run {
                        //Nếu đăng ký thành công
                        if (task.isSuccessful) {
                            //Đăng ký thêm trên Firebase
                            val firebaseUserAccount: FirebaseUser = task.result!!.user!!
                            val user = User(
                                firebaseUserAccount.uid,
                                binding.etFirstName.text.toString().trim { it<=' ' },
                                binding.etLastname.text.toString().trim{ it<= ' '},
                                binding.etEmail.text.toString().trim {it<=' '},
                            )

                            FirestoreClass().registerUser(this@DangKy,user)

//                            FirebaseAuth.getInstance().signOut()
//                            finish()

                        } else {
                            hideProgressDialog()
                            //Đăng ký không thành công
                            showErrorSnackBar(
                                task.exception!!.message.toString(),
                                true
                            )
                        }

                    }
                }

        }
    }
    fun userRegisterSuccess(){
        hideProgressDialog()
        Toast.makeText(this@DangKy,resources.getString(R.string.text_status_successful_register),Toast.LENGTH_LONG).show()
    }

}