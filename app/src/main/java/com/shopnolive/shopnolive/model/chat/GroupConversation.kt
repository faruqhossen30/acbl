package com.shopnolive.shopnolive.model.chat

data class GroupConversation(
    val groupId: String = "",
    val groupName: String = "",
    val createdTime: Long = 0L,
    val groupImage: String = "",
    val privacy: String = "",
    val creatorId: String = "",
    val lastTimestamps: Long = 0L,
    val seen: Boolean = false,
    val lastConversation: String = ""
)