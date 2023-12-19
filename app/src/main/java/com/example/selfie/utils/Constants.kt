package com.example.selfie.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {

    const val USERS = "users"
    const val IMAGE = "image"
    const val POST = "posts"
    const val UPLOADER_ID = "uploaderId"
    const val USER_PROFILE = "userProfile"
    const val CHAT = "chat"
    var currentActivity:String? =null // for Notification




    // NOTIFICATION

    const val BASE_URL = "https://fcm.googleapis.com"
    const val CONTENT_TYPE = "application/json"


    fun getFileExtension(activity: Activity, imageUri: Uri):String{

        // file can be jpg,jpeg,png ... etc
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver .getType(imageUri))!!


    }

}