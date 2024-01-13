package com.example.myappshop.ui.activities.products

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.adapter.CartProductItemAdapter
import com.example.myappshop.databinding.ActivityCartProductListBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.CartProductItem
import com.example.myappshop.models.Product
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.ui.activities.addresses.AddressListActivity
import com.example.myappshop.utils.Constants
import java.text.NumberFormat
import java.util.Locale

class CartProductListActivity : BaseActivity() {
    private var shippingFee: Double = 0.0
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartList :ArrayList<CartProductItem>
    private var binding:ActivityCartProductListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartProductListBinding.inflate(layoutInflater)
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
        FirestoreClass().getUserDetails(this@CartProductListActivity)

        binding?.btnCheckout?.setOnClickListener {
            val intent = Intent(this@CartProductListActivity,AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        //getCartProductList()
        getAllProductList()
    }
    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarCartListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarCartListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun getCartProductList(){
//        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartProductList(this@CartProductListActivity)
    }
    @SuppressLint("SetTextI18n")
    fun getCartProductListSuccess(cartProductList :ArrayList<CartProductItem>){
        hideProgressDialog()

        for( product in mProductList){
            for(cartItem in cartProductList)
                if(product.id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity

                    if(product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
        }
        mCartList = cartProductList

        if (mCartList.size > 0) {
            binding?.tvNoCartItemFound?.visibility = View.GONE
            binding?.rvCartItemsList?.visibility = View.VISIBLE
            binding?.btnCheckout?.visibility = View.VISIBLE


            binding?.rvCartItemsList?.layoutManager = LinearLayoutManager(this@CartProductListActivity)
            binding?.rvCartItemsList?.setHasFixedSize(true)
            var cartProductItemAdapter : CartProductItemAdapter = CartProductItemAdapter(this@CartProductListActivity,mCartList,true)


            binding?.rvCartItemsList?.adapter=cartProductItemAdapter

            var subTotal: Double = 0.0

            for(cartItem in mCartList){
                val availableQuantity = cartItem.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    var price = cartItem.price.toDouble()
                    val quantity = cartItem.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }
            binding?.tvSubTotal?.text = "${transformNumber(subTotal)} VND "
            binding?.tvShippingCharge?.text ="${transformNumber(shippingFee)} VND"


            if(subTotal > 0){
                binding?.llCheckout?.visibility = View.VISIBLE

                var total = subTotal + shippingFee

                binding?.tvTotalAmount?.text = "${transformNumber(total)} VND"

            }else {
                binding?.llCheckout?.visibility = View.GONE
            }
        }else {
            binding?.tvNoCartItemFound?.visibility = View.VISIBLE
            binding?.rvCartItemsList?.visibility = View.GONE
            binding?.btnCheckout?.visibility = View.GONE
        }
    }
    fun getAllProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this@CartProductListActivity)
    }
    fun getAllProductListSuccess(productList: ArrayList<Product>){
       hideProgressDialog()
        mProductList = productList

        getCartProductList()
    }
    fun itemRemovedSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@CartProductListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_LONG
        ).show()

        getCartProductList()
    }
    fun upDateCartSuccess(){
        hideProgressDialog()
        getCartProductList()
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