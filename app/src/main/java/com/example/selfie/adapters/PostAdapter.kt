package com.example.selfie.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.db.LoveDb
import com.example.selfie.db.Posts
import com.example.selfie.fragments.HomeFragment
import com.example.selfie.fragments.LoveFragment
import com.example.selfie.fragments.ProfileFragment
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.ui.MainActivity
import com.example.selfie.ui.OtherUserProfileActivity
import com.example.selfie.utils.Constants
import com.example.selfie.viewModel.SelfieViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.home_item.view.*


class PostAdapter(
    private val items: ArrayList<Posts>,
    private val fragment: Fragment,
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.home_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemView.apply {

            // ამ ფრაგმენტებში არ გვინდა რო ჩანდეს
            if (fragment is LoveFragment || fragment is ProfileFragment) {
                home_like.visibility = View.GONE
                home_star.visibility = View.GONE
                home_comment.visibility = View.GONE
                home_like_count.visibility = View.GONE
            }

            Glide
                .with(holder.itemView)
                .load(item.uploaderImage)
                .into(home_profile_image)

            home_name.text = item.uploaderSelfieName
            home_editText.text = item.text
            home_date.text = getTimeAgo(item.date.time)
            home_like_count.text = item.likeCount.toString() + " likes"

            if (item.image == "null") {
                home_image.visibility = View.GONE
            } else {
                Glide
                    .with(holder.itemView)
                    .load(item.image)
                    .into(home_image)

                home_image.visibility = View.VISIBLE
            }

            val viewModel = (context as MainActivity).viewModel
            val repository = Repository(LoveDb(context))

            var isLiked = item.likedBy.contains(repository.getCurrentUserId()) // თუ შეიცავს ახლანდელი იუზერის აიდის

            home_like.setOnClickListener {
                val likedBy = item.likedBy.toMutableList()

                if (isLiked) {
                    likedBy.remove(repository.getCurrentUserId())
                    home_like.setColorFilter(ContextCompat.getColor(context, R.color.black))
                    item.likeCount--
                } else {
                    likedBy.add(repository.getCurrentUserId())
                    home_like.setColorFilter(ContextCompat.getColor(context, R.color.background))
                    item.likeCount++
                }

                // პირიქით რო გაკეთეს. ანუ თუ დალაიქებულია ანლაიქი და პირიქით.
                isLiked = !isLiked

                viewModel.updateLikes(item.id, item.likeCount, likedBy)
                home_like_count.text = item.likeCount.toString() + " likes"
            }

            var isSaved = item.savedBy.contains(repository.getCurrentUserId())

            home_star.setOnClickListener {
                val savedBy = item.savedBy.toMutableList()

                if (isSaved) {
                    // Unsave logic
                    savedBy.remove(repository.getCurrentUserId())
                    home_star.setColorFilter(ContextCompat.getColor(context, R.color.black))
                } else {
                    // Save logic
                    savedBy.add(repository.getCurrentUserId())
                    home_star.setColorFilter(ContextCompat.getColor(context, R.color.background))
                    viewModel.lovePost(item)
                }

                // Toggle the isSaved flag
                isSaved = !isSaved

                // Update Firestore
                viewModel.updateSaves(item.id, savedBy)
            }


            if (item.likedBy.contains(repository.getCurrentUserId())) {
                home_like.setColorFilter(ContextCompat.getColor(context, R.color.background))
            } else {
                home_like.setColorFilter(ContextCompat.getColor(context, R.color.black))
            }

            if (item.savedBy.contains(repository.getCurrentUserId())) {
                home_star.setColorFilter(ContextCompat.getColor(context, R.color.background))
            } else {
                home_star.setColorFilter(ContextCompat.getColor(context, R.color.black))
            }

        }
    }

    // რამდენი ხნის წინ აიტვირთა პოსტი
    private fun getTimeAgo(previousTime: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - previousTime

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days >= 365 -> "${days / 365} Y"
            days >= 30 -> "${days / 30} M"
            days >= 1 -> "$days D"
            hours >= 1 -> "$hours H"
            minutes >= 1 -> "$minutes M"
            else -> "just now"
        }
    }
}
