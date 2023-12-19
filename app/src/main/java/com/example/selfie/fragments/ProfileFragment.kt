package com.example.selfie.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.adapters.PostAdapter
import com.example.selfie.db.Posts
import com.example.selfie.models.Users
import com.example.selfie.ui.LogInActivity
import com.example.selfie.ui.MainActivity
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment:Fragment(R.layout.fragment_profile) {

    private lateinit var viewModel: SelfieViewModel
    private  var dialog: Dialog?=null
    private var items:ArrayList<Posts> = ArrayList()
    private lateinit var postAdapter: PostAdapter
    val TAG = "Error"
    val users:Users = Users()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = (activity as MainActivity).viewModel

        setUpRecyclerView(items)


        viewModel.profileDetails()
        viewModel.getMyPost()






        profile_log_out.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity,LogInActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }

        viewModel.getMyPost.observe(viewLifecycleOwner, Observer { response->

            when(response){


                is Resource.Success->{

                    response.data!!.let {newsResponse->
                        hideDialog()
                        items.clear()
                        items.addAll(newsResponse.sortedByDescending { it.date })
                        profile_text.text = "Selfies ${(items.size)}"
                        postAdapter.notifyDataSetChanged()


                    }



                }

                is Resource.Error->{
                    hideDialog()
                    response.message!!.let {message->

                        Log.e(TAG,"Error, $message")

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
                        profile_name.text = resultResource.name + resultResource.lastname
                        profile_selfie_name.text = resultResource.selfieName
                        Glide.with(this)
                            .load(resultResource.image)
                            .into(profile_image)
                        profile_followers.text = resultResource.followers.toString() + " Followers"



                    }

                }


                is Resource.Error->{

                    resource.message!!.let { message ->

                        Toast.makeText(activity,message, Toast.LENGTH_SHORT).show()

                    }
                }

                is Resource.Loading->{



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

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    private fun setUpRecyclerView(items:ArrayList<Posts>){
        postAdapter = PostAdapter(items,this)

            profile_rv.apply {

                visibility = View.VISIBLE

                adapter = postAdapter
                layoutManager = LinearLayoutManager(activity)


            }

    }


}