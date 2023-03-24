package com.shopnolive.shopnolive.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL
import com.shopnolive.shopnolive.databinding.LayoutConversationItemBinding
import com.shopnolive.shopnolive.model.chat.GroupConversation
import com.shopnolive.shopnolive.ui.chat.GroupMessageActivity
import com.shopnolive.shopnolive.utils.loadImageFromUrl
import java.text.SimpleDateFormat
import java.util.*

class GroupChatListAdapter(
    private val context: Context
) : RecyclerView.Adapter<GroupChatListAdapter.ConversationViewHolder>() {

    private var conversationList = mutableListOf<GroupConversation>()
    private val rootReference = FirebaseDatabase.getInstance().reference

    inner class ConversationViewHolder(val binding: LayoutConversationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(
            LayoutConversationItemBinding.inflate(
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
            tvUserName.text = conversation.groupName
            ivProfileUserConversation.loadImageFromUrl("$BASE_URL${conversation.groupImage}")

            tvLastMsgConversation.text = conversation.lastConversation.take(35)

            if (!conversation.seen) {
                tvLastMsgConversation.setTypeface(null, Typeface.BOLD)
                tvLastMsgConversation.setTextColor(ContextCompat.getColor(context, R.color.black))
            } else {
                tvLastMsgConversation.setTypeface(null, Typeface.NORMAL)
                tvLastMsgConversation.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.viewColor
                    )
                )
            }

            // val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val sdf = SimpleDateFormat("dd/MM/yy")
            val netDate = Date(conversation.lastTimestamps)
            val date = sdf.format(netDate)
            tvDateConversation.text = date
        }

        // Click..
        holder.itemView.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, GroupMessageActivity::class.java)
                    .putExtra("groupId", conversation.groupId)
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun deleteMessage(
        messageSenderReference: String,
        messageReceiverReference: String,
        convSenderReference: String,
        convReceiverReference: String
    ) {
        rootReference.child(messageSenderReference).removeValue()
        rootReference.child(messageReceiverReference).removeValue()
        rootReference.child(convSenderReference).removeValue()
        rootReference.child(convReceiverReference).removeValue()
    }

    fun addAllConversation(newConversation: ArrayList<GroupConversation>) {
        conversationList = newConversation
        notifyDataSetChanged()
    }

    fun removeAllItem() {
        conversationList.clear()
    }

}
