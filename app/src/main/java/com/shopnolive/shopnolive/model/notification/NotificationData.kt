package com.shopnolive.shopnolive.model.notification

data class NotificationData(
    val senderId: String = "",
    val receiverId: String = "",
    val senderName: String = "",
    val senderImage: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "",
    val extra: String = "",
    val icon: Int = 0
)