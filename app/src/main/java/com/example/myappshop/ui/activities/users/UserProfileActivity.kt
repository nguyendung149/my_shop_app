package com.example.myappshop.ui.activities.users

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityUserProfileBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.ui.activities.DashboardActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), OnClickListener {
    private var binding: ActivityUserProfileBinding? = null
    private lateinit var mUserInfo: User
    private var mUserProfileImageUrl: String = ""
    private var mSelectedImageFileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
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
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserInfo = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
        }
        binding?.etEmail?.isEnabled = false
        binding?.etEmail?.setText(mUserInfo.emaiAdd)
        binding?.etFirstName?.setText(mUserInfo.firstName)
        binding?.etLastName?.setText(mUserInfo.lastName)
        if(mUserInfo.profileCompleted == 0){
            binding?.tvTitle?.text = resources.getString(R.string.title_complete_profile)
            binding?.etFirstName?.isEnabled = false

            binding?.etLastName?.isEnabled = false


            binding?.etEmail?.isEnabled = false
            binding?.etEmail?.setText(mUserInfo.emaiAdd)
        }else {
            binding?.etFirstName?.isEnabled = true
            binding?.etLastName?.isEnabled = true
            setupActionBar()
            binding?.tvTitle?.text = resources.getString(R.string.title_edit_profile)
            GliderLoader(this).loadUserPicture(mUserInfo.image,binding?.ivUserPhoto!!)


            if(mUserInfo.mobile != ""){
                binding?.etMobileNumber?.setText(mUserInfo.mobile)
            }
            if(mUserInfo.birthDay != ""){
                binding?.etBirthday?.setText(mUserInfo.birthDay)
            }
            if(mUserInfo.address != ""){
                binding?.etAdd?.setText(mUserInfo.address)
            }
            if(mUserInfo.gender == Constants.MALE){
                binding?.rbMale?.isChecked = true
            }else {
                binding?.rbFemale?.isChecked = true
            }

        }

        binding?.btnSubmit?.setOnClickListener(this@UserProfileActivity)
        binding?.ivUserPhoto?.setOnClickListener(this@UserProfileActivity)
    }
    private fun setupActionBar() {
        setSupportActionBar(binding!!.toolbarUserProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding!!.toolbarUserProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0?.id) {
                binding?.ivUserPhoto?.id -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //showErrorSnackBar("You already have the storage permission", false)
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                binding?.btnSubmit?.id -> {

                    if (validateUserProfileDetail()) {
                        //showErrorSnackBar("Your details are valid. You can update them",false)
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (mSelectedImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.USER_PROFILE_AVATAR)
                        } else {
                            updateUserProfileDetails()
                        }

                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val mobilePhone:String =
            binding?.etMobileNumber?.text.toString().trim { it <= ' ' }
        val birthday = binding?.etBirthday?.text.toString().trim { it <= ' ' }
        val address = binding?.etAdd?.text.toString().trim { it <= ' ' }

        val firstName: String = binding?.etFirstName?.text.toString().trim {it <= ' '}
        val lastName: String = binding?.etLastName?.text.toString().trim {it<=' ' }

        if(mUserInfo.firstName != firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        if(lastName != mUserInfo.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }


        val gender = if (binding?.rbMale!!.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(mUserProfileImageUrl.isNotEmpty()){
            userHashMap[Constants.USER_AVATAR] = mUserProfileImageUrl
        }

        if (mobilePhone.isNotEmpty() && mobilePhone != mUserInfo.mobile) {
            userHashMap[Constants.MOBILE] = mobilePhone
        }
        if(gender.isNotEmpty() && gender != mUserInfo.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        if (birthday.isNotEmpty() && mUserInfo.birthDay != birthday) {
            userHashMap[Constants.BIRTHDAY] = birthday
        }
        if(address.isNotEmpty() && mUserInfo.address != address) {
            userHashMap[Constants.ADDRESS] = address
        }

        //Cap nhat day du thong tin nguoi dung
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        //showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showErrorSnackBar("The storage permission is granted.",false)
                Constants.showImageChooser(this)

            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        //binding?.ivUserPhoto?.setImageURI(selectedImageFileUri)
                        GliderLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding?.ivUserPhoto!!
                        )
                        showErrorSnackBar("Success", false)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }
        }
    }

    fun validateUserProfileDetail(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.etMobileNumber?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_phone_number), true)
                false
            }

            TextUtils.isEmpty(binding?.etBirthday?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_birthday), true
                )
                false
            }

            TextUtils.isEmpty(binding?.etAdd?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_address), true)
                false
            }

            else -> {
                true
            }
        }

    }

    fun imageUploadSuccess(imageUri: String) {
        //hideProgressDialog()
        mUserProfileImageUrl = imageUri
        updateUserProfileDetails()
    }


}

