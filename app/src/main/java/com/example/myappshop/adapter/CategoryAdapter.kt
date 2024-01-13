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




class CategoryAdapter(private val categoryItemList: List<CategoryItem>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catImageView: ImageView = itemView.findViewById(R.id.cat_img)
        val catNameTextView: TextView = itemView.findViewById(R.id.cat_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryItem = categoryItemList[position]

        // Set data to views in item_category layout
        holder.catImageView.setImageResource(categoryItem.imageResource)
        holder.catNameTextView.text = categoryItem.categoryName

    }

    override fun getItemCount(): Int {
        return categoryItemList.size
    }
}
