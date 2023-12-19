package com.example.selfie.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selfie.R
import com.example.selfie.adapters.PostAdapter
import com.example.selfie.db.Posts
import com.example.selfie.ui.FollowingListActivity
import com.example.selfie.ui.MainActivity
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_item.*

class HomeFragment:Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: SelfieViewModel
    private  var dialog: Dialog?=null
    private var items:ArrayList<Posts> = ArrayList()
    private lateinit var postAdapter: PostAdapter
    val TAG = "Error"
    var posts = Posts()



    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        setUpRecyclerView(items)

        viewModel.getRegisterPost()



        home_chat.setOnClickListener {

            val intent = Intent(activity,FollowingListActivity::class.java)
            startActivity(intent)

        }




        viewModel.getRegisterPost.observe(viewLifecycleOwner, Observer { response->

            when(response){


                is Resource.Success->{

                    response.data!!.let {newsResponse->
                        hideDialog()
                        items.clear()
                        items.addAll(newsResponse.sortedByDescending { it.date })
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

    private fun setUpRecyclerView(items:ArrayList<Posts>){

        postAdapter = PostAdapter(items,this)

        home_rv.apply {

            adapter = postAdapter
            layoutManager = LinearLayoutManager(activity)
        }




    }


}