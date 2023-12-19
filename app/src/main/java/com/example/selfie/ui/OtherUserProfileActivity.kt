package com.example.selfie.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.adapters.PostAdapter
import com.example.selfie.adapters.PostAdapterForActivity
import com.example.selfie.db.LoveDb
import com.example.selfie.db.Posts
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.utils.Constants
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.example.selfie.viewModel.ViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_other_user_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.home_item.*
import kotlinx.android.synthetic.main.home_item.view.*

class OtherUserProfileActivity : BaseActivity() {


    private var users:Users = Users()
    lateinit var viewModel: SelfieViewModel
    lateinit var repository: Repository
    private var items:ArrayList<Posts> = ArrayList()
    private lateinit var postAdapterForActivity: PostAdapterForActivity
    val TAG = "Error"




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)

        repository = Repository(LoveDb(this))
        val viewModelProvideFactory = ViewModelProviderFactory(repository, this)
        viewModel =
            ViewModelProvider(this, viewModelProvideFactory).get(SelfieViewModel::class.java)

        toolbar()




        if(intent.hasExtra(Constants.USER_PROFILE)){

            users = intent.getSerializableExtra(Constants.USER_PROFILE) as Users

            other_user_profile_selfie_name.text = users.selfieName
            Glide
                .with(this)
                .load(users.image)
                .into(other_user_profile_image)
            other_user_profile_followers.text = "${users.followers} Followers"
        }


        setUpRecyclerView(items)

        viewModel.getConcreteUserPost(users.id)

        viewModel.getConcreteUsersPost.observe(this, Observer {response->

            when(response){


                is Resource.Success->{

                    response.data!!.let {newsResponse->
                        hideDialog()
                        items.clear()
                        items.addAll(newsResponse.sortedByDescending { it.date })
                        other_user_profile_text.text= "Selfies ${(items.size)}"
                        postAdapterForActivity.notifyDataSetChanged()



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



    private fun toolbar(){

        setSupportActionBar(other_user_toolbar)
        other_user_toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        other_user_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }



    }

    private fun setUpRecyclerView(items:ArrayList<Posts>){
        postAdapterForActivity = PostAdapterForActivity(items)

        other_user_profile_rv.apply {

            visibility = View.VISIBLE

            adapter = postAdapterForActivity
            layoutManager = LinearLayoutManager(this@OtherUserProfileActivity)


        }

    }



}