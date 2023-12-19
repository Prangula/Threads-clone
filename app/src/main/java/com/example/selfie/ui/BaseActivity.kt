package com.example.selfie.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.selfie.R

open class BaseActivity : AppCompatActivity() {
    private lateinit var dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showDialog(){

        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog)
        dialog.show()

    }

    fun hideDialog(){

        if(dialog!=null){

            dialog.dismiss()
        }
    }


}