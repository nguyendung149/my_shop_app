package com.example.myappshop.ui.activities.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myappshop.R
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.ui.fragments.MessageFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ChatListRepo() {
    val firestore = FirebaseFirestore.getInstance()
    fun getAllChatList(fragment:MessageFragment): LiveData<List<RecentChats>> {
        val mainChatList = MutableLiveData<List<RecentChats>>()
        fragment.showProgessDialog(fragment.resources.getString(R.string.please_wait))
        firestore.collection("Conversation${FirestoreClass().getCurrentUserId()}").orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                val chatlist = mutableListOf<RecentChats>()
                snapshot?.forEach { document ->
                    val chatlistmodel = document.toObject(RecentChats::class.java)
                    if (chatlistmodel!!.sender.equals(FirestoreClass().getCurrentUserId())) {
                            chatlistmodel.let {
                                chatlist.add(it)
                            }

                        }
                }
                mainChatList.value = chatlist
            }
        return mainChatList

    }
}