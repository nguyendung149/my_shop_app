package com.example.myappshop.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappshop.R
import com.example.myappshop.adapter.ProductListAdapter
import com.example.myappshop.databinding.FragmentProductsBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Product
import com.example.myappshop.ui.activities.products.AddProductActivity
import com.example.myappshop.ui.activities.products.ShowNotificationActivity
import com.example.myappshop.ui.activities.users.SettingsActivity

class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root





        return root
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))

                return true
            }
            R.id.action_notification -> {
                startActivity(Intent(activity,ShowNotificationActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteProduct(productID: String) {
        showAlertDialogToDeleteProduct(productID)
    }

    fun successDeleteProduct() {
        hideProgressDialog()


        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_LONG
        ).show()
        getProductListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
            .setMessage(resources.getString(R.string.delete_dialog_message))
            .setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) {dialogInterface,_ ->
            showProgessDialog(resources.getString(R.string.please_wait))

            FirestoreClass().deleteProduct(this,productID)

            dialogInterface.dismiss()

        }
            .setNegativeButton(resources.getString(R.string.no)) {dialogInterface,_ ->
                dialogInterface.dismiss()

            }
        val alertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    fun successProductListFromFireStore(productlist: ArrayList<Product>) {
        hideProgressDialog()


        if (productlist.size > 0) {
            binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

            binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyProductItems.setHasFixedSize(true)
            val adapterProducts =
                ProductListAdapter(requireActivity(), this@ProductsFragment, productlist)

            binding.rvMyProductItems.adapter = adapterProducts
        } else {
            binding.rvMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }

    }

    private fun getProductListFromFireStore() {
        showProgessDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductList(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}