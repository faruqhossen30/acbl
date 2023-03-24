package com.shopnolive.shopnolive.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.shopnolive.shopnolive.adapter.GroupChatListAdapter
import com.shopnolive.shopnolive.databinding.FragmentGroupChatListBinding
import com.shopnolive.shopnolive.model.chat.GroupConversation
import com.shopnolive.shopnolive.utils.Variable.myId
import com.shopnolive.shopnolive.utils.toast

class GroupChatListFragment : Fragment() {

    private var _binding: FragmentGroupChatListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mReference: DatabaseReference

    private lateinit var chatList: ArrayList<GroupConversation>

    private lateinit var conversationListAdapter: GroupChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupChatListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init
        chatList = ArrayList()

        mReference = FirebaseDatabase.getInstance().getReference("GroupConversations")
        conversationListAdapter = GroupChatListAdapter(requireContext())
        val linearLayoutManager = LinearLayoutManager(requireContext())

        binding.rvGroupList.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = conversationListAdapter
        }

        binding.fabCreateGroup.setOnClickListener {
            Intent(requireContext(), CreateChatGroupActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private val chatListEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            chatList.clear()
            for (snapshot in dataSnapshot.children) {
                if (snapshot.child("Members").child(myId).exists()) {
                    val conversation: GroupConversation =
                        snapshot.getValue(GroupConversation::class.java)!!
                    chatList.add(conversation)
                }
            }
            chatList.sortBy { it.lastTimestamps }
            if (chatList.size > 1) {
                chatList = chatList.reversed() as ArrayList<GroupConversation>
            }
            conversationListAdapter.addAllConversation(chatList)

            if (chatList.isNotEmpty()) {
                binding.rvGroupList.visibility = View.VISIBLE
                binding.emptyLayout.root.visibility = View.GONE
            } else {
                binding.emptyLayout.root.visibility = View.VISIBLE
                binding.rvGroupList.visibility = View.GONE
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            requireContext().toast(databaseError.message)
        }
    }

    override fun onStart() {
        mReference.addValueEventListener(chatListEventListener)
        super.onStart()
    }

    override fun onDestroy() {
        mReference.removeEventListener(chatListEventListener)
        super.onDestroy()
        _binding = null
    }


}