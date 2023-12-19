package com.example.selfie.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.selfie.R
import com.example.selfie.models.Chat
import com.example.selfie.models.Users
import com.example.selfie.ui.ChatActivity
import kotlinx.android.synthetic.main.followers_item.view.*

class FollowersAdapter(private val items:ArrayList<Users>,private val chatMessages: ArrayList<Chat>)
    :RecyclerView.Adapter<FollowersAdapter.ViewHolder>(){


        inner class ViewHolder(itemView: View)
            :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.followers_item,parent,false))
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        holder.itemView.apply {

            followers_item_name.text = item.name + item.lastname
            followers_item_selfie_name.text = item.selfieName
            Glide
                .with(this)
                .load(item.image)
                .into(followers_item_image)


            val sharedPreferences = context.getSharedPreferences("message", Context.MODE_PRIVATE)
            val lastMessage = sharedPreferences.getString("lastMessage_${item.id}", "")
            followers_item_last_message.text = lastMessage


        }

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context,ChatActivity::class.java)
            intent.putExtra("detail",item)
            holder.itemView.context.startActivity(intent)


        }

    }
}