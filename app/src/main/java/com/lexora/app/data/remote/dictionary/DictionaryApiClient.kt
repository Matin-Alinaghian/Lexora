package com.lexora.app.data.remote.dictionary

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DictionaryApiClient {

    private const val FREE_DICT_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"
    private const val FARSI_MATRIX_BASE_URL = "https://api-farsimatrix.ehsanjs.ir/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    val freeDictApi: DictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FREE_DICT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApiService::class.java)
    }

    val farsiMatrixApi: DictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FARSI_MATRIX_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApiService::class.java)
    }

    
    val genericApi: DictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mymemory.translated.net/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApiService::class.java)
    }
}
