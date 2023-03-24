package com.shopnolive.shopnolive.model.chat

data class MessageItem(
    val senderId: String = "",
    val receiverId: String = "",
    val senderName: String = "",
    val message: String = "",
    val messageId: String = "",
    val type: String = "",
    val imageUrl: String = "",
    val seen: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)