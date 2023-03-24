package com.shopnolive.shopnolive.utils

import android.Manifest
import io.agora.rtc.Constants
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions

object Constants {

    const val REQUEST_CODE_STORAGE_PERMISSION = 1001
    const val REQUEST_CODE_ALL_PERMISSION = 1002
    const val REQUEST_CODE_CAMERA_AND_AUDIO_PERMISSION = 1003
    const val REQUEST_CODE_SELECT_IMAGE = 2001

    val CAMERA_AND_AUDIO_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    val STORAGE_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val ALL_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO
    )

    const val BASE_URL_NOTIFICATION = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAAry_kpxQ:APA91bE3xWiYosKJMvo3qDChukRBKOG_p6mdeexII6xFxoOJ-SbiuI4FDgJ_KBDw9n6Cbx3AiIJtHZmTFT7bqVmNRqoytoS7O5HWLBjM9iIYaeAqMHnfLffoH_W6_WFuDf9yRLEv8YwE"
    const val CONTENT_TYPE ="application/json"

    const val NOTIFICATION_TITLE = "title"
    const val NOTIFICATION_DESCRIPTION= "description"
    const val NOTIFICATION_SENDER_NAME = "senderName"
    const val NOTIFICATION_SENDER_IMAGE = "senderImage"
    const val NOTIFICATION_SENDER_ID = "senderId"
    const val NOTIFICATION_TYPE = "type"
    const val NOTIFICATION_EXTRA = "extra"
    const val NOTIFICATION_KEY_LIVE = "live"
    const val NOTIFICATION_KEY_MESSAGE = "message"

    object AgoraConst{
        private const val BEAUTY_EFFECT_DEFAULT_CONTRAST = BeautyOptions.LIGHTENING_CONTRAST_NORMAL
        private const val BEAUTY_EFFECT_DEFAULT_LIGHTNESS = 0.7f
        private const val BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = 0.5f
        private const val BEAUTY_EFFECT_DEFAULT_REDNESS = 0.1f

        val DEFAULT_BEAUTY_OPTIONS = BeautyOptions(
            BEAUTY_EFFECT_DEFAULT_CONTRAST,
            BEAUTY_EFFECT_DEFAULT_LIGHTNESS,
            BEAUTY_EFFECT_DEFAULT_SMOOTHNESS,
            BEAUTY_EFFECT_DEFAULT_REDNESS
        )

        var VIDEO_DIMENSIONS = arrayOf(
            VideoEncoderConfiguration.VD_320x240,
            VideoEncoderConfiguration.VD_480x360,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            VideoDimensions(960, 540),
            VideoEncoderConfiguration.VD_1280x720
        )

        var VIDEO_MIRROR_MODES = intArrayOf(
            Constants.VIDEO_MIRROR_MODE_AUTO,
            Constants.VIDEO_MIRROR_MODE_ENABLED,
            Constants.VIDEO_MIRROR_MODE_DISABLED
        )

        const val PREF_NAME = "io.agora.openlive"
        const val DEFAULT_PROFILE_IDX = 2
        const val PREF_RESOLUTION_IDX = "pref_profile_index"
        const val PREF_ENABLE_STATS = "pref_enable_stats"
        const val PREF_MIRROR_LOCAL = "pref_mirror_local"
        const val PREF_MIRROR_REMOTE = "pref_mirror_remote"
        const val PREF_MIRROR_ENCODE = "pref_mirror_encode"

        const val KEY_CLIENT_ROLE = "key_client_role"
    }
}