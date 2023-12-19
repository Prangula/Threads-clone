package com.example.selfie.utils

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                saveTokenToSharedPreferences(token)

            }

    }

    private fun saveTokenToSharedPreferences(token: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("fcmToken", token)
        editor.apply()
    }


}