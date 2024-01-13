package com.example.myappshop.ui.activities.products

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityAddProductBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Product
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), OnClickListener {
    private var mSelectedImageUrl:Uri? = null
    private var mProductImageURL:String = ""
    private var binding: ActivityAddProductBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()
        binding?.btnSubmit?.setOnClickListener(this)
        binding?.ivAddUpdateProduct?.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarAddProductActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0?.id) {
                binding?.ivAddUpdateProduct?.id -> {
                    if (ContextCompat.checkSelfPermission(
                            this@AddProductActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@AddProductActivity, arrayOf(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }

                }
                binding?.btnSubmit?.id -> {
                    if(validateProductDetails()){
//                        showErrorSnackBar("You product details are valid",false)
                        uploadProductImage()
                    }
                }
                else -> {

                }

            }
        }
    }
    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_LONG
        ).show()
        finish()
    }
    fun imageUploadSuccess(imageUrl:String){
        hideProgressDialog()
//        showErrorSnackBar("Product image is uploaded successful. ImageURL: $imageUrl",false)4
        mProductImageURL = imageUrl
        uploadProductDetails()
    }
    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.MYSHOPAPP_PREFERENCES,Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME,"")!!
        val product = Product(
            FirestoreClass().getCurrentUserId(),
            username,
            binding?.etProductTitle?.text.toString().trim {  it <= ' ' },
            binding?.etProductPrice?.text.toString().trim { it <= ' '},
            binding?.etProductDescription?.text.toString().trim { it <= ' '},
            binding?.etProductQuantity?.text
                .toString().trim {  it <= ' ' },
            mProductImageURL
        )
        FirestoreClass().uploadProductDetails(this,product)

    }
    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this@AddProductActivity,mSelectedImageUrl,Constants.PRODUCT_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@AddProductActivity)
            }
        }else {
            Toast.makeText(
                this@AddProductActivity,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data!=null){
                    binding?.ivAddUpdateProduct?.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddProductActivity,
                            R.drawable.baseline_edit_24

                        )
                    )
                    try {
                        mSelectedImageUrl = data.data!!
                        GliderLoader(this@AddProductActivity).loadUserPicture(mSelectedImageUrl!!,binding?.ivProductImage!!)
                    }catch (error:IOException){
                        error.printStackTrace()
                    }
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){

            Log.e("Request Cancelled","Image selection cancelled")
        }
    }
    private fun validateProductDetails():Boolean {
        return when {
            mSelectedImageUrl == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image),true)
                false
            }
            TextUtils.isEmpty(binding?.etProductTitle?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title),true)
                false
            }
            TextUtils.isEmpty(binding?.etProductPrice?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price),true)
                false
            }
            TextUtils.isEmpty(binding?.etProductDescription?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description),true)
                false
            }
            TextUtils.isEmpty(binding?.etProductQuantity?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity),true)
                false
            }
            else -> {
                true
            }
        }
    }
}