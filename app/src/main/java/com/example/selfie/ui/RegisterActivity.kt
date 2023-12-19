package com.example.selfie.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
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
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.db.LoveDb
import com.example.selfie.repository.Repository
import com.example.selfie.utils.Constants
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.example.selfie.viewModel.ViewModelProviderFactory
import com.google.api.ResourceProto.resource
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    private lateinit var viewModel: SelfieViewModel
    private lateinit var repository: Repository
    private var imageUri: Uri? = null
    private var imageUrl:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        repository = Repository(LoveDb(this))
        val viewModelProvideFactory = ViewModelProviderFactory(repository,this)
        viewModel = ViewModelProvider(this,viewModelProvideFactory).get(SelfieViewModel::class.java)

        register_image.setOnClickListener {

            gallery()

        }

        register_button.setOnClickListener {
            val name = register_name.text.toString()
            val lastname = register_lastname.text.toString()
            val selfieName = register_selfie_name.text.toString()
            val email = register_gmail.text.toString().trim(){it<=' '}
            val image = imageUri
            val password = register_password.text.toString()
            val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val fcmToken = sharedPreferences.getString("fcmToken","")


            if(name.isNotEmpty()&&lastname.isNotEmpty()&&email.isNotEmpty()
                &&image!=null&&password.isNotEmpty()){

                if (fcmToken != null) {
                    viewModel.registerUser(name,lastname,selfieName,email,image.toString(),password,fcmToken)
                }

            }else{

                Toast.makeText(this,"შეავსეთ ყველა ველი",Toast.LENGTH_SHORT).show()

            }




        }

        viewModel.uploadImageToCloudStorageLiveData.observe(this, Observer {resource->

            when(resource){

                is Resource.Success->{

                    resource.data.let { resultResponse ->
                      hideDialog()
                        imageUploadSuccess(imageUri.toString())
                    }

                }

                is Resource.Error->{
                    resource.message!!.let { message ->

                        hideDialog()

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }

                }

                is Resource.Loading->{
                    showDialog()

                }

            }


        })



      viewModel.registerUser.observe(this, Observer {resource->

          when(resource){

              is Resource.Success->{

                  resource.data.let { resultResponse ->
                      viewModel.uploadImageToCloudStorage(this,imageUri!!,Constants.IMAGE)
                      hideDialog()
                      FirebaseAuth.getInstance().signOut()
                      finish()
                  }

              }

              is Resource.Error->{
                  resource.message!!.let { message ->

                      hideDialog()

                      Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                  }

              }

              is Resource.Loading->{
                  showDialog()

              }

          }


      })



        toolbar()
    }






    private fun toolbar(){

        setSupportActionBar(toolbar_register)
        toolbar_register.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar_register.setNavigationOnClickListener {

            onBackPressed()
        }

    }

    fun imageUploadSuccess(imageUri:String){

        imageUrl = imageUri

    }

    private fun gallery(){

        Dexter.withActivity(this)
            .withPermissions(
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

                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
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

    companion object{

        const val GALLERY = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY && data!!.data!=null){

            imageUri = data!!.data!!

            Glide
                .with(this)
                .load(imageUri)
                ?.into(register_image)

        }

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