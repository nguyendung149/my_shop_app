package com.example.myappshop.ui.activities.users

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivitySettingsBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.ui.activities.addresses.AddressListActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(),OnClickListener {
    private lateinit var mUserDetails:User
    private var binding:ActivitySettingsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding?.btnChangePassword?.setOnClickListener(this)
        binding?.llAddress?.setOnClickListener(this)
        binding?.btnLogout?.setOnClickListener(this)
        binding?.tvEdit?.setOnClickListener(this)
        setupActionBar()
    }
    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarSettingsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarSettingsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }
    @SuppressLint("SetTextI18n")
    fun userDetailsSuccess(user: User){
        mUserDetails = user

        hideProgressDialog()
        GliderLoader(this@SettingsActivity).loadUserPicture(user.image,binding!!.ivUserPhoto)
        binding?.tvName?.text = "${user.firstName} ${user.lastName}"
        binding?.tvBirthday?.text = user.birthDay
        binding?.tvEmail?.text = user.emaiAdd
        binding?.tvGender?.text = user.gender
        binding?.tvMobileNumber?.text = "${user.mobile} "
        binding?.tvAddress?.text = user.address
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(p0: View?) {
       if(p0 != null){
           when(p0?.id){
               binding?.btnLogout?.id -> {
                   FirebaseAuth.getInstance().signOut()
                   val intent = Intent(this@SettingsActivity, DangNhap::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                   startActivity(intent)
                   finish()
               }
               binding?.tvEdit?.id -> {
                   val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                   intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                   startActivity(intent)
               }
               binding?.llAddress?.id -> {
                   val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                   startActivity(intent)
               }
               binding?.btnChangePassword?.id -> {
                   val intent = Intent(this@SettingsActivity, ChangePasswordActivity::class.java)
                   intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                   startActivity(intent)
               }
           }
       }
    }

}