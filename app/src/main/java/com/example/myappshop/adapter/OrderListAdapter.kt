package com.example.myappshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myappshop.R
import com.example.myappshop.models.Order
import com.example.myappshop.ui.activities.products.OrderDetailActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import org.w3c.dom.Text
import java.text.NumberFormat
import java.util.Locale

class OrderListAdapter(

    private val context: Context,
    private var orderList: ArrayList<Order>
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return OrderListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product_list_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return  orderList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = orderList[position]


        if(holder is OrderListViewHolder){

            GliderLoader(context).loadProductPicture(model.image,holder.itemView.findViewById(R.id.iv_item_image))
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text = "${transformNumber(model.total_amount.toDouble())} VND"
            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context,OrderDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_ORDER_DETAILS,model)
                context.startActivity(intent)
            }
        }
    }
    private fun transformNumber(number: Number):String {
        return NumberFormat.getInstance(Locale.US).format(number)

    }
    private class OrderListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}