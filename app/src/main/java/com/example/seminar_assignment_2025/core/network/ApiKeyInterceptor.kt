package com.example.seminar_assignment_2025.core.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val newUrl = original.url.newBuilder()
            .addQueryParameter("api_key", apiKey) // 또는 헤더로
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            // .addHeader("X-API-KEY", apiKey)
            .build()

        return chain.proceed(newRequest)
    }
}
