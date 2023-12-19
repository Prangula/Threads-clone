package com.example.selfie.fcm

import com.example.selfie.models.Chat

data class PushNotification(

    val data:Chat,
    val to:String,
)
