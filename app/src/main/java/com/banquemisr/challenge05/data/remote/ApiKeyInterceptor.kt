package com.banquemisr.challenge05.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        
        val url = originalUrl.newBuilder()
           // .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()
        
        val request = originalRequest.newBuilder()
            .url(url)
            .build()
        
        return chain.proceed(request)
    }
}