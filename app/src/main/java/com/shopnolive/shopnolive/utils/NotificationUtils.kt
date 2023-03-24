package com.shopnolive.shopnolive.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shopnolive.shopnolive.R
import kotlin.random.Random

object NotificationUtils {

    private const val CHANNEL_ID = "speak_english_online"

    fun sendNotification(
        context: Context?,
        intent: Intent?,
        title: String,
        body: String,
        senderImage: String
    ) {

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //val soundUri = Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.notification_tone)

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()

            val channelName = "channelName"
            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "My channel description"
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                lightColor = Color.GREEN
                setSound(soundUri, attributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intentFlags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            intentFlags
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setSmallIcon(R.drawable.app_icon)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        notificationManager.notify(notificationId, notification)
    }
}