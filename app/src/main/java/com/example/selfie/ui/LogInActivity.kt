package com.example.selfie.ui

import android.Manifest.permission.*
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.selfie.R
import com.example.selfie.db.LoveDb
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.utils.Constants
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.example.selfie.viewModel.ViewModelProviderFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : BaseActivity() {
    private lateinit var viewModel: SelfieViewModel
    private lateinit var repository: Repository
    private lateinit var users:Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        users = Users()

        gallery()

        repository = Repository(LoveDb(this))
        val viewModelProvideFactory = ViewModelProviderFactory(repository,this)
        viewModel = ViewModelProvider(this,viewModelProvideFactory).get(SelfieViewModel::class.java)

        login_button.setOnClickListener {

            val gmail = login_email.text.toString().trim(){it<=' '}
            val password = login_password.text.toString()
            if(gmail.isNotEmpty()&&password.isNotEmpty()){

                viewModel.loginUser(gmail,password)

            }
            else{

                Toast.makeText(this,"შეავსეთ ყველა ველი",Toast.LENGTH_SHORT).show()

            }

        }

        viewModel.loginUser.observe(this, Observer {resource->

        when(resource){

            is Resource.Success->{

                resource.data?.let {resultResource->

                    hideDialog()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }

            }


            is Resource.Error->{

                resource.message!!.let { message ->
                    hideDialog()
                    Toast.makeText(this,message, Toast.LENGTH_SHORT).show()

                }
            }

            is Resource.Loading->{

                showDialog()

            }
        }



        })

        login_register.setOnClickListener {

            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)

        }

        login_forgot_password.setOnClickListener {

            val intent = Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)

        }



    }

    private fun gallery(){

        Dexter.withActivity(this)
            .withPermissions(
                // Permission request logic
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    android.Manifest.permission.READ_MEDIA_IMAGES
                    android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    android.Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                }
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationaleDialogForPermissions()
                }


            }).onSameThread().check()

    }

    private fun showRationaleDialogForPermissions(){

        AlertDialog.Builder(this).
        setMessage("turn off")
            .setPositiveButton("GO TO SETTINGS"){
                    _,_ ->
                try {

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }

            }.setNegativeButton("Cancel"){dialog,which ->
                dialog.dismiss()

            }.show()
    }
}