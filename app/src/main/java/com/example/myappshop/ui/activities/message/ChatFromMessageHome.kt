package com.example.myappshop.ui.activities.message

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.adapter.MessageAdapter
import com.example.myappshop.databinding.ActivityChatFromMessageHomeBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Messages
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatFromMessageHome  : BaseActivity() {
    var imageOfList:ArrayList<String> = ArrayList<String>()
    private var messageApater:MessageAdapter? = null
    private var binding: ActivityChatFromMessageHomeBinding? = null
    private val messageRepo = MessageRepo()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatFromMessageHomeBinding.inflate(layoutInflater)
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
        val chatList = intent.getParcelableExtra<RecentChats>(Constants.RECENT_CHAT)!!
        Picasso.get().load(chatList.friendsimage)
            .into(binding!!.ivUserImageHome)
        binding?.tvTitleChatLogHome?.text = "${chatList.name}"
        binding?.btnSendHome?.setOnClickListener {
            sendMessage(
                FirestoreClass().getCurrentUserId(),
                chatList.friendid!!,
                "${chatList.name}",
                chatList.friendsimage!!
            )

        }
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                snapshot?.documents?.forEach { document ->
                    val user = document.toObject(com.example.myappshop.models.User::class.java)
                    if (user!!.id == FirestoreClass().getCurrentUserId()) {
                        user.let {
                            imageOfList.add(user.image)
                        }
                    }
                }

            }
        getMessages(chatList.friendid!!).observe(this, androidx.lifecycle.Observer {

            imageOfList.add(chatList.friendsimage!!)
            initRecycleView(it)
        })

    }
    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarChatLogActivityHome)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@ChatFromMessageHome,
                    R.drawable.app_gradient_color_background
                )
            )
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarChatLogActivityHome?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun getTime(): String {
        val dateFormat = "dd MMM yyyy HH:mm:ss"
        val formatter = java.text.SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = System.currentTimeMillis()

        return formatter.format(System.currentTimeMillis())
    }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) {

        val hashMap = hashMapOf<String, Any>(
            "sender" to sender,
            "receiver" to receiver,
            "message" to binding?.etMessageContentHome?.text.toString(),
            "time" to getTime()
        )
        val uniqueId = listOf(sender, receiver).sorted()
        uniqueId.joinToString(separator = "")
        val friendnamesplit = friendname.split("\\s".toRegex())[0]
        FirebaseFirestore.getInstance().collection("Messages").document(uniqueId.toString())
            .collection("chats")
            .document(getTime()).set(hashMap).addOnCompleteListener { taskmessage ->
                val chatList = intent.getParcelableExtra<RecentChats>(Constants.RECENT_CHAT)!!
                val setHashap = hashMapOf<String, Any>(
                    "friendid" to receiver,
                    "time" to getTime(),
                    "sender" to sender,
                    "message" to binding?.etMessageContentHome?.text.toString(),
                    "friendsimage" to friendimage,
                    "name" to friendname,
                    "person" to "you"
                )
                FirebaseFirestore.getInstance().collection("Conversation${sender}")
                    .document(receiver)
                    .set(setHashap)
                FirebaseFirestore.getInstance().collection("Conversation${receiver}")
                    .document(sender)
                    .update(
                        "message",
                        binding?.etMessageContentHome?.text.toString(),
                        "time",
                        getTime(),
                        "person",
                        "${chatList.name}"
                    )
                if (taskmessage.isSuccessful) {

                }

            }
        
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycleView(messageList:List<Messages>){
        messageApater = MessageAdapter(listOfImage = imageOfList)
        val layoutManager = LinearLayoutManager(this)
        binding?.recyclerviewMessageHome!!.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageApater!!.setList(messageList)
        messageApater!!.notifyDataSetChanged()
        binding?.recyclerviewMessageHome?.adapter = messageApater
    }
    private fun getMessages(friendid:String):LiveData<List<Messages>>{
        return messageRepo.getMessages(friendid)
    }
}

