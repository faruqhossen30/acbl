package com.shopnolive.shopnolive.model.chat

data class GroupMember(
    val memberId: String = "",
    val name: String = "",
    val profileImage: String = "",
    val joinAt: Long = 0L,
    val role: String = "",
    val lastTimestamps: Long = 0L,
    val seen: Boolean = false
)