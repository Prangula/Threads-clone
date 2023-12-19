package com.example.selfie.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selfie.R
import com.example.selfie.adapters.PostAdapter
import com.example.selfie.adapters.SearchAdapter
import com.example.selfie.db.Posts
import com.example.selfie.models.Users
import com.example.selfie.ui.MainActivity
import com.example.selfie.ui.OtherUserProfileActivity
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.fragment_love.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment:Fragment(R.layout.fragment_search) {

    private lateinit var viewModel: SelfieViewModel
    private var items:ArrayList<Users> = ArrayList()
    private  var dialog: Dialog?=null
    private lateinit var searchAdapter: SearchAdapter
    val TAG = "Error"
    val users:Users = Users()
    private var job: Job? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        setUpRecyclerView(items)


        // ჩაწერიდან 1 წამში რო მოხდეს შედეგის გამოტანა

        search_et.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch(Dispatchers.Main) {
                delay(500)
                viewModel.getSearchUser(it.toString())

            }

        }

        viewModel.getSearchUser.observe(viewLifecycleOwner, Observer { response->

            when(response){


                is Resource.Success->{

                    response.data!!.let {newsResponse->
                        hideDialog()
                        items.clear()
                        items.addAll(newsResponse)
                        searchAdapter.notifyDataSetChanged()


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



    private fun setUpRecyclerView(items:ArrayList<Users>){

        searchAdapter = SearchAdapter(items)

        search_rv.apply {

            adapter = searchAdapter
            layoutManager = LinearLayoutManager(activity)
        }




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





}
