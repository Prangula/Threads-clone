package com.example.selfie.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.db.LoveDb
import com.example.selfie.db.Posts
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.ui.MainActivity
import com.example.selfie.ui.OtherUserProfileActivity
import com.example.selfie.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_other_user_profile.view.*
import kotlinx.android.synthetic.main.home_item.view.*
import kotlinx.android.synthetic.main.search_item.view.*
import java.lang.Integer.max
import java.lang.Integer.min

class SearchAdapter(private val items:ArrayList<Users>)
    :RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

        inner class ViewHolder(itemView:View)
            :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_item,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemView.apply {

            search_item_name.text = item.name + item.lastname
            search_item_selfie_name.text = item.selfieName
            Glide
                .with(context)
                .load(item.image)
                .into(search_item_image)



            val repository = Repository(LoveDb(context))

            search_item_image.setOnClickListener {

                if(item.id!=repository.getCurrentUserId()){
                    val intent = Intent(context, OtherUserProfileActivity::class.java)
                    intent.putExtra(Constants.USER_PROFILE,item)
                    context.startActivity(intent)
                }
            }

            val viewModel = (context as MainActivity).viewModel

            var isFollower = item.followersList.contains(repository.getCurrentUserId())
            var isFollowing = item.followingList.contains(repository.getCurrentUserId())


            search_item_profile_follow.setOnClickListener {

                val followersList = item.followersList.toMutableList()
                val followingList = item.followingList.toMutableList()

                if (isFollower) {

                    // Unfollow logic
                    followersList.remove(repository.getCurrentUserId())
                    followingList.remove(item.id)

                    // Update Firestore first
                    viewModel.updateFollowers(item.id, "${item.followers.toInt() - 1}", followersList)

                    // Update Firestore for removing from following list
                    viewModel.removeFollowing(repository.getCurrentUserId(), "${item.following.toInt() - 1}", listOf(item.id))

                    // Update UI
                    item.followers = "${item.followers.toInt() - 1}"
                    item.following = "${item.following.toInt() - 1}"
                    search_item_profile_follow.setBackgroundDrawable(
                        ContextCompat.getDrawable(context, R.drawable.button1))
                    search_item_profile_follow.setTextColor(ContextCompat.getColor(context, R.color.black))
                    search_item_profile_follow.setText("Follow")


                } else {

                    // Follow logic
                    followersList.add(repository.getCurrentUserId())
                    followingList.add(item.id)

                    // Update Firestore first
                    viewModel.updateFollowers(item.id, "${item.followers.toInt() + 1}", followersList)

                    // Update Firestore for adding to following list
                    viewModel.updateFollowing(repository.getCurrentUserId(), "${item.following.toInt() + 1}", listOf(item.id))

                    // Update UI
                    item.followers = "${item.followers.toInt() + 1}"
                    item.following = "${item.following.toInt() + 1}"
                    search_item_profile_follow.setBackgroundDrawable(
                        ContextCompat.getDrawable(context, R.drawable.button))
                    search_item_profile_follow.setTextColor(ContextCompat.getColor(context, R.color.white))
                    search_item_profile_follow.setText("Unfollow")

                }


                // Toggle isFollower and isFollowing
                isFollower = !isFollower
                isFollowing = !isFollowing
            }

            if (isFollower) {

                search_item_profile_follow.setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.button))
                search_item_profile_follow.setTextColor(ContextCompat.getColor(context, R.color.white))
                search_item_profile_follow.setText("Unfollow")

            } else {


                search_item_profile_follow.setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.button1))
                search_item_profile_follow.setTextColor(ContextCompat.getColor(context, R.color.black))
                search_item_profile_follow.setText("Follow")



            }


        }



    }



}