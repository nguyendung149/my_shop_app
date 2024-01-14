package com.example.myappshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.models.Notification
import com.example.myappshop.models.Product
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.Calendar
import java.util.Locale

class NotificationListAdapter constructor(
    private var context:Context,
    private var notificationList: ArrayList<Notification>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: NotificationListAdapter.OnClickListener? =null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.notification_item_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = notificationList[position]
        FirebaseFirestore.getInstance().collection(Constants.PRODUCT).document(model.product_id).get()
            .addOnSuccessListener {document ->
                var productTitle:String = ""
                var product = document.toObject(Product::class.java)!!
                productTitle = product.title
                if (holder is NotificationListViewHolder){
                    GliderLoader(context).loadProductPicture(model.user_image,holder.itemView.findViewById(R.id.cv_image_user))
                    holder.itemView.findViewById<TextView>(R.id.tv_user_name_notification).text = model.user_name
                    holder.itemView.findViewById<TextView>(R.id.tv_order_datetime).text = getTime(model.time)
                    holder.itemView.findViewById<TextView>(R.id.tv_notification_content).text = "Người dùng ${model.user_name} đã" +
                            " mua sản phầm $productTitle từ bạn."
                    holder.itemView.setOnClickListener {
                        if (onClickListener != null) {
                            onClickListener!!.onClick(position, model)
                        }
                    }
                }
            }


    }
    fun setOnClickListener(onClickListener: NotificationListAdapter.OnClickListener){
        this.onClickListener = onClickListener
    }
    private fun getTime(dateTime:Long): String {
        val dateFormat = "dd MMM yyyy HH:mm:ss"
        val formatter = java.text.SimpleDateFormat(dateFormat, Locale.ENGLISH)
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = dateTime

        return formatter.format(calendar.timeInMillis)
    }

    class NotificationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    interface OnClickListener {
        fun onClick(position: Int,notificationItem: Notification)
    }
}