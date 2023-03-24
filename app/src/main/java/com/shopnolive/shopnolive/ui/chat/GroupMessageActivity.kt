package com.shopnolive.shopnolive.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.shopnolive.shopnolive.adapter.MessageAdapter
import com.shopnolive.shopnolive.databinding.ActivityGroupMessageBinding
import com.shopnolive.shopnolive.model.chat.GroupConversation
import com.shopnolive.shopnolive.model.chat.MessageItem
import com.shopnolive.shopnolive.utils.Variable.myId
import com.shopnolive.shopnolive.utils.Variable.myName
import com.shopnolive.shopnolive.utils.toast

class GroupMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupMessageBinding

    private lateinit var groupReference: DatabaseReference
    private lateinit var mReference: DatabaseReference
    private lateinit var messageList: ArrayList<MessageItem>

    private var groupId = ""
    private var groupInfo = GroupConversation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        groupId = intent.getStringExtra("groupId").toString()
        groupReference =
            FirebaseDatabase.getInstance().getReference("GroupConversations").child(groupId)
        mReference = FirebaseDatabase.getInstance().getReference("GroupMessages").child(groupId)
        messageList = ArrayList()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = true
        binding.rvMessages.layoutManager = linearLayoutManager

        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.sendButtonMessage.setOnClickListener {
            if (binding.etMessage.text.toString().isEmpty()) {
                binding.etMessage.error = "Please enter message here"
                binding.etMessage.requestFocus()
            } else {
                val messageId = groupReference.push().key
                val strMessage = binding.etMessage.text.toString()
                val message = MessageItem(
                    myId,
                    groupId,
                    myName,
                    strMessage,
                    messageId!!,
                    "text",
                    "none",
                    false
                )
                mReference.child(messageId).setValue(message)
                groupReference.child("lastConversation").setValue(strMessage)
                binding.etMessage.setText("")
            }
        }

        getGroupInfo()

        binding.tvReceiverName.setOnClickListener {
            startActivity(
                Intent(this, GroupInfoActivity::class.java)
                    .putExtra("groupId", groupId)
            )
        }

    }

    private fun getGroupInfo() {
        groupReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    groupInfo = dataSnapshot.getValue(GroupConversation::class.java)!!
                    updateGroupInfoUI()
                } else {
                    toast("No Group info.")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toast(p0.message)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateGroupInfoUI() {
        binding.tvReceiverName.text = groupInfo.groupName
    }

    private val messageValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            messageList.clear()
            for (dataSnapshot1 in dataSnapshot.children) {
                val message: MessageItem = dataSnapshot1.getValue(MessageItem::class.java)!!
                messageList.add(message)
            }
            val mAdapter = MessageAdapter(this@GroupMessageActivity, messageList, myId,true)
            binding.rvMessages.adapter = mAdapter
        }

        override fun onCancelled(databaseError: DatabaseError) {
            toast(databaseError.message)
        }
    }

    override fun onDestroy() {
        mReference.removeEventListener(messageValueEventListener)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        mReference.addValueEventListener(messageValueEventListener)
    }
}