package com.example.selfie.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.selfie.R
import com.example.selfie.adapters.PostAdapter
import com.example.selfie.db.LoveDb
import com.example.selfie.db.Posts
import com.example.selfie.repository.Repository
import com.example.selfie.viewModel.SelfieViewModel
import com.example.selfie.viewModel.ViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPasswordActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        forgot_button.setOnClickListener {

            password()
        }


        toolbar()
    }

    private fun toolbar(){

        setSupportActionBar(toolbar_forgot)
        toolbar_forgot.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar_forgot.setNavigationOnClickListener {

            onBackPressed()
        }

    }

    private fun password(){

        if(forgot_email.text.isNotEmpty()){

            showDialog()

            val email = forgot_email.text.toString().trim(){it<= ' '}

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task->

                    if(task.isSuccessful){


                        Toast.makeText(this@ForgotPasswordActivity,
                            "იმეილი წარმატებით გამოიგზავნა", Toast.LENGTH_LONG)
                            .show()
                        hideDialog()
                        finish()
                    }else{

                        Toast.makeText(this,task.exception!!.message, Toast.LENGTH_SHORT)
                            .show()
                        hideDialog()
                    }
                }


        }else{
            Toast.makeText(this,"შეავსეთ ყველა ველი",Toast.LENGTH_SHORT).show()
        }

    }

}