package com.aks.didi.network

import com.aks.didi.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface Api {
    @GET("auth?client_id=taxididi.app&client_secret={)lXo[yK%23c|BY\$/p3idEuwzU1=q_%26Rn9")
    @Headers("accept: application/json","content-type: application/json")
    suspend fun auth(): Response<Auth>

    @GET("checktoken?client_id=taxididi.app")
    @Headers("accept: application/json","content-type: application/json")
    suspend fun checkToken(
        @Query("token") sid: String
    ): Response<Auth>

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

    @POST("form/send")
    @Headers("accept: application/json")
    suspend fun sendSecond(
            @Header("authorization") sid: String,
            @Query("fullname") fullname: String,
            @Query("tel") phone: String,
            @Query("city") city: String,
            @Query("sts_front") stsFront: String?,
            @Query("sts_back") stsBack: String?,
            @Query("vu_front") vuFront: String?,
            @Query("vu_back") vuBack: String?,
            @Query("pasport") pasport: String?,
            @Query("vu_self") vuSelf: String?,
            @Query("uid") uid: UUID,
            @Query("form") from: String = "bid.main.didi"
    ): Response<ResponseWrapper>

    @POST("form/file/upload")
    @Headers("Accept: application/json")
    suspend fun loadImage(
            @Header("Authorization") sid: String,
            @Body body: MultipartBody,
            @Query("formfield") formfield: String,
            @Query("uid") uid: UUID,
            @Query("form") from: String = "bid.main.didi"
    ): Response<LoadImage>
}