package com.shopnolive.shopnolive.api.client

import com.shopnolive.shopnolive.FamousLiveApp.Companion.okHttpClient
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClientFactory {

    fun getRetroFitClient(): Retrofit {

        val builder = OkHttpClient().newBuilder().connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)

        okHttpClient =
            builder.addInterceptor(ResponseInterceptor()).addInterceptor(getInterceptor()).build()


        val gson = GsonBuilder().setLenient().disableHtmlEscaping().create()

        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    private fun getInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original: Request = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer ${com.shopnolive.shopnolive.utils.Variable.accessToken}"
                )
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    companion object {
        const val BASE_URL = "http://194.233.93.200/Api/"
    }
}

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder().body(
            ResponseBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                response.body()!!.bytes()
            )
        ).build()
    }
}

fun cancelAllApi() {
    okHttpClient?.dispatcher()?.cancelAll()
}