package com.example.selfie.fcm

import com.example.selfie.models.Chat
import com.example.selfie.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key = ${Constants.SERVER_KEY}","Content-Type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification:PushNotification
    ):Response<ResponseBody>

}