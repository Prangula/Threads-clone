package com.example.selfie.fcm

import com.example.selfie.utils.Constants
import com.google.gson.internal.GsonBuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    }

    val api by lazy {


        retrofit.create(NotificationApi::class.java)

    }



}