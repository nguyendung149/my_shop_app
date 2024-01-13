package com.example.myappshop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.models.CategoryItem
import com.example.myappshop.models.Product


class CategoryAdapter(
    private val categoryItemList: List<CategoryItem>,
    private val context: Context) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? =null
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catImageView: ImageView = itemView.findViewById(R.id.cat_img)
        val catNameTextView: TextView = itemView.findViewById(R.id.cat_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryItem = categoryItemList[position]

        // Set data to views in item_category layout
        holder.catImageView.setImageResource(categoryItem.imageResource)
        holder.catNameTextView.text = categoryItem.categoryName
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, categoryItem)
            }
        }

    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return categoryItemList.size
    }
    interface OnClickListener {
        fun onClick(position: Int,category: CategoryItem)
    }
}
