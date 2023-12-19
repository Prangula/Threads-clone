package com.example.selfie.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selfie.R
import com.example.selfie.adapters.FollowersAdapter
import com.example.selfie.db.LoveDb
import com.example.selfie.models.Chat
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.example.selfie.viewModel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_following_list.*


class FollowingListActivity : BaseActivity() {
    private var users: Users = Users()
    lateinit var viewModel: SelfieViewModel
    lateinit var repository: Repository
    private var items:ArrayList<Users> = ArrayList()
    private lateinit var followersAdapter: FollowersAdapter
    private var chat:ArrayList<Chat> = ArrayList()
    val TAG = "Error"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)

        repository = Repository(LoveDb(this))
        val viewModelProvideFactory = ViewModelProviderFactory(repository, this)
        viewModel =
            ViewModelProvider(this, viewModelProvideFactory).get(SelfieViewModel::class.java)

        setUpRecyclerView(items)

        toolbar()





        viewModel.getCurrentUserFollowing(repository.getCurrentUserId())


        viewModel.getCurrentUserFollowing.observe(this, Observer{

                response->

            when(response){

                is Resource.Success->{

                    response.data!!.let {newsResponse->
                        items.clear()
                        items.addAll(newsResponse)
                        followersAdapter.notifyDataSetChanged()

                    }
                }

                is Resource.Error->{

                    response.message!!.let {message->


                        Log.e(TAG,"Error, $message")

                    }


                }

                is Resource.Loading->{



                }


            }

        })

    }


    private fun toolbar(){

        setSupportActionBar(toolbar_followers)
        toolbar_followers.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbar_followers.setNavigationOnClickListener {

            onBackPressed()
        }


    }

    private fun setUpRecyclerView(items:ArrayList<Users>){
        followersAdapter = FollowersAdapter(items,chat)

        followers_rv.apply {

            visibility = View.VISIBLE

            adapter = followersAdapter
            layoutManager = LinearLayoutManager(this@FollowingListActivity)


        }


    }

}