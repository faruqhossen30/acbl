package com.shopnolive.shopnolive.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.api.client.RetrofitNotificationInstance
import com.shopnolive.shopnolive.model.notification.CloudToken
import com.shopnolive.shopnolive.model.notification.NotificationData
import com.shopnolive.shopnolive.model.notification.PushNotification
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_KEY_MESSAGE
import com.shopnolive.shopnolive.utils.Variable.myId
import com.shopnolive.shopnolive.utils.Variable.myImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationSender {
    val TAG = "PUSH_NOTIFICATION"
    fun sendNotificationToReceiver(receiverId: String, name: String, textMessage: String) {
        val tokenRef = FirebaseDatabase.getInstance().getReference("Tokens").child(receiverId)
        Log.d("PUSH_NOTIFICATION", "ReceiverId: $receiverId")
        tokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val cloudToken = dataSnapshot.getValue(CloudToken::class.java)
                    Log.d("PUSH_NOTIFICATION", "Token: ${cloudToken!!.token}")
                    val data = NotificationData(
                        senderId = myId,
                        receiverId = receiverId,
                        senderName = name,
                        senderImage = myImage,
                        title = "New message",
                        description = "$name : $textMessage",
                        type = NOTIFICATION_KEY_MESSAGE,
                        extra = myId,
                        icon = R.drawable.app_icon
                    )

                    PushNotification(data, cloudToken.token).also {
                        sendPushNotification(it)
                    }
                } else {
                    Log.d("PUSH_NOTIFICATION", "Token: Token not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun sendPushNotification(it: PushNotification) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitNotificationInstance.api.postNotification(it)
                if (response.isSuccessful) {
                    //Log.d(TAG, "Response: ${com.google.gson.Gson().toJson(response)}")
                } else {
                    //Log.d(TAG, "Error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                //Log.d(TAG, "sendNotification: ${e.message}")
            }
        }
    }
}