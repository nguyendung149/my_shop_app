package com.example.myappshop.ui.activities.message

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myappshop.R
import com.example.myappshop.adapter.MessageAdapter
import com.example.myappshop.databinding.ActivityChatLogBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Messages
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Observer

open class ChatLogActivity : BaseActivity() {
    var imageOfList: ArrayList<String> = ArrayList<String>()
    private var messageApater: MessageAdapter? = null
    private var binding: ActivityChatLogBinding? = null
    private val messageRepo = MessageRepo()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatLogBinding.inflate(layoutInflater)
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
        if (intent.hasExtra(Constants.PRODUCT_TITLE)) {
            binding?.etMessageContenet?.setText(intent.getStringExtra(Constants.PRODUCT_TITLE))
        }
        val user = intent.getParcelableExtra<User>(Constants.USER_INFO)!!
        Picasso.get().load(user.image)
            .into(binding!!.ivUserImage)
        binding?.tvTitleChatLog?.text = "${user.firstName} ${user.lastName}"
        binding?.btnSend?.setOnClickListener {
            sendMessage(
                FirestoreClass().getCurrentUserId(),
                user.id,
                "${user.firstName} ${user.lastName}",
                user.image
            )


        }
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                snapshot?.documents?.forEach { document ->
                    val user = document.toObject(User::class.java)
                    if (user!!.id == FirestoreClass().getCurrentUserId()) {
                        user.let {
                            imageOfList.add(user.image)
                        }
                    }
                }

            }



        getMessages(user.id).observe(this, androidx.lifecycle.Observer {

            imageOfList.add(user.image)
            initRecycleView(it)
        })

    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarChatLogActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@ChatLogActivity,
                    R.drawable.app_gradient_color_background
                )
            )
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarChatLogActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getTime(): String {
        val dateFormat = "dd MMM yyyy HH:mm:ss"
        val formatter = java.text.SimpleDateFormat(dateFormat, Locale.ENGLISH)
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = System.currentTimeMillis()

        return formatter.format(calendar.timeInMillis)
    }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) {

        val hashMap = hashMapOf<String, Any>(
            "sender" to sender,
            "receiver" to receiver,
            "message" to binding?.etMessageContenet?.text.toString(),
            "time" to getTime()
        )


        val uniqueId = listOf(sender, receiver).sorted()
        uniqueId.joinToString(separator = "")


        val friendnamesplit = friendname.split("\\s".toRegex())[0]

        FirebaseFirestore.getInstance().collection("Messages").document(uniqueId.toString())
            .collection("chats")
            .document(getTime()).set(hashMap).addOnCompleteListener { taskmessage ->
                val user = intent.getParcelableExtra<User>(Constants.USER_INFO)!!
                val setHashap = hashMapOf<String, Any>(
                    "friendid" to receiver,
                    "time" to getTime(),
                    "sender" to sender,
                    "message" to binding?.etMessageContenet?.text.toString(),
                    "friendsimage" to friendimage,
                    "name" to friendname,
                    "person" to "you"
                )
                val setHashMapReceiver = hashMapOf<String,Any>(
                    "message" to binding?.etMessageContenet?.text.toString(),
                    "time" to getTime(),
                    "person" to "${user.firstName} ${user.lastName}"
                )
                FirebaseFirestore.getInstance().collection("Conversation${sender}")
                    .document(receiver)
                    .set(setHashap)
                FirebaseFirestore.getInstance().collection("Conversation${receiver}")
                    .document(sender)
                    .update(
                        "message",
                        binding?.etMessageContenet?.text.toString(),
                        "time",
                        getTime(),
                        "person",
                        "${user.firstName} ${user.lastName}"
                    )
                if (taskmessage.isSuccessful) {

                }

            }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycleView(messageList: List<Messages>) {
        messageApater = MessageAdapter(listOfImage = imageOfList)
        val layoutManager = LinearLayoutManager(this)
        binding?.recyclerviewMessage!!.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageApater!!.setList(messageList)
        messageApater!!.notifyDataSetChanged()
        binding?.recyclerviewMessage?.adapter = messageApater


    }

    private fun getMessages(friendid: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friendid)
    }
}

