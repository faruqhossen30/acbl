package com.shopnolive.shopnolive.api.client

import com.shopnolive.shopnolive.api.service.NotificationAPIs
import com.shopnolive.shopnolive.utils.Constants.BASE_URL_NOTIFICATION
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitNotificationInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_NOTIFICATION)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NotificationAPIs by lazy {
        retrofit.create(NotificationAPIs::class.java)
    }
}