package com.example.selfie.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.selfie.R
import com.example.selfie.db.LoveDb
import com.example.selfie.models.Chat
import com.example.selfie.repository.Repository
import kotlinx.android.synthetic.main.chat_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private val items:ArrayList<Chat>,   private val repository: Repository)
    : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){


        inner class ViewHolder(itemView: View)
            :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_item,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]



        holder.itemView.apply {

            chat_text.text = item.message
            chat_date.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(item.date)


            val currentUser = repository.getCurrentUserId() // ახლანდელი იუზერი
            val layoutParams = chat_card.layoutParams as LinearLayout.LayoutParams

            // გამგზავნი
            if(item.senderId == currentUser){

                chat_card.setCardBackgroundColor(ContextCompat.getColor(
                    context,R.color.background
                ))

                chat_text.setTextColor(ContextCompat.getColor(
                    context,R.color.white
                ))
                layoutParams.gravity = Gravity.END

            }

            // მიმღები
            else{

                chat_card.setCardBackgroundColor(ContextCompat.getColor(
                    context,R.color.grey
                ))

                chat_text.setTextColor(ContextCompat.getColor(
                    context,R.color.background
                ))
                chat_date.setTextColor(ContextCompat.getColor(
                    context,R.color.background
                ))
                layoutParams.gravity = Gravity.START

            }
            chat_card.layoutParams = layoutParams

        }





    }


}