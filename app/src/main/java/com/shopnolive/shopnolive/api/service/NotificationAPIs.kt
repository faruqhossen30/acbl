package com.shopnolive.shopnolive.api.service

import com.shopnolive.shopnolive.model.notification.PushNotification
import com.shopnolive.shopnolive.utils.Constants.CONTENT_TYPE
import com.shopnolive.shopnolive.utils.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPIs {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}