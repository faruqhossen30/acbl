package com.shopnolive.shopnolive.services

import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shopnolive.shopnolive.model.notification.CloudToken
import com.shopnolive.shopnolive.ui.intro.SplashActivity
import com.shopnolive.shopnolive.ui.chat.PersonalMessageActivity
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_DESCRIPTION
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_EXTRA
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_KEY_LIVE
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_KEY_MESSAGE
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_SENDER_IMAGE
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_SENDER_NAME
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_TITLE
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_TYPE
import com.shopnolive.shopnolive.utils.NotificationUtils.sendNotification
import com.shopnolive.shopnolive.utils.Variable.myId

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationData = remoteMessage.data
        val title = notificationData[NOTIFICATION_TITLE].toString()
        val description = notificationData[NOTIFICATION_DESCRIPTION].toString()
        val senderName = notificationData[NOTIFICATION_SENDER_NAME].toString()
        val senderImage = notificationData[NOTIFICATION_SENDER_IMAGE].toString()
        val notificationType = notificationData[NOTIFICATION_TYPE].toString()
        val notificationExtra = notificationData[NOTIFICATION_EXTRA].toString()

        if (notificationType == NOTIFICATION_KEY_LIVE) {
            sendNotification(
                this,
                Intent(this@FCMService, SplashActivity::class.java).putExtra(
                    "playId",
                    notificationExtra
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                title,
                description,
                senderImage
            )
        } else if (notificationType == NOTIFICATION_KEY_MESSAGE) {
            sendNotification(
                this,
                Intent(this@FCMService, PersonalMessageActivity::class.java)
                    .putExtra("receiverId", notificationExtra)
                    .putExtra("receiverName",senderName)
                    .putExtra("receiverImage",senderImage)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                title,
                description,
                senderImage
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if (!myId.isNullOrEmpty()) {
            val cloudToken = CloudToken(token = token)
            FirebaseDatabase.getInstance().getReference("Tokens")
                .child(myId)
                .setValue(cloudToken)
        }
    }
}