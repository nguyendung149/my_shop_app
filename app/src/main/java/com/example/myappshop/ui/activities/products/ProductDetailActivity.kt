package com.example.myappshop.ui.activities.products

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityProductDetailBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.CartProductItem
import com.example.myappshop.models.Product
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.ui.activities.message.ChatLogActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailActivity : BaseActivity(), OnClickListener {
    private var mProductID: String = ""
    private var mProductOwnerID: String = ""
    lateinit var mProductDetails: Product
    lateinit var mUser: User
    lateinit var mIntent: Intent
    private var binding: ActivityProductDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
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
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product ID", mProductID)
        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerID = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        if (FirestoreClass().getCurrentUserId().equals(mProductOwnerID)) {
            binding?.btnAddToCart?.visibility = View.GONE
            binding?.btnGoToCart?.visibility = View.GONE
        } else {

            binding?.btnAddToCart?.visibility = View.VISIBLE
        }
        getProductDetails()
        binding?.btnGoToCart?.setOnClickListener(this@ProductDetailActivity)
        binding?.btnAddToCart?.setOnClickListener(this@ProductDetailActivity)
        binding?.tvProductDetailsMore?.setOnClickListener(this@ProductDetailActivity)
        binding?.btnChatWithShop?.setOnClickListener(this@ProductDetailActivity)

    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this@ProductDetailActivity, mProductID)
    }

    @SuppressLint("SetTextI18n")
    fun productDetailsSuccess(product: Product) {
//        hideProgressDialog()
        mProductDetails = product
        FirestoreClass().getUserDetailShop(this@ProductDetailActivity,mProductDetails.user_id)
        GliderLoader(this@ProductDetailActivity).loadProductPicture(
            product.image,
            binding?.ivProductDetailImage!!
        )
        FirestoreClass().calAverRatingScore(mProductID, this@ProductDetailActivity)
        binding?.tvProductDetailsTitle?.text = product.title
        binding?.tvProductDetailsPrice?.text = "${product.price} VND"
        binding?.tvProductDetailsDescription?.text = product.description
        binding?.tvProductDetailsStockQuantity?.text = product.stock_quantity
        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            binding?.btnAddToCart?.visibility = View.GONE
            binding?.tvProductDetailsStockQuantity?.text =
                resources.getString(R.string.lbl_out_of_stock)
            binding?.tvProductDetailsStockQuantity?.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailActivity,
                    R.color.colorSnackBarError
                )
            )
            binding?.tvProductDetailsStockQuantity?.typeface =
                Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")

        } else {
            if (FirestoreClass().getCurrentUserId() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkProductItemExistInCart(this@ProductDetailActivity, mProductID)
            }
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarProductDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarProductDetailsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun addToCart() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val cartProductItem = CartProductItem(
            FirestoreClass().getCurrentUserId(),
            mProductOwnerID,
            mProductID,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CARD_QUANTITY,
        )
        FirestoreClass().addCartItem(this@ProductDetailActivity, cartProductItem)

    }

    fun addToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_LONG
        ).show()
        binding?.btnAddToCart?.visibility = View.GONE
        binding?.btnGoToCart?.visibility = View.VISIBLE
    }

    fun productExistInCartList() {
        hideProgressDialog()
        binding?.btnAddToCart?.visibility = View.GONE
        binding?.btnGoToCart?.visibility = View.VISIBLE
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0?.id) {
                binding?.btnAddToCart?.id -> {
                    addToCart()
                }

                binding?.btnGoToCart?.id -> {
                    val intent: Intent =
                        Intent(this@ProductDetailActivity, CartProductListActivity::class.java)

                    startActivity(intent)
                }

                binding?.tvProductDetailsMore?.id -> {
                    val intent = Intent(this@ProductDetailActivity, CommentActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, mProductID)
                    startActivity(intent)
                }

                binding?.btnChatWithShop?.id -> {

                    startActivity(mIntent)
                }
            }
        }
    }

    fun calcAverageStart(average: Double) {
        binding?.ratingBarRowDetail?.rating = average.toFloat()
    }

    fun userDetailSuccess(user: User) {
        mUser = user
        mIntent = Intent(this@ProductDetailActivity,ChatLogActivity::class.java)
        mIntent.putExtra(Constants.USER_INFO, mUser)
    }
}