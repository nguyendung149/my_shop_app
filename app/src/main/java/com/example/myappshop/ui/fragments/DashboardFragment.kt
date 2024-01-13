package com.example.myappshop.ui.fragments

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.adapter.CategoryAdapter
import com.example.myappshop.adapter.DashboardItemListsAdapter
import com.example.myappshop.databinding.FragmentDashboardBinding
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.CategoryItem
import com.example.myappshop.models.Product
import com.example.myappshop.ui.activities.products.CartProductListActivity
import com.example.myappshop.ui.activities.products.ProductDetailActivity
import com.example.myappshop.ui.activities.products.SearchProductActivity
import com.example.myappshop.ui.activities.users.SettingsActivity
import com.example.myappshop.utils.Constants

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null

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

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Khởi tạo danh sách mẫu Category
        val categoryItemList = createCategoryItemList()

        // Khởi tạo RecyclerView cho danh sách Category
        val categoryRecyclerView: RecyclerView = root.findViewById(R.id.rec_category)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.setHasFixedSize(true)

        // Khởi tạo Adapter và thiết lập cho RecyclerView
        val categoryAdapter = CategoryAdapter(categoryItemList, requireActivity())
        categoryRecyclerView.adapter = categoryAdapter
        categoryAdapter.setOnClickListener(object : CategoryAdapter.OnClickListener {
            override fun onClick(position: Int, category: CategoryItem) {
                FirestoreClass().getProductCatelogList(
                    this@DashboardFragment,
                    category.categoryName
                )
            }
        })
        binding.tvLblAllTheProduct.setOnClickListener {
            getDashboardItemsList()
        }
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }

            R.id.action_cart -> {
                startActivity(Intent(activity, CartProductListActivity::class.java))
                return true
            }

            R.id.action_search -> {
                startActivity(Intent(activity, SearchProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    private fun getDashboardItemsList() {
        showProgessDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemList(this@DashboardFragment)
    }

    fun successDashboardItemLists(dashboardItemList: ArrayList<Product>) {
        hideProgressDialog()
        if (dashboardItemList.size > 0) {
            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE

            binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvDashboardItems.setHasFixedSize(true)

            val adapter = DashboardItemListsAdapter(requireActivity(), dashboardItemList)
            binding.rvDashboardItems.adapter = adapter

            adapter.setOnClickListener(object : DashboardItemListsAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                    startActivity(intent)
                }

            })
        } else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createCategoryItemList(): List<CategoryItem> {
        // Tạo danh sách mẫu Category ở đây, hoặc lấy từ nguồn dữ liệu
        val list = mutableListOf<CategoryItem>()
        list.add(CategoryItem(R.drawable.computermouse, "Computer Mouse"))
        list.add(CategoryItem(R.drawable.screen, "Screen"))
        list.add(CategoryItem(R.drawable.keyboard, "Key Board"))
        list.add(CategoryItem(R.drawable.headphone, "HeadPhone"))
        list.add(CategoryItem(R.drawable.ram, "Ram"))
        list.add(CategoryItem(R.drawable.mainboard, "Main Board"))
        list.add(CategoryItem(R.drawable.pc, "PC"))
        list.add(CategoryItem(R.drawable.laptop, "Laptop"))
        list.add(CategoryItem(R.drawable.ssd, "SSD"))
        list.add(CategoryItem(R.drawable.graphiccard, "Graphic Card"))
        list.add(CategoryItem(R.drawable.cpu, "CPU"))

        // Thêm các mục khác nếu cần

        return list
    }

    fun successProductCatelogList(productCatelogList: ArrayList<Product>) {
        hideProgressDialog()
        binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
        binding.rvDashboardItems.setHasFixedSize(true)

        val adapter = DashboardItemListsAdapter(requireActivity(), productCatelogList)
        binding.rvDashboardItems.adapter = adapter
        adapter.setOnClickListener(object : DashboardItemListsAdapter.OnClickListener {
            override fun onClick(position: Int, product: Product) {
                val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                startActivity(intent)
            }

        })
    }
}