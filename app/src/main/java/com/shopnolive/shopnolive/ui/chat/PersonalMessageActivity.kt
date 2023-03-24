package com.shopnolive.shopnolive.ui.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shopnolive.shopnolive.adapter.MessageAdapter
import com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL
import com.shopnolive.shopnolive.databinding.ActivityPersonalMessageBinding
import com.shopnolive.shopnolive.model.BlockItem
import com.shopnolive.shopnolive.model.chat.Conversation
import com.shopnolive.shopnolive.model.chat.MessageItem
import com.shopnolive.shopnolive.utils.NotificationSender.sendNotificationToReceiver
import com.shopnolive.shopnolive.utils.Variable.*
import com.shopnolive.shopnolive.utils.loadImageFromUrl
import com.shopnolive.shopnolive.utils.toast
import com.google.firebase.database.*

class PersonalMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalMessageBinding

    private var conversation = Conversation()

    private lateinit var mReference: DatabaseReference
    private lateinit var rootReference: DatabaseReference
    private lateinit var chatReference: DatabaseReference
    private lateinit var conversationRef: DatabaseReference

    private lateinit var messageList: ArrayList<MessageItem>

    private var receiverId = ""
    private var receiverName = ""
    private var receiverImage = ""

    private var notify = false
    private var isBlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("login", MODE_PRIVATE)
        myId = sharedPreference.getString("myId", "") ?: ""
        myName = sharedPreference.getString("myName", "") ?: ""
        myImage = sharedPreference.getString("myImage", "") ?: ""
        //toast("ID: $myId Name: $myName")

        setSupportActionBar(binding.toolbar)

        //init
        //conversation = intent.getSerializableExtra("receiverInfo") as Conversation
        receiverId = intent.getStringExtra("receiverId").toString()
        receiverName = intent.getStringExtra("receiverName").toString()
        receiverImage = intent.getStringExtra("receiverImage").toString()
        val senderId = myId

        binding.tvReceiverName.text = receiverName
        binding.civReceiverImage.loadImageFromUrl("$BASE_URL${receiverImage}")


        rootReference = FirebaseDatabase.getInstance().reference
        chatReference = FirebaseDatabase.getInstance().getReference("Chats").child(senderId)
        conversationRef =
            FirebaseDatabase.getInstance().getReference("Conversation").child(senderId)
                .child(receiverId)
        mReference =
            FirebaseDatabase.getInstance().getReference("Messages").child(senderId)
                .child(receiverId)
        messageList = ArrayList()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = true
        binding.rvMessages.layoutManager = linearLayoutManager

        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.sendButtonMessage.setOnClickListener {
            notify = true
            if (binding.etMessage.text.toString().isEmpty()) {
                binding.etMessage.error = "Please enter message here"
                binding.etMessage.requestFocus()
            } else {
                val senderReference = "Messages/$senderId/$receiverId"
                val receiverReference = "Messages/$receiverId/$senderId"
                val myRef = rootReference.child("Messages").child(senderId).child(receiverId).push()
                val messageId = myRef.key
                val strMessage = binding.etMessage.text.toString()
                val message = MessageItem(
                    senderId,
                    receiverId,
                    myName,
                    strMessage,
                    messageId!!,
                    "text",
                    "none",
                    false
                )
                rootReference.child(senderReference).child(messageId).setValue(message)
                    .addOnFailureListener { e: Exception? ->
                        toast(e!!.localizedMessage!!)
                    }
                rootReference.child(receiverReference).child(messageId).setValue(message)
                    .addOnFailureListener { e: Exception? ->
                        toast(e!!.localizedMessage!!)
                    }
                /*chatReference.setValue(chatItem1).addOnFailureListener { e: Exception? ->
                    requireContext().toast(e!!.localizedMessage!!)
                }*/
                createConversation("text", strMessage)
                binding.etMessage.setText("")
            }
        }

        conversationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                conversation = snapshot.getValue(Conversation::class.java)!!
                setMessageSeen()
            }

            override fun onCancelled(error: DatabaseError) {
                toast(error.message)
            }

        })

        checkBlockStatus()
    }

    private fun createConversation(messageType: String, textMessage: String) {
        // Sender...
        val senderConversation = Conversation(
            id = receiverId,
            name = receiverName,
            profileImage = receiverImage,
            lastMessage = textMessage,
            receiverId = receiverId,
            messageType = messageType,
            lastTimestamps = System.currentTimeMillis(),
            typing = false,
            seen = false
        )
        // Receiver...
        val receiverConversation = Conversation(
            id = myId,
            name = myName,
            profileImage = myImage,
            lastMessage = textMessage,
            receiverId = receiverId,
            messageType = messageType,
            lastTimestamps = System.currentTimeMillis(),
            typing = false,
            seen = false
        )

        val ref: DatabaseReference =
            rootReference.child("Conversation").child(myId).child(receiverId)
        val ref2: DatabaseReference =
            rootReference.child("Conversation").child(receiverId).child(myId)

        ref.setValue(senderConversation)
        ref2.setValue(receiverConversation)
        if (notify) {
            notify = false
            sendNotificationToReceiver(
                receiverId,
                myName,
                textMessage
            )
        }
    }

    private val messageValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            messageList.clear()
            for (dataSnapshot1 in dataSnapshot.children) {
                val message: MessageItem = dataSnapshot1.getValue(MessageItem::class.java)!!
                messageList.add(message)
            }
            val mAdapter = MessageAdapter(this@PersonalMessageActivity, messageList, myId)
            binding.rvMessages.adapter = mAdapter
        }

        override fun onCancelled(databaseError: DatabaseError) {
            toast(databaseError.message)
        }
    }

    private fun setMessageSeen() {
        if (conversation.receiverId == myId && !myId.isNullOrEmpty() && receiverId.isNotEmpty()) {
            val ref: DatabaseReference =
                rootReference.child("Conversation").child(myId).child(receiverId).child("seen")
            val ref2: DatabaseReference =
                rootReference.child("Conversation").child(receiverId).child(myId).child("seen")
            ref.setValue(true)
            ref2.setValue(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(com.shopnolive.shopnolive.R.menu.menu_action_chat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            com.shopnolive.shopnolive.R.id.action_block -> {
                blockUser(receiverId)
                true
            }
            com.shopnolive.shopnolive.R.id.action_unblock -> {
                unblockUser(receiverId)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun unblockUser(receiverId: String) {
        rootReference.child("ConversationBlocks").child(receiverId)
            .child(myId).removeValue()
    }

    private fun blockUser(receiverId: String) {
        val blockItem = BlockItem(
            userId = myId,
            blockDuration = System.currentTimeMillis() + 604800000
        )
        rootReference.child("ConversationBlocks").child(receiverId).child(myId).setValue(blockItem)
            .addOnSuccessListener {
                isBlocked = true
                invalidateOptionsMenu()
            }
    }

    private fun checkBlockStatus() {
        val reference = rootReference.child("ConversationBlocks")
            .child(receiverId).child(myId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    isBlocked = true
                    invalidateOptionsMenu()
                } else {
                    isBlocked = false
                    invalidateOptionsMenu()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast(error.message)
            }

        })
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