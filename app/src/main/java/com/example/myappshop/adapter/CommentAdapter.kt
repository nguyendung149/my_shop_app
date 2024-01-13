package com.example.myappshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Comment
import com.example.myappshop.models.Messages
import com.example.myappshop.models.RatingProduct
import com.squareup.picasso.Picasso

class CommentAdapter (
    private val userName:String,
    private val ratingNumList:ArrayList<RatingProduct>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listComment = listOf<Comment>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.user_comment_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comment = listComment[position]
        if (holder is CommentViewHolder) {
            holder.timeOfSent.visibility = View.VISIBLE
            holder.userImage.visibility = View.VISIBLE

            Picasso.get().load(comment.image)
                .into(holder.userImage)
            holder.messageText.text = comment.message
            holder.timeOfSent.text = comment.time ?: ""
            for (item in ratingNumList){
                if (item.image == comment.image){
                    holder.rating.rating = item.ratingScore.toFloat()
                    holder. userName.text = item.username
                }
            }

        }
    }

    override fun getItemCount() = listComment.size
    fun setList(newList: List<Comment>) {
        this.listComment = newList
    }
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView.rootView) {
        val userName: TextView = itemView.findViewById(R.id.user_name_comment)
        val userImage: ImageView = itemView.findViewById(R.id.iv_user_icon_comment)
        val messageText: TextView = itemView.findViewById(R.id.tv_content_comment)
        val timeOfSent: TextView = itemView.findViewById(R.id.tv_time_comment)
        val rating: RatingBar = itemView.findViewById(R.id.rating_bar_row)

    }
}