package com.shopnolive.shopnolive.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.model.chat.MessageItem
import com.shopnolive.shopnolive.utils.Variable.myId

class MessageAdapter(
    private val context: Context,
    private val messages: ArrayList<MessageItem>,
    private val senderId: String,
    private val isGroup: Boolean = false
) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1
    private val rootReference = FirebaseDatabase.getInstance().reference

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var showMessage: TextView = itemView.findViewById(R.id.show_message)
        var senderName: TextView = itemView.findViewById(R.id.tv_sender_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == MESSAGE_RIGHT) {
            MessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_chat_item_right, parent, false)
            )
        } else {
            MessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_chat_item_left, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.senderName.isVisible = isGroup
        holder.showMessage.text = messages[position].message
        holder.senderName.text = messages[position].senderName
        var receiverId = messages[position].receiverId
        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.showMessage)
            popupMenu.inflate(R.menu.menu_action)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.action_delete) {
                    if (receiverId == myId) receiverId = messages[position].senderId
                    val senderReference = "Messages/$senderId/$receiverId"
                    val receiverReference = "Messages/$receiverId/$senderId"
                    deleteMessage(senderReference, receiverReference, messages[position].messageId)
                }
                true
            }
            popupMenu.show()
            true
        }
    }

    private fun deleteMessage(
        senderReference: String,
        receiverReference: String,
        messageId: String
    ) {
        rootReference.child(senderReference).child(messageId).removeValue()
        rootReference.child(receiverReference).child(messageId).removeValue()
        Log.d("deleteMessage", "deleteMessage: $messageId")
        Log.d("deleteMessage", "deleteMessage: ${rootReference.child(senderReference).child(messageId)}")
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == senderId) {
            MESSAGE_RIGHT
        } else {
            MESSAGE_LEFT
        }
    }
}