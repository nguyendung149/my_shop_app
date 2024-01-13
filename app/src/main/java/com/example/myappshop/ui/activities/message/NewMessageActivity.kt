package com.example.myappshop.ui.activities.message

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.databinding.ActivityNewMessageBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : BaseActivity() {
    private var binding: ActivityNewMessageBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
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
        fetchUser()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun fetchUser() {
        var adapter = GroupAdapter<ViewHolder>()
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                snapshot?.documents?.forEach { document ->
                    val user = document.toObject(User::class.java)!!
                    if (user.id != FirestoreClass().getCurrentUserId()) {
                        user.let {
                            adapter.add(UserItem(user))
                        }
                    }
                }

            }
        adapter.setOnItemClickListener{item,view->
            val userItem = item as UserItem
            var intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra(Constants.USER_INFO,userItem.user)
            startActivity(intent)
            finish()
        }
        binding?.recyclerviewNewMessage?.layoutManager =
            LinearLayoutManager(this@NewMessageActivity)
        binding?.recyclerviewNewMessage?.adapter = adapter

    }

    class UserItem(val user: User) : Item<ViewHolder>() {
        @SuppressLint("SetTextI18n")
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.user_name).text =
                "${user.firstName} ${user.lastName}"
            if (user.image != "") {
                Picasso.get().load(user.image)
                    .into(viewHolder.itemView.findViewById<ImageView>(R.id.iv_user_icon))
            }
        }
        override fun getLayout(): Int {
            return R.layout.user_item_row
        }

    }
    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarNewMessageActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@NewMessageActivity,
                    R.drawable.app_gradient_color_background
                )
            )
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarNewMessageActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}