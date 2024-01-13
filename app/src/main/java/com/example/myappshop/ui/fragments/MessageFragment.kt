package com.example.myappshop.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatmessenger.adapter.RecentChatAdapter
import com.example.chatmessenger.adapter.onChatClicked
import com.example.myappshop.R
import com.example.myappshop.databinding.FragmentMessageBinding
import com.example.myappshop.ui.activities.MainActivity
import com.example.myappshop.ui.activities.message.ChatFromMessageHome
import com.example.myappshop.ui.activities.message.ChatListRepo
import com.example.myappshop.ui.activities.message.ChatLogActivity
import com.example.myappshop.ui.activities.message.NewMessageActivity
import com.example.myappshop.ui.activities.message.RecentChats
import com.example.myappshop.utils.Constants


class MessageFragment : BaseFragment(),onChatClicked {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val recentMessageRepo = ChatListRepo()
    private var recentChatAdapter:RecentChatAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recentChatAdapter = RecentChatAdapter()
        getRecentChat().observe(viewLifecycleOwner, Observer {
            hideProgressDialog()
            if(it.isNotEmpty()){
                binding.ryRecentChat.visibility = View.VISIBLE
                binding.tvNoMessageItemsFound.visibility = View.GONE
            }else {
                binding.tvNoMessageItemsFound.visibility = View.VISIBLE
                binding.ryRecentChat.visibility = View.GONE
            }
            binding.ryRecentChat.layoutManager = LinearLayoutManager(activity)
            recentChatAdapter!!.setList(it)
            binding.ryRecentChat.adapter = recentChatAdapter
        })

        recentChatAdapter!!.setOnChatClickListener(this)

        return root
    }

    override fun onResume() {
        super.onResume()
        hideProgressDialog()
        getRecentChat().observe(viewLifecycleOwner, Observer {
            hideProgressDialog()
            if(it.isNotEmpty()){
                binding.ryRecentChat.visibility = View.VISIBLE
                binding.tvNoMessageItemsFound.visibility = View.GONE
            }else {
                binding.tvNoMessageItemsFound.visibility = View.VISIBLE
                binding.ryRecentChat.visibility = View.GONE
            }
            binding.ryRecentChat.layoutManager = LinearLayoutManager(activity)
            recentChatAdapter!!.setList(it)
            binding.ryRecentChat.adapter = recentChatAdapter
        })

        recentChatAdapter!!.setOnChatClickListener(this)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.message_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_new_message -> {
                startActivity(Intent(activity, NewMessageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getRecentChat():LiveData<List<RecentChats>>{
        return recentMessageRepo.getAllChatList(this@MessageFragment)
    }

    override fun getOnChatCLickedItem(position: Int, chatList: RecentChats) {
        var intent = Intent(activity,ChatFromMessageHome::class.java)
        intent.putExtra(Constants.RECENT_CHAT,chatList)
        startActivity(intent)
    }
}