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
import com.example.myappshop.R
import com.example.myappshop.models.Product
import com.example.myappshop.ui.activities.products.ProductDetailActivity
import com.example.myappshop.ui.fragments.ProductsFragment
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader

open class ProductListAdapter constructor(
    private var context: Context,
    private val fragment:ProductsFragment,
    private var productlist: ArrayList<Product>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product_list_layout,
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
        if (holder is ProductViewHolder){
            GliderLoader(context).loadProductPicture(model.image,holder.itemView.findViewById(R.id.iv_item_image))
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener {
                fragment.deleteProduct(model.id)
            }
            holder.itemView.setOnClickListener {
                val intent : Intent = Intent(context,ProductDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID,model.id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,model.user_id)
                context.startActivity(intent)
            }
            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text ="${model.price} VND"
        }
    }
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
}