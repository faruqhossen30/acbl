package com.shopnolive.shopnolive.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL
import com.shopnolive.shopnolive.databinding.LayoutAddMemberBinding
import com.shopnolive.shopnolive.model.chat.Conversation
import com.shopnolive.shopnolive.model.chat.GroupMember
import com.shopnolive.shopnolive.utils.loadImageFromUrl
import java.util.*

class AddGroupMemberAdapter(
    private val context: Context,
    private val groupId: String
) : RecyclerView.Adapter<AddGroupMemberAdapter.ConversationViewHolder>() {

    private var conversationList = mutableListOf<Conversation>()
    private val groupReference = FirebaseDatabase.getInstance().getReference("GroupConversations")

    inner class ConversationViewHolder(val binding: LayoutAddMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(
            LayoutAddMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = conversationList.size

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversationList[position]

        with(holder.binding) {
            userFullName.text = conversation.name
            userProfilePic.loadImageFromUrl("$BASE_URL${conversation.profileImage}")
        }

        holder.binding.addUserBtn.setOnClickListener {
            addMemberToGroup(conversation, holder.binding.addUserBtn)
        }

    }

    private fun addMemberToGroup(conversation: Conversation, addUserBtn: TextView) {
        val timestamp = System.currentTimeMillis()
        val member = GroupMember(
            conversation.id,
            conversation.name,
            conversation.profileImage,
            timestamp,
            "member",
            timestamp,
            false
        )
        groupReference.child(groupId).child("Members").child(conversation.id)
            .setValue(member).addOnSuccessListener {
                addUserBtn.text = "Added"
            }
    }

    fun addAllConversation(newConversation: ArrayList<Conversation>) {
        conversationList = newConversation
        notifyDataSetChanged()
    }

    fun removeAllItem() {
        conversationList.clear()
    }

}
