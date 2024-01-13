package com.example.myappshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myappshop.R
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.CartProductItem
import com.example.myappshop.ui.activities.products.CartProductListActivity
import com.example.myappshop.utils.Constants
import com.example.myappshop.utils.GliderLoader
import com.google.firebase.firestore.FirebaseFirestore

class CartProductItemAdapter constructor(
    private val context: Context,
    private var cartList: ArrayList<CartProductItem>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return CartProductItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.cart_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = cartList[position]
        if (holder is CartProductItemViewHolder) {
            GliderLoader(context).loadProductPicture(
                model.image,
                holder.itemView.findViewById(R.id.iv_cart_item_image)
            )
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_price).text =
                "${model.price} VND"
            holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text = model.cart_quantity
            if (model.cart_quantity == "0") {
                holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                    View.GONE
                holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                    View.GONE
                if(updateCartItems){
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.VISIBLE
                }else {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.GONE
                }
                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).typeface =
                    Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
            } else {
                if(updateCartItems){
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility =
                        View.VISIBLE

                }else {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility =
                        View.GONE

                }


            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).setOnClickListener {
                when (context) {
                    is CartProductListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }
                FirestoreClass().removeItemFromCart(context, model.id)
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).setOnClickListener {
                var cartQuantity = model.cart_quantity.toInt()
                if (cartQuantity < model.stock_quantity.toInt()) {
                    var itemHashMap: HashMap<String, Any> = HashMap()
                    itemHashMap[Constants.CART_ITEM_QUANTITY] = (cartQuantity + 1).toString()
                    if(context is CartProductListActivity){
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    FirestoreClass().updateCart(context,model.id,itemHashMap)
                }else {
                    if(context is CartProductListActivity){
                        context.showErrorSnackBar(
                            context.resources.getString(R.string.msg_for_available_stock,model.stock_quantity),
                            true
                        )
                    }
                }
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).setOnClickListener {
                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else {
                    val cartQuantity = model.cart_quantity.toInt()
                    val itemHashMap: HashMap<String, Any> = HashMap()
                    itemHashMap[Constants.CART_ITEM_QUANTITY] = (cartQuantity - 1).toString()

                    if (context is CartProductListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    FirestoreClass().updateCart(context, model.id, itemHashMap)

                }
            }


        }
    }

    class CartProductItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}