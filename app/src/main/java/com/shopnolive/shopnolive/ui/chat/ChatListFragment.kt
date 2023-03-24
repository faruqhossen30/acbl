package com.shopnolive.shopnolive.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.shopnolive.shopnolive.adapter.ConversationListAdapter
import com.shopnolive.shopnolive.databinding.FragmentChatListBinding
import com.shopnolive.shopnolive.model.chat.Conversation
import com.shopnolive.shopnolive.utils.Variable.conversationBlockedIdList
import com.shopnolive.shopnolive.utils.Variable.myId
import com.shopnolive.shopnolive.utils.toast


class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mReference: DatabaseReference

    private lateinit var chatList: ArrayList<Conversation>

    private lateinit var conversationListAdapter: ConversationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init
        chatList = ArrayList()

        mReference = FirebaseDatabase.getInstance().getReference("Conversation").child(myId)
        conversationListAdapter = ConversationListAdapter(requireContext())
        val linearLayoutManager = LinearLayoutManager(requireContext())

        binding.rvChatList.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = conversationListAdapter
        }

        //mReference.addValueEventListener(chatListEventListener)

    }

    private val chatListEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            chatList.clear()
            for (snapshot in dataSnapshot.children) {
                val message: Conversation = snapshot.getValue(Conversation::class.java)!!
                if (!conversationBlockedIdList.contains(message.senderId) && !conversationBlockedIdList.contains(message.receiverId)){
                    chatList.add(message)
                }
            }
            chatList.sortBy { it.lastTimestamps }
            if (chatList.size > 1){
                chatList = chatList.reversed() as ArrayList<Conversation>
            }
            conversationListAdapter.addAllConversation(chatList)

            if (chatList.isNotEmpty()) {
                binding.rvChatList.visibility = View.VISIBLE
                binding.emptyLayout.root.visibility = View.GONE
            } else {
                binding.emptyLayout.root.visibility = View.VISIBLE
                binding.rvChatList.visibility = View.GONE
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