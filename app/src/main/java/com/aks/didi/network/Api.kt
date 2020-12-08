package com.aks.didi.network

import com.aks.didi.model.Auth
import com.aks.didi.model.CityGetList
import com.aks.didi.model.FromSend
import com.aks.didi.model.ResponseWrapper
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface Api {
    @GET("auth?client_id=taxididi.app&client_secret={)lXo[yK%23c|BY\$/p3idEuwzU1=q_%26Rn9")
    @Headers("accept: application/json","content-type: application/json")
    suspend fun auth(): Response<Auth>

    @GET("city/getlist")
    @Headers("accept: application/json")
    suspend fun getCityList(
            @Header("authorization") sid: String
    ): Response<CityGetList>

    @POST("form/send")
    @Headers("accept: application/json")
    suspend fun sendFirst(
            @Header("authorization") sid: String,
            @Query("fullname") fullname: String,
            @Query("phone") phone: String,
            @Query("city") city: String,
            @Query("privacy") privacy: Boolean = true,
            @Query("form") from: String = "bid.hook.didi",
            @Query("uid") uid: UUID = UUID.randomUUID()
    ): Response<ResponseWrapper>

    @Multipart
    @POST("form/file/upload")
    @Headers("Accept: application/json","Content-Type: multipart/form-data;boundary=-------------573cf973d5228")
    suspend fun loadImage(
            @Header("Authorization") sid: String,
            @Part image: MultipartBody.Part,
            @Query("formfield") formfield: String,
            @Query("form") from: String = "bid.main.didi",
            @Query("uid") uid: UUID = UUID.randomUUID()
    ): Response<ResponseWrapper>
}