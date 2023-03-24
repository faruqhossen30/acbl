package com.shopnolive.shopnolive.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor


class PreferenceUtils(context: Context) {

    // shared pref mode
    private var PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME = "app-notifications"

    private val IS_FIRST_TIME = "isFirstTime"
    private val IS_NOTIFICATION_ON = "isNotificationOn"
    private val IS_FCM_ON = "isFCMOn"
    private val NOTIFICATION_TIME = "notificationTime"
    private val UPDATE_SKIPPED = "updateSkipped"

    var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    var editor: Editor = sharedPreferences.edit()


    fun setNotifications(isNotification: Boolean) {
        editor.putBoolean(IS_NOTIFICATION_ON, isNotification)
        editor.commit()
    }

    fun setNotificationTime(time: Long) {
        editor.putLong(NOTIFICATION_TIME, time)
        editor.commit()
    }

    fun setFCM(isFCM: Boolean) {
        editor.putBoolean(IS_FCM_ON, isFCM)
        editor.commit()
    }

    fun setFirstTime(isFirstTime: Boolean) {
        editor.putBoolean(IS_FIRST_TIME, isFirstTime)
        editor.commit()
    }

    fun setUpdateSkipped(isUpdateSkipped: Boolean) {
        editor.putBoolean(UPDATE_SKIPPED, isUpdateSkipped)
        editor.commit()
    }

    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true)
    }

    fun isNotificationOn(): Boolean {
        return sharedPreferences.getBoolean(IS_NOTIFICATION_ON, true)
    }

    fun isFCMOn(): Boolean {
        return sharedPreferences.getBoolean(IS_FCM_ON, true)
    }

    fun getNotificationTime(): Long {
        return sharedPreferences.getLong(NOTIFICATION_TIME, 0L)
    }

    fun isUpdateSkipped(): Boolean {
        return sharedPreferences.getBoolean(UPDATE_SKIPPED, false)
    }

}