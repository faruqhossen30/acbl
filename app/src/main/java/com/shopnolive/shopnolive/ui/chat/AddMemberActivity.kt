package com.shopnolive.shopnolive.ui.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.shopnolive.shopnolive.adapter.AddGroupMemberAdapter
import com.shopnolive.shopnolive.databinding.ActivityAddMemberBinding
import com.shopnolive.shopnolive.model.chat.Conversation
import com.shopnolive.shopnolive.utils.Variable
import com.shopnolive.shopnolive.utils.toast

class AddMemberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMemberBinding
    private lateinit var groupReference: DatabaseReference
    private lateinit var mReference: DatabaseReference

    private lateinit var chatList: ArrayList<Conversation>
    private lateinit var conversationListAdapter: AddGroupMemberAdapter

    private var groupId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup toolbar
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar!!.title = "Add Member"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init
        groupId = intent.getStringExtra("groupId").toString()
        groupReference =
            FirebaseDatabase.getInstance().getReference("GroupConversations").child(groupId)
        mReference =
            FirebaseDatabase.getInstance().getReference("Conversation").child(Variable.myId)
        conversationListAdapter = AddGroupMemberAdapter(this, groupId)
        val linearLayoutManager = LinearLayoutManager(this)
        chatList = ArrayList()

        binding.recyclerViewMemberList.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = conversationListAdapter
        }

        binding.btnDoneAddMember.setOnClickListener {
            val intent = Intent(this@AddMemberActivity, GroupInfoActivity::class.java)
                .putExtra("groupId", groupId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            this@AddMemberActivity.finish()
        }

    }

    private val chatListEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            chatList.clear()
            for (snapshot in dataSnapshot.children) {
                val message: Conversation = snapshot.getValue(Conversation::class.java)!!
                chatList.add(message)
            }
            chatList.sortBy { it.lastTimestamps }
            if (chatList.size > 1) {
                chatList = chatList.reversed() as ArrayList<Conversation>
            }
            conversationListAdapter.addAllConversation(chatList)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            toast(databaseError.message)
        }
    }

    override fun onStart() {
        mReference.addValueEventListener(chatListEventListener)
        super.onStart()
    }

    override fun onDestroy() {
        mReference.removeEventListener(chatListEventListener)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}