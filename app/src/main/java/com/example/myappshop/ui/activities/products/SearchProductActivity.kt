package com.example.myappshop.ui.activities.products

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.example.myappshop.R
import com.example.myappshop.adapter.DashboardItemListsAdapter
import com.example.myappshop.databinding.ActivitySearchProductBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Product
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants

class SearchProductActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchProductBinding
    private var mProductList = ArrayList<Product>()
    private var mTitleProductLists: ArrayList<String>? = null
    private var mAdapterProductTitleList: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchProductBinding.inflate(layoutInflater)
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
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()

    }

    private fun getProductListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductListSearch(this)
    }

    fun successProductListFromFireStoreSearchView(
        productlist: ArrayList<Product>,
        titleProductList: ArrayList<String>
    ) {
        hideProgressDialog()
        mProductList = productlist
        mTitleProductLists = titleProductList
        mAdapterProductTitleList = ArrayAdapter(
            this@SearchProductActivity,
            android.R.layout.simple_list_item_1,
            mTitleProductLists!!
        )

        binding.lvMyProductItemsSearch.adapter = mAdapterProductTitleList
        binding.lvMyProductItemsSearch.visibility = View.GONE
        binding.lvMyProductItemsSearch.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@SearchProductActivity, ProductDetailActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, mProductList[position].id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, mProductList[position].user_id)
            startActivity(intent)
        }
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.searchView.clearFocus()
                    if (mTitleProductLists!!.contains(query)) {
                        mAdapterProductTitleList!!.filter.filter(query)
                        if (query != null) {
                           mProductList = productlist.filter { item -> item.title.lowercase().contains(query.lowercase()) } as ArrayList<Product>
                        }
                        binding.lvMyProductItemsSearch.visibility = View.VISIBLE
                    } else {
                        binding.lvMyProductItemsSearch.visibility = View.GONE
                    }

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.length!! > 0) {
                        mAdapterProductTitleList!!.filter.filter(newText)
                        mProductList = productlist.filter { item -> item.title.lowercase().contains(newText.lowercase()) } as ArrayList<Product>

                        binding.lvMyProductItemsSearch.visibility = View.VISIBLE
                    } else {
                        binding.lvMyProductItemsSearch.visibility = View.GONE
                    }
                    return false
                }

            })


    }

}