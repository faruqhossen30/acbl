package com.shopnolive.shopnolive.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL
import com.shopnolive.shopnolive.databinding.LayoutConversationItemBinding
import com.shopnolive.shopnolive.model.chat.Conversation
import com.shopnolive.shopnolive.ui.chat.PersonalMessageActivity
import com.shopnolive.shopnolive.utils.Variable.myId
import com.shopnolive.shopnolive.utils.loadImageFromUrl
import java.text.SimpleDateFormat
import java.util.*

class ConversationListAdapter(
    private val context: Context
) : RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder>() {

    private var conversationList = mutableListOf<Conversation>()
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
            tvUserName.text = conversation.name
            ivProfileUserConversation.loadImageFromUrl("$BASE_URL${conversation.profileImage}")

            tvLastMsgConversation.text = conversation.lastMessage.take(35)

            if (!conversation.seen) {
                tvLastMsgConversation.setTypeface(null, Typeface.BOLD)
                tvLastMsgConversation.setTextColor(ContextCompat.getColor(context, R.color.black))
            } else {
                tvLastMsgConversation.setTypeface(null, Typeface.NORMAL)
                tvLastMsgConversation.setTextColor(ContextCompat.getColor(context, R.color.viewColor))
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
                Intent(holder.itemView.context, PersonalMessageActivity::class.java)
                    .putExtra("receiverId", conversation.id)
                    .putExtra("receiverName", conversation.name)
                    .putExtra("receiverImage", conversation.profileImage)
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.binding.tvUserName)
            popupMenu.inflate(R.menu.menu_action)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.action_delete) {
                    val messageSenderReference = "Messages/${conversation.id}/$myId"
                    val messageReceiverReference = "Messages/$myId/${conversation.id}"
                    val convSenderReference = "Conversation/${conversation.id}/$myId"
                    val convReceiverReference = "Conversation/$myId/${conversation.id}"
                    deleteMessage(
                        messageSenderReference,
                        messageReceiverReference,
                        convSenderReference,
                        convReceiverReference
                    )
                }
                true
            }
            popupMenu.show()
            true
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

    fun addAllConversation(newConversation: ArrayList<Conversation>) {
        conversationList = newConversation
        notifyDataSetChanged()
    }

    fun removeAllItem() {
        conversationList.clear()
    }

}
