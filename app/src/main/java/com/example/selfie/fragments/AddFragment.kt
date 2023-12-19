package com.example.selfie.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.models.Users
import com.example.selfie.ui.MainActivity
import com.example.selfie.utils.Constants
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_profile.*

class AddFragment:Fragment(R.layout.fragment_add) {
    private lateinit var viewModel: SelfieViewModel
    private  var dialog: Dialog?=null
    private var imageUri: Uri? = null
    private var imageUrl:String = ""
    private var user = Users()
    private var uploaderSelfieName:String = ""
    private var uploaderImage:String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        viewModel.profileDetails()


// ფრაგმენტში რო გადავალთ EditText-ზე რო მოხდეს ფოკუსი და კლავიატურა რო ამოვარდეს.
        add_editText.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(add_editText, InputMethodManager.SHOW_IMPLICIT)



        add_image_button.setOnClickListener {

            gallery()

            add_image_button.visibility = View.GONE
            add_image.visibility = View.VISIBLE

        }





        add_button.setOnClickListener {

            uploaderSelfieName = add_name!!.text.toString()
            val text = add_editText!!.text.toString()
            Glide.with(this)
                .load(uploaderImage)
                .into(add_profile_image)

            if(text.isNotEmpty()){

                viewModel.registerPost(uploaderSelfieName,uploaderImage,text,imageUri.toString())
                add_editText.text.clear()
                add_image_button.visibility = View.VISIBLE
                add_image.visibility = View.GONE


            }else{
                Toast.makeText(activity,"შეავსეთ ყველა ველი",Toast.LENGTH_SHORT).show()
            }




        }

        viewModel.uploadImageToCloudStorageLiveData.observe(viewLifecycleOwner, Observer {resource->

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
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }

                }

                is Resource.Loading->{
                    showDialog()

                }

            }


        })

        viewModel.registerPost.observe(viewLifecycleOwner, Observer {resource->

            when(resource){

                is Resource.Success->{

                    resource.data.let { resultResponse ->
                        hideDialog()
                        if (imageUri != null) {
                            viewModel!!.uploadImageToCloudStorage(activity!!, imageUri!!, Constants.IMAGE)
                            Toast.makeText(activity, "თქვენ წარმატებით ატვირთეთ სელფი", Toast.LENGTH_SHORT).show()
                        }

                        else{
                            Toast.makeText(activity, "თქვენ წარმატებით ატვირთეთ სელფი", Toast.LENGTH_SHORT).show()
                        }

                    }

                }

                is Resource.Error->{
                    resource.message!!.let { message ->

                        hideDialog()

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }

                }

                is Resource.Loading->{
                    showDialog()

                }

            }


        })

        viewModel.profileDetails.observe(viewLifecycleOwner, Observer{resource->

            when(resource){

                is Resource.Success->{

                    resource.data?.let {resultResource->

                        hideDialog()

                       user = resultResource

                        uploaderSelfieName = user.selfieName
                        add_name.text = uploaderSelfieName

                        uploaderImage = user.image

                        Glide.with(this)
                            .load(uploaderImage)
                            .into(add_profile_image)


                    }

                }


                is Resource.Error->{

                    resource.message!!.let { message ->
                        hideDialog()
                        Toast.makeText(activity,message, Toast.LENGTH_SHORT).show()

                    }
                }

                is Resource.Loading->{

                    showDialog()

                }
            }


        })



    }

    private fun showDialog(){

        dialog = Dialog(activity!!)
        dialog!!.setCancelable(false)

        dialog!!.setContentView(R.layout.dialog)
        dialog!!.show()


    }

    private fun hideDialog(){

        if(dialog!=null){

            dialog!!.dismiss()
        }


    }

    fun imageUploadSuccess(imageUri:String){

        imageUrl = imageUri

    }

    private fun gallery(){

        Dexter.withActivity(activity)
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

        const val GALLERY = 2
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY && data!!.data!=null){

            imageUri = data!!.data!!

            Glide
                .with(this@AddFragment)
                .load(imageUri)
                ?.into(add_image)

        }

    }

    private fun showRationaleDialogForPermissions(){

        AlertDialog.Builder(activity).
        setMessage("turn off")
            .setPositiveButton("GO TO SETTINGS"){
                    _,_ ->
                try {

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",requireContext().packageName,null)
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