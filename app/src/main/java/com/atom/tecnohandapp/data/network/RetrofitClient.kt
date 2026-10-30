package com.atom.tecnohandapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Default base URL for Android emulator accessing local machine host
    var baseUrl: String = "http://10.0.2.2:3000/api/"
        private set

    private var apiService: TecnoHandApiService? = null
    private var githubApiService: GithubApiService? = null

    fun getService(customUrl: String? = null): TecnoHandApiService {
        val newUrl = formatUrl(customUrl ?: baseUrl)

        if (apiService == null || baseUrl != newUrl) {
            baseUrl = newUrl

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(TecnoHandApiService::class.java)
        }

        return apiService!!
    }

    fun getGithubService(): GithubApiService {
        if (githubApiService == null) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            githubApiService = retrofit.create(GithubApiService::class.java)
        }
        return githubApiService!!
    }

    private fun formatUrl(url: String): String {
        var formatted = url.trim()
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "http://$formatted"
        }
        if (!formatted.endsWith("/")) {
            formatted = "$formatted/"
        }
        if (!formatted.endsWith("api/")) {
            formatted = "${formatted}api/"
        }
        return formatted
    }
}
