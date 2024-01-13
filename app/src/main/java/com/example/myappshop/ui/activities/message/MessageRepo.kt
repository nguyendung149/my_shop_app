package com.example.myappshop.ui.activities.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Messages
import com.example.myappshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class MessageRepo {
    private val firestore = FirebaseFirestore.getInstance()
    val messages = MutableLiveData<List<Messages>>()

        fun getMessages(friendid:String):LiveData<List<Messages>>{
        val  uniqueid = listOf(FirebaseAuth.getInstance().currentUser!!.uid,friendid).sorted()
        uniqueid.joinToString(separator = "")
        firestore.collection(Constants.MESSAGE).document(uniqueid.toString()).collection("chats").orderBy("time",Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if(error!=null){
                return@addSnapshotListener
            }
            val messageList = mutableListOf<Messages>()
            if(!value!!.isEmpty){
                value.documents.forEach {documentSnapshot ->  
                    val messageModel = documentSnapshot.toObject(Messages::class.java)
                    if (messageModel!!.sender.equals(FirestoreClass().getCurrentUserId()) &&messageModel.receiver.equals(friendid) ||
                        messageModel!!.sender.equals(friendid)&&messageModel.receiver.equals(FirestoreClass().getCurrentUserId())){
                        messageModel.let {
                            messageList.add(it!!)
                        }
                    }
                }
                messages.value = messageList
            }


        }
    return messages
    }
}