package com.example.myappshop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.adapter.OrderListAdapter
import com.example.myappshop.databinding.FragmentOrdersBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Order


class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
    private fun getMyOrderList(){
        showProgessDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyOrderList(this@OrdersFragment)
    }
    fun populateOrdersListIInUI(orderList:ArrayList<Order>){
        hideProgressDialog()

        if (orderList.size > 0) {
            binding.rvMyOrderItems.visibility = View.VISIBLE
            binding.tvNoOrdersFound.visibility = View.GONE

            binding.rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyOrderItems.setHasFixedSize(true)

            val orderListAdapter = OrderListAdapter(requireActivity(),orderList)
            binding.rvMyOrderItems.adapter = orderListAdapter
        }else {
            binding.rvMyOrderItems.visibility = View.GONE
            binding.tvNoOrdersFound.visibility = View.VISIBLE
        }

    }


    override fun onResume() {
        super.onResume()
        getMyOrderList()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}