package com.example.myappshop.ui.activities.addresses

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.adapter.AddressListAdapter
import com.example.myappshop.databinding.ActivityAdressListBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Address
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.SwipeToDeleteCallback
import com.example.myappshop.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {

    private var mSelectedAddress:Boolean = false
    private lateinit var binding: ActivityAdressListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdressListBinding.inflate(layoutInflater)
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
        binding.tvAddAddress.setOnClickListener{
            val intent = Intent(this@AddressListActivity, EditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }
        getAddressList()
        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS,false)
        }
        if(mSelectedAddress){
            binding?.tvTitle?.text = resources.getString(R.string.title_select_address)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }
    fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressFronFireStore(this@AddressListActivity)
    }
    fun getAddressListFromFireStoreSuccess(addresslist: ArrayList<Address>){
        hideProgressDialog()
        if(addresslist.size > 0){
            binding.tvNoAddressFound.visibility = View.GONE
            binding.rvAddressList.visibility = View.VISIBLE

            binding.rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding.rvAddressList.setHasFixedSize(true)

            val addressListAdapter: AddressListAdapter = AddressListAdapter(this@AddressListActivity,addresslist,mSelectedAddress)
            binding.rvAddressList.adapter = addressListAdapter


            if(!mSelectedAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this@AddressListActivity){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = binding.rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(this@AddressListActivity,viewHolder.adapterPosition)
                    }

                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)


                val deleteSwipeHandler = object :SwipeToDeleteCallback(this@AddressListActivity){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().deleteAddressOnFireStore(this@AddressListActivity,addresslist[viewHolder.adapterPosition].id)
                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
            }


        }else {
            binding.tvNoAddressFound.visibility = View.VISIBLE
            binding.rvAddressList.visibility = View.GONE
        }
    }
    fun deleteAddressOnFireStoreSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_LONG
        ).show()

        getAddressList()
    }
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarAddressListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}