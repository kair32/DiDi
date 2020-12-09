package com.aks.didi.model

import com.google.gson.annotations.SerializedName
import java.util.*

interface RequestWrapper{
        val success: Boolean
        val errors: Any?
}

data class LoadImage(
        override val success: Boolean,
        override val errors: Any,
        val result: Result
):RequestWrapper

data class Result(
        val uploaded: List<Uploaded>
)
data class Uploaded(
        val name: String?,
        val error: Int?,
        val size: Long?,
        val id: String?,
        val thumb: String?,
)

data class ResponseWrapper(
        override val success: Boolean,
        override val errors: Any,
):RequestWrapper

data class Auth(
        override val success: Boolean,
        override val errors: Any,
        @SerializedName("access_token")
        val accessToken: String?,
        @SerializedName("expires_in")
        val expiresIn: Long?
): RequestWrapper


data class CityGetList(
        override val success: Boolean,
        override val errors: Any,
        val result: List<String>?
): RequestWrapper