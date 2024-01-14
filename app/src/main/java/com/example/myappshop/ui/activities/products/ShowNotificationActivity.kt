package com.example.myappshop.ui.activities.products

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.adapter.DashboardItemListsAdapter
import com.example.myappshop.adapter.NotificationListAdapter
import com.example.myappshop.databinding.ActivityShowNotificationBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Notification
import com.example.myappshop.models.Product
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class ShowNotificationActivity : BaseActivity() {
    private lateinit var binding: ActivityShowNotificationBinding
    private lateinit var mOwnerID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()

    }

    override fun onResume() {
        super.onResume()
        getNotificationList()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.notificationViewToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.notificationViewToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getNotificationList() {
        mOwnerID = FirestoreClass().getCurrentUserId()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getNotificationItemList(this@ShowNotificationActivity, mOwnerID)
    }

    fun successGetNotificationList(notificationList: ArrayList<Notification>) {
        hideProgressDialog()
        if (notificationList.size > 0) {
            binding.rvMyNotificationItems.visibility = View.VISIBLE
            binding.tvNoNotificationFound.visibility = View.GONE
            binding.rvMyNotificationItems.layoutManager =
                LinearLayoutManager(this@ShowNotificationActivity)
            binding.rvMyNotificationItems.setHasFixedSize(true)

            val adapter = NotificationListAdapter(this@ShowNotificationActivity, notificationList)
            binding.rvMyNotificationItems.adapter = adapter
            adapter.setOnClickListener(object : NotificationListAdapter.OnClickListener {
                override fun onClick(position: Int, notificationItem: Notification) {
                    val intent =
                        Intent(this@ShowNotificationActivity, ProductDetailActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, notificationList[position].product_id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, notificationList[position].owner_id)
                    startActivity(intent)
                }


            }
            )
        } else {
            binding.rvMyNotificationItems.visibility = View.GONE
            binding.tvNoNotificationFound.visibility = View.VISIBLE
        }
    }
}
