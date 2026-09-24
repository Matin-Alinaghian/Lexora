package com.lexora.app.data.remote.dictionary

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface DictionaryApiService {

    
    @GET
    suspend fun getEnglishDefinition(@Url url: String): Response<List<FreeDictResponse>>

    
    @GET("search")
    suspend fun getFarsiTranslation(@Query("q") word: String): Response<FarsiMatrixResponse>

    
    @GET
    suspend fun getMyMemoryTranslation(@Url url: String): Response<MyMemoryResponse>
}
