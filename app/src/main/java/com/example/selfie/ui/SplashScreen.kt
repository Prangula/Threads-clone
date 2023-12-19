package com.example.selfie.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import com.example.selfie.R
import com.example.selfie.db.LoveDb
import com.example.selfie.repository.Repository
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



            lifecycleScope.launch {

                delay(1500)

                if(Repository(LoveDb(this@SplashScreen)).getCurrentUserId().isNotEmpty()){

                    val intent = Intent(this@SplashScreen, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else{


                    val intent = Intent(this@SplashScreen, LogInActivity::class.java)
                    startActivity(intent)
                    finish()
                }





                finish()


            }



    }
}