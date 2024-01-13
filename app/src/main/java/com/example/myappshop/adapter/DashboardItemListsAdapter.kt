package com.example.myappshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.models.Product
import com.example.myappshop.ui.activities.products.ProductDetailActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import java.security.PrivateKey

class DashboardItemListsAdapter constructor(
    private val context: Context,
    private var productlist: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? =null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DashboardProductViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product_dashboard_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productlist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = productlist[position]
        if (holder is DashboardProductViewHolder) {
            GliderLoader(context).loadProductPicture(model.image,holder.itemView.findViewById(R.id.iv_dashboard_item_image))
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_price).text = "${model.price} VND"

            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position,model)
                }
//                val intent : Intent = Intent(context,ProductDetailActivity::class.java)
//                intent.putExtra(Constants.EXTRA_PRODUCT_ID,model.id)
//                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,model.user_id)
//                context.startActivity(intent)
            }
        }
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class DashboardProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    interface OnClickListener {
        fun onClick(position: Int,product:Product)
    }
}