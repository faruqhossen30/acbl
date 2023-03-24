package com.shopnolive.shopnolive.model.chat

import com.shopnolive.shopnolive.utils.Variable.myId
import java.io.Serializable

data class Conversation(
    val id: String = "",
    val name: String = "",
    val profileImage: String = "",
    val lastMessage: String = "",
    val receiverId: String = "",
    val messageType: String = "",
    val lastTimestamps: Long = 0L,
    val typing: Boolean = false,
    val seen: Boolean = false,
    val senderId: String = myId
) : Serializable