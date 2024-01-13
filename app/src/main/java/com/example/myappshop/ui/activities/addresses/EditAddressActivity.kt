package com.example.myappshop.ui.activities.addresses

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityEditAddressBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Address
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants

class EditAddressActivity : BaseActivity() {
    private var mAddressDetails: Address? = null
    private lateinit var binding: ActivityEditAddressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
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
        if(intent.hasExtra(Constants.EXTRA_ADDRESS_DETAIL)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAIL)
        }
        if(mAddressDetails != null) {
            if(mAddressDetails!!.id.isNotEmpty()){
                binding.tvTitle.text = resources.getString(R.string.title_edit_address)
                binding.btnSubmitAddress.text = resources.getString(R.string.btn_lbl_update)

                binding.etFullName.setText(mAddressDetails!!.name)
                binding.etPhoneNumber.setText(mAddressDetails!!.mobileNumber)
                binding.etAddress.setText(mAddressDetails!!.address)
                binding.etZipCode.setText(mAddressDetails!!.zipCode)
                binding.etAdditionalNote.setText(mAddressDetails!!.additionalNote)

                when(mAddressDetails!!.type){
                    Constants.HOME -> {
                        binding.rbHome.isChecked = true
                    }
                    Constants.OFFICE -> {
                        binding.rbOffice.isChecked = true
                    }
                    else -> {
                        binding.rbOther.isChecked = true
                        binding.etOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mAddressDetails!!.otherDetails)
                    }
                }

            }
        }
        binding.btnSubmitAddress.setOnClickListener {
            saveAddressOnFireStore()
        }
        binding.rgType.setOnCheckedChangeListener { radioGroup, checkState ->
            if(checkState == binding.rbOther.id){
                binding.tilOtherDetails.visibility = View.VISIBLE
            }else {
                binding.tilOtherDetails.visibility = View.GONE
            }

        }


        setupActionBar()
    }
    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFullName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }
    fun saveAddressOnFireStore(){
        val fullName: String = binding.etFullName.text.toString().trim { it <= ' ' }
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim { it <= ' ' }
        val address: String = binding.etAddress.text.toString().trim { it <= ' ' }
        val zipCode: String = binding.etZipCode.text.toString().trim { it <= ' ' }
        val additionalNote: String = binding.etAdditionalNote.text.toString().trim { it <= ' ' }
        val otherDetails: String = binding.etOtherDetails.text.toString().trim { it <= ' ' }
        if (validateData()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    Constants.HOME
                }

                binding.rbOffice.isChecked -> {
                    Constants.OFFICE
                }

                else -> {
                    Constants.OTHERS
                }
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                FirestoreClass().updateAddressOnFireStore(this@EditAddressActivity,
                    addressModel,mAddressDetails!!.id)
            }else{
                FirestoreClass().addAddressOnFireStore(this@EditAddressActivity,addressModel)
            }


        }
    }
    fun addAddressSuccess(){
        hideProgressDialog()

        val notifySuccessMessage:String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
            resources.getString(R.string.msg_your_address_updated_successfully)
        }else {
            resources.getString(R.string.err_your_address_added_successfully)
        }
        Toast.makeText(
            this@EditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_LONG
        ).show()
        setResult(RESULT_OK)
        finish()
    }
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarEditAddressActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarEditAddressActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}