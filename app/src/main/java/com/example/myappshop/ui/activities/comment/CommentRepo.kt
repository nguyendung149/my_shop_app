package com.example.myappshop.ui.activities.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Comment
import com.example.myappshop.models.Messages
import com.example.myappshop.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommentRepo {
    private val firestore = FirebaseFirestore.getInstance()
    val comments = MutableLiveData<List<Comment>>()

    fun getComment(productID: String): LiveData<List<Comment>> {
        firestore.collection("Comments $productID").orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val commentList = mutableListOf<Comment>()
                if (!value!!.isEmpty) {
                    value.documents.forEach { documentSnapshot ->
                        val commentModel = documentSnapshot.toObject(Comment::class.java)
                        commentModel.let {
                            commentList.add(it!!)
                        }
                    }
                }
                comments.value = commentList
            }

        return comments
    }
}