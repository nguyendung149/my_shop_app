package com.example.myappshop.ui.activities.products

import android.annotation.SuppressLint
import android.media.Rating
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.adapter.CommentAdapter
import com.example.myappshop.adapter.MessageAdapter
import com.example.myappshop.databinding.ActivityCommentBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Comment
import com.example.myappshop.models.Messages
import com.example.myappshop.models.Product
import com.example.myappshop.models.RatingProduct
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.BaseActivity
import com.example.myappshop.ui.activities.comment.CommentRepo
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso
import java.util.Calendar
import java.util.Locale

class CommentActivity : BaseActivity() {
    private lateinit var binding: ActivityCommentBinding
    private var user: User? = null
    private var commentRepo = CommentRepo()
    private var commentAdapter: CommentAdapter? = null
    private var productID: String? = null
    private var getRatingValue: Float = 0.0F
    private var userName: String = ""
    private var ratingScoreList = ArrayList<RatingProduct>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
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

        productID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        setUI()
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(FirestoreClass().getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                user = document.toObject(User::class.java)
                userName = "${user?.firstName} ${user?.lastName}"
            }
//        FirebaseFirestore.getInstance().collection("Comments $productID").orderBy("time",Query.Direction.DESCENDING).get()
//            .addOnSuccessListener { document ->
//                for (item in document.documents){
//                    val userComment =item.toObject(Comment::class.java)!!
//                    userCommentList.add(userComment)
//                }
//
//            }
        binding.rattingBar.isEnabled = true
        FirebaseFirestore.getInstance().collection("Rating $productID")
            .orderBy("time", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { document ->
                ratingScoreList.clear()
                for (item in document.documents) {
                    val ratingScore = item.toObject(RatingProduct::class.java)!!
                    ratingScoreList.add(ratingScore)
                    if (ratingScore.user_id == FirestoreClass().getCurrentUserId()) {
                        binding.rattingBar.isEnabled = false
                        binding.rattingBar.rating = ratingScore.ratingScore.toFloat()
                        getComment(productID!!).observe(
                            this@CommentActivity, androidx.lifecycle.Observer {
                                initRecycleView(it)
                            })
                    }
                }
            }
        binding.rattingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            getRatingValue = fl
            binding.rattingBar.isEnabled = false
            storeRating(getRatingValue)
            FirebaseFirestore.getInstance().collection("Rating $productID")
                .orderBy("time", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { document ->
                    ratingScoreList.clear()
                    for (item in document.documents) {
                        val ratingScore = item.toObject(RatingProduct::class.java)!!
                        ratingScoreList.add(ratingScore)
                        if (ratingScore.user_id == FirestoreClass().getCurrentUserId()) {
                            binding.rattingBar.isEnabled = false
                            binding.rattingBar.rating = ratingScore.ratingScore.toFloat()
                            getComment(productID!!).observe(
                                this@CommentActivity, androidx.lifecycle.Observer {
                                    initRecycleView(it)
                                })
                        }
                    }
                }.addOnFailureListener {

                }
        }




        binding.btnCommentSend.setOnClickListener {

            sendComment(
                FirestoreClass().getCurrentUserId(),
                productID!!,
                user!!.image
            )
            getComment(productID!!)
            binding.etCommentContent.setText("")
            Toast.makeText(this@CommentActivity, "Rating: $getRatingValue", Toast.LENGTH_LONG)
                .show()

        }
        getComment(productID!!).observe(
            this@CommentActivity, androidx.lifecycle.Observer {
                initRecycleView(it)
            })
    }

    override fun onResume() {
        super.onResume()
        getComment(productID!!).observe(
            this@CommentActivity, androidx.lifecycle.Observer {
                initRecycleView(it)
            })

    }

    private fun getTime(): String {
        val dateFormat = "dd MMM yyyy HH:mm:ss"
        val formatter = java.text.SimpleDateFormat(dateFormat, Locale.ENGLISH)
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = System.currentTimeMillis()

        return formatter.format(calendar.timeInMillis)
    }

    fun sendComment(sender: String, product_id: String, image: String) {

        val hashMap = hashMapOf<String, Any>(
            "sender" to sender,
            "image" to image,
            "message" to binding.etCommentContent.text.toString(),
            "time" to getTime(),
            "product_id" to product_id
        )
        FirebaseFirestore.getInstance().collection("Comments ${product_id}").document()
            .set(hashMap, SetOptions.merge())
            .addOnSuccessListener {

            }
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCommentLogActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarCommentLogActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycleView(commentList: List<Comment>) {
        commentAdapter = CommentAdapter(userName, ratingScoreList)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerviewComment.layoutManager = layoutManager
        binding.recyclerviewComment.setHasFixedSize(true)
        layoutManager.stackFromEnd = false
        commentAdapter!!.setList(commentList)
        commentAdapter!!.notifyDataSetChanged()
        binding.recyclerviewComment.adapter = commentAdapter
    }

    fun getComment(productID: String): LiveData<List<Comment>> {
        return commentRepo.getComment(productID)
    }

    fun setUI() {
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            FirebaseFirestore.getInstance().collection(Constants.PRODUCT).document(productID!!)
                .get()
                .addOnFailureListener {

                }
                .addOnSuccessListener { document ->
                    val product: Product = document.toObject(Product::class.java)!!
                    GliderLoader(this).loadProductPicture(
                        product.image,
                        binding.ivProductDetailCommentImage
                    )
                    binding.tvProductDetailsCommentTitle.text = product.title

                }

        }
    }

    fun storeRating(ratingNum: Float) {
        var ratingProduct = RatingProduct(
            FirestoreClass().getCurrentUserId(),
            "${user!!.firstName} ${user!!.lastName}",
            user!!.image,
            ratingNum.toString(),
            getTime()
        )
        FirestoreClass().storeRatingOnFireStore(ratingProduct, productID!!)
    }
}