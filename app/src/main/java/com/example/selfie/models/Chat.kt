package com.example.selfie.models

import java.io.Serial
import java.util.Date

data class Chat (

    val message:String = "",
    val title:String = "", // for Notification
    val date:Date = Date(),
    var senderId:String = "",
    var receiverId:String = "",
    var lastMessage:String = "",
    var id:String = ""


):java.io.Serializable