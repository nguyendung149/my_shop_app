package com.example.myappshop.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.models.Address
import com.example.myappshop.ui.activities.addresses.EditAddressActivity
import com.example.myappshop.ui.activities.products.CheckOutActivity
import com.example.myappshop.utils.Constants

class AddressListAdapter (
    private val context:Context,
    private var addresslist:ArrayList<Address>,
    private var selectedAddress:Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.address_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return addresslist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model: Address = addresslist[position]

        if(holder is AddressListViewHolder){
            holder.itemView.findViewById<TextView>(R.id.tv_address_full_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_address_details).text = "${model.address}, ${model.zipCode}"
            holder.itemView.findViewById<TextView>(R.id.tv_address_mobile_number).text = model.mobileNumber
            holder.itemView.findViewById<TextView>(R.id.tv_address_type).text = model.type

            if(selectedAddress) {
                holder.itemView.setOnClickListener {
                     val intent = Intent(context,CheckOutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS,model)
                    context.startActivity(intent)
                }
            }
        }
    }
    fun notifyEditItem(activity:Activity,position: Int){
        val intent = Intent(context,EditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAIL,addresslist[position])
        activity.startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }
    private class AddressListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}