package com.aks.didi.model

import com.google.gson.annotations.SerializedName
import java.util.*

interface RequestWrapper{
        val success: Boolean
        val errors: Any?
}
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


data class FromSend(
        val fullname: String,
        val phone: String,
        val city: String,
        val privacy: Boolean = true,
        val form: String = "bid.hook.didi",
        val uid: UUID = UUID.randomUUID()
)