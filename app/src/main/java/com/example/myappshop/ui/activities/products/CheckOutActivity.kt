package com.example.myappshop.ui.activities.products

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.adapter.CartProductItemAdapter
import com.example.myappshop.databinding.ActivityCheckOutBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Address
import com.example.myappshop.models.CartProductItem
import com.example.myappshop.models.Order
import com.example.myappshop.models.Product
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.ui.activities.DashboardActivity
import com.example.myappshop.utils.Constants
import java.text.NumberFormat
import java.util.Locale

class CheckOutActivity : BaseActivity() {
    private var mAddressSelectedDetail:Address? = null
    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var mProductList :ArrayList<Product>
    private lateinit var mCartList : ArrayList<CartProductItem>
    private var mTotalAmount:Double = 0.0
    private var mSubTotal: Double = 0.0
    private var shippingFee:Double = 0.0
    private lateinit var mOrderDetails: Order
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
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
        FirestoreClass().getUserDetails(this@CheckOutActivity)
        setupActionBar()
        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressSelectedDetail = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }
        if(mAddressSelectedDetail!=null){
            binding.tvCheckoutAddressType.text = mAddressSelectedDetail!!.type
            binding.tvCheckoutFullName.text = mAddressSelectedDetail!!.name
            binding.tvCheckoutAddress.text = "${mAddressSelectedDetail!!.address}, ${mAddressSelectedDetail!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressSelectedDetail!!.additionalNote
            binding.tvCheckoutMobileNumber.text = mAddressSelectedDetail!!.mobileNumber
            if(mAddressSelectedDetail!!.otherDetails!!.isNotEmpty()){
                binding.tvCheckoutOtherDetails.text = mAddressSelectedDetail!!.otherDetails
            }
        }
        getProductList()
        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }
    }
    fun successProductListFromFireStore(productList:ArrayList<Product>){
        mProductList = productList
        getCartItemList()
    }
    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductList(this@CheckOutActivity)
    }
    private fun getCartItemList(){
        FirestoreClass().getCartProductList(this@CheckOutActivity)
    }
    @SuppressLint("SetTextI18n")
    fun successCartItemList(cartList: ArrayList<CartProductItem>){
        hideProgressDialog()
        for(product in mProductList) {
            for (cart in cartList) {
                if (product.id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartList = cartList

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartProductItemAdapter(this,cartList,false)
        binding.rvCartListItems.adapter = cartListAdapter

        for(item in mCartList){
            val availableQuantity = item.stock_quantity.toInt()
            if(availableQuantity > 0){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += price*quantity
            }
        }

        binding.tvCheckoutSubTotal.text = "${transformNumber(mSubTotal)} VND"
        binding.tvCheckoutShippingCharge.text = "${transformNumber(shippingFee)} VND"

        if(mSubTotal>0){
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + shippingFee

            binding.tvCheckoutTotalAmount.text = "${transformNumber(mTotalAmount)} VND"

        }else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE

        }

    }
    private fun placeAnOrder(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mAddressSelectedDetail != null) {
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserId(),
                mCartList,
                mAddressSelectedDetail!!,
                "The order ${System.currentTimeMillis()}",
                mCartList[0].image,
                mSubTotal.toString(),
                shippingFee.toString(),
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )
            FirestoreClass().placeOrder(this@CheckOutActivity,mOrderDetails)
        }
    }
    fun orderPlaceSuccess(){
        FirestoreClass().updateAllDetails(this@CheckOutActivity,mCartList,mOrderDetails)
    }
    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this@CheckOutActivity,
            "Your order was placed successfully!",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this@CheckOutActivity,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCheckoutActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarCheckoutActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun ShippingFee(user: User){
        if(user.address.trim{ it <= ' '}.toLowerCase().equals("ho chi minh")){
            shippingFee = 30000.0
        }else {
            shippingFee = 100000.0
        }
    }
    private fun transformNumber(number: Number):String {
        return NumberFormat.getInstance(Locale.US).format(number)

    }
}