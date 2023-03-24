package com.shopnolive.shopnolive.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel
import com.shopnolive.shopnolive.adapter.MessageAdapter
import com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL
import com.shopnolive.shopnolive.databinding.FragmentMessageBinding
import com.shopnolive.shopnolive.model.BlockItem
import com.shopnolive.shopnolive.model.chat.Conversation
import com.shopnolive.shopnolive.model.chat.MessageItem
import com.shopnolive.shopnolive.utils.*
import com.shopnolive.shopnolive.utils.Variable.*


class MessageFragment(
    private val senderId: String,
    private val receiverId: String,
    private val isHost: Boolean = false
) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mReference: DatabaseReference
    private lateinit var rootReference: DatabaseReference
    private lateinit var chatReference: DatabaseReference

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var messageList: ArrayList<MessageItem>

    private var receiverName = ""
    private var receiverImage = ""

    private var isSeen = false
    private var notify = false
    private var isFollowEnable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootReference = FirebaseDatabase.getInstance().reference
        chatReference = FirebaseDatabase.getInstance().getReference("Chats").child(senderId)
        mReference =
            FirebaseDatabase.getInstance().getReference("Messages").child(senderId)
                .child(receiverId)
        messageList = ArrayList()

        profileViewModel = ViewModelProviders.of(this)[ProfileViewModel::class.java]

        binding.btnBlockDialog.isVisible = isHost

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = true
        binding.rvMessages.layoutManager = linearLayoutManager

        loadUserProfile()

        binding.btnReportDialog.setOnClickListener {
            requireContext().toast("Reported")
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
                /*val chatItem1 = ChatItem(
                    senderId,
                    user.name,
                    user.phoneNumber,
                    user.imageUrl,
                    strMessage,
                    System.currentTimeMillis()
                )*/
                rootReference.child(senderReference).child(messageId).setValue(message)
                    .addOnFailureListener { e: Exception? ->
                        requireContext().toast(e!!.localizedMessage!!)
                    }
                rootReference.child(receiverReference).child(messageId).setValue(message)
                    .addOnFailureListener { e: Exception? ->
                        requireContext().toast(e!!.localizedMessage!!)
                    }
                /*chatReference.setValue(chatItem1).addOnFailureListener { e: Exception? ->
                    requireContext().toast(e!!.localizedMessage!!)
                }*/
                createConversation("text", strMessage)
                binding.etMessage.setText("")
            }
        }

        binding.btnBlockDialog.setOnClickListener {
            blockUser()
        }

        binding.userFollowForHistory.setOnClickListener {
            if (isFollowEnable) {
                isFollowEnable = false
                if (binding.userFollowForHistory.tag == "not_following") {
                    FirebaseMessaging.getInstance().subscribeToTopic(receiverId)
                    followUser()
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(receiverId)
                    unfollowUser()
                }
            }
        }

        if (conversationBlockedIdList.contains(receiverId)) {
            binding.messageLayout.visibility = View.GONE
            binding.rvMessages.visibility = View.GONE
        }
    }

    private fun unfollowUser() {
        callApi(getRestApis().unFollowUser(senderId, receiverId),
            onApiSuccess = {
                setUnfollow()
            }, onApiError = {
                setFollow()
            }
        )
    }

    private fun followUser() {
        callApi(getRestApis().followUser(senderId, receiverId),
            onApiSuccess = {
                setFollow()
            },
            onApiError = {
                setUnfollow()
            }
        )
    }

    private fun blockUser() {
        myRef.child(playId).child("view").child(receiverId).child("status")
            .setValue("blocked")
        val blockItem = BlockItem(
            userId = senderId,
            blockDuration = System.currentTimeMillis() + 604800000
        )
        rootReference.child("BlockedUsers").child(receiverId).child(senderId).setValue(blockItem)
        /*callApi(
            getRestApis().blockSpecificUser(receiverId.toInt(), 24),
            onApiSuccess = {
                requireContext().toast("User successfully blocked")
            },
            onApiError = {
                requireContext().toast("Failed to block")
            }
        )*/
    }

    private fun loadUserProfile() {
        profileViewModel.getProfileById(receiverId).observe(this) {
            if (it != null) {
                receiverName = it.name
                binding.tvUserName.text = it.name
                binding.tvLevel.text = it.userLevel.toString()
                val idText = "ID: ${it.id}"
                binding.tvUserId.text = idText
                val diamondText = "\uD83D\uDC8E ${it.presentCoinBalance}"
                binding.tvUserDiamond.text = diamondText
                if (!it.image.isNullOrEmpty()) {
                    receiverImage = it.image
                    binding.ivProfile.loadImageFromUrl("$BASE_URL${it.image}")
                }
                checkFollow()
            }
        }
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
            id = senderId,
            name = userInfo.name,
            profileImage = userInfo.image,
            lastMessage = textMessage,
            receiverId = receiverId,
            messageType = messageType,
            lastTimestamps = System.currentTimeMillis(),
            typing = false,
            seen = false
        )

        val ref: DatabaseReference =
            rootReference.child("Conversation").child(senderId).child(receiverId)
        val ref2: DatabaseReference =
            rootReference.child("Conversation").child(receiverId).child(senderId)

        ref.setValue(senderConversation)
        ref2.setValue(receiverConversation).addOnSuccessListener {
            if (notify) {
                notify = false
                NotificationSender.sendNotificationToReceiver(
                    receiverId,
                    userInfo.name,
                    textMessage
                )
            }
        }
    }

    private fun checkFollow() {
        if (userProfileFollow != null) {
            var follow = 0
            for (followers in userProfileFollow) {
                if (followers.id.equals(receiverId)) {
                    follow++
                }
            }
            if (follow > 0) {
                setFollow()
            } else {
                setUnfollow()
            }
        } else {
            setUnfollow()
        }
    }

    private fun setFollow() {
        isFollowEnable = true
        binding.userFollowForHistory.tag = "following"
        binding.userFollowForHistory.text="Following"
        //binding.userFollowForHistory.setImageResource(R.drawable.ic_min)
    }

    private fun setUnfollow() {
        isFollowEnable = true
        binding.userFollowForHistory.tag = "not_following"
        binding.userFollowForHistory.text="Follow"

        // binding.userFollowForHistory.setImageResource(R.drawable.plus)
    }

    private val messageValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            messageList.clear()
            for (dataSnapshot1 in dataSnapshot.children) {
                val message: MessageItem = dataSnapshot1.getValue(MessageItem::class.java)!!
                messageList.add(message)
            }
            if (messageList.size > 1 && !isSeen) {
                setMessageSeen(messageList[messageList.size - 1])
            }
            val mAdapter = MessageAdapter(requireContext(), messageList, senderId)
            binding.rvMessages.adapter = mAdapter
        }

        override fun onCancelled(databaseError: DatabaseError) {
            requireContext().toast(databaseError.message)
        }
    }

    private fun setMessageSeen(messageItem: MessageItem) {
        if (messageItem.receiverId == myId && senderId.isNotEmpty() && receiverId.isNotEmpty()) {
            val ref: DatabaseReference =
                rootReference.child("Conversation").child(myId).child(receiverId).child("seen")
            val ref2: DatabaseReference =
                rootReference.child("Conversation").child(receiverId).child(myId).child("seen")
            ref.setValue(true)
            ref2.setValue(true)
        }
    }

    override fun onDestroy() {
        mReference.removeEventListener(messageValueEventListener)
        Log.d("MessageFragment", "onDestroy: $receiverId")
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d("MessageFragment", "onStart: $receiverId")
        mReference.addValueEventListener(messageValueEventListener)
    }
}