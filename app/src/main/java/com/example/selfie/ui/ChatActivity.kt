package com.example.selfie.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.Normalizer.NO
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.adapters.ChatAdapter
import com.example.selfie.adapters.SearchAdapter
import com.example.selfie.db.LoveDb
import com.example.selfie.fcm.MyFirebaseMessagingService
import com.example.selfie.fcm.PushNotification
import com.example.selfie.fcm.RetrofitInstance
import com.example.selfie.models.Chat
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.utils.Constants
import com.example.selfie.utils.Resource
import com.example.selfie.viewModel.SelfieViewModel
import com.example.selfie.viewModel.ViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList



class ChatActivity : BaseActivity() {

    private lateinit var viewModel: SelfieViewModel
    private lateinit var repository: Repository
    private var receiverId = ""
    private var user: Users = Users()
    private var items: ArrayList<Chat> = ArrayList()
    private lateinit var chatAdapter: ChatAdapter
    val TAG = "Error"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)




        repository = Repository(LoveDb(this))
        val viewModelProvideFactory = ViewModelProviderFactory(repository, this)
        viewModel =
            ViewModelProvider(this, viewModelProvideFactory).get(SelfieViewModel::class.java)

        toolbar()
        setUpRecyclerView(items)

        if (intent.hasExtra("detail")) {

            user = intent.getSerializableExtra("detail") as Users

        }
        receiverId = user.id

        chat_activity_selfie_name.text = user.selfieName
        Glide
            .with(this)
            .load(user.image)
            .into(chat_activity_profile_image)



        FirebaseMessaging.getInstance().subscribeToTopic(receiverId)


        chat_activity_send.setOnClickListener {

            val message = chat_activity_text.text.toString()
            var lastMessage = message.last().toString()


            viewModel.registerChat(
                message,
                user.selfieName,
                Date(),
                receiverId,
                receiverId,
                lastMessage,
            )


            chat_activity_text.text.clear()
            val imm =
                chat_activity_text.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(chat_activity_text.windowToken, 0)

            val sharedPreferences = this.getSharedPreferences("message", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("lastMessage_${user.id}", message)
            editor.apply()

               val pushNotification =  PushNotification(
                    Chat(message,"Message from ${user.selfieName}"),
                receiverId,)

            sendNotification(pushNotification)




        }



        viewModel.getChatList(receiverId)



        viewModel.registerChat.observe(this, Observer { resource ->

            when (resource) {

                is Resource.Success -> {

                    resource.data.let { resultResponse ->

                    }

                }

                is Resource.Error -> {
                    resource.message!!.let { message ->


                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }

                }

                is Resource.Loading -> {

                }

            }


        })


        viewModel.getChatList.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { chatList ->
                        items.clear()
                        items.addAll(chatList)
                        setUpRecyclerView(items)
                        chatAdapter.notifyDataSetChanged()

                    }
                }
                is Resource.Error -> {
                    response.message?.let { errorMessage ->
                        Log.e(TAG, "Error: $errorMessage")
                    }
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
        })




    }



    private fun toolbar() {

        setSupportActionBar(chat_toolbar_top)
        chat_toolbar_top.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        chat_toolbar_top.setNavigationOnClickListener {

            onBackPressed()
        }


    }

    private fun setUpRecyclerView(items: ArrayList<Chat>) {
        val sortedChat = items.sortedBy { it.date }
        chatAdapter = ChatAdapter(ArrayList(sortedChat), repository)

        chat_activity_rv.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }

        if (items.size > 1) {
            chat_activity_rv.smoothScrollToPosition(items.size - 1)
        }
    }




    private fun sendNotification(notification: PushNotification) =
        lifecycleScope.launch(Dispatchers.IO) {
            RetrofitInstance.api.postNotification(notification)

        }

    override fun onResume() {
        super.onResume()
        Constants.currentActivity = this::class.java.name
    }

    override fun onPause() {
        super.onPause()
        Constants.currentActivity = null
    }


}









