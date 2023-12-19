package com.example.selfie.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfie.R
import com.example.selfie.adapters.PostAdapter
import com.example.selfie.db.LoveDb
import com.example.selfie.db.Posts
import com.example.selfie.repository.Repository
import com.example.selfie.ui.MainActivity
import com.example.selfie.viewModel.SelfieViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_love.*
import kotlinx.android.synthetic.main.home_item.*

class LoveFragment:Fragment(R.layout.fragment_love) {
    private lateinit var viewModel: SelfieViewModel
    private var items:ArrayList<Posts> = ArrayList()
    private lateinit var postAdapter: PostAdapter
    val TAG = "Error"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        setUpRecyclerView(items)

        viewModel.getAllPosts().observe(viewLifecycleOwner, Observer {posts->

            items.clear()
            items.addAll(posts)
            postAdapter.notifyDataSetChanged()

        })



      delete()

    }
     fun delete() {
        val itemTouch = object : ItemTouchHelper.SimpleCallback(

            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = items[position]
                alertDialog(item)


            }


        }

        ItemTouchHelper(itemTouch).apply {

            attachToRecyclerView(love_rv)
        }
    }

    fun alertDialog(posts: Posts){

        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("წაშლა")
        dialog.setMessage("დარწმუნებილი ხართ?")

        dialog.setPositiveButton("დიახ"){dialogInterface,_->

            viewModel.deletePost(posts)
            dialogInterface.dismiss()
            Toast.makeText(activity,"პოსტი წაშლილია მოწონების სიიდან.", Toast.LENGTH_LONG)
                .show()

        }


        dialog.setNegativeButton("არა"){dialogInterface,_->

            dialogInterface.dismiss()
            viewModel.lovePost(posts)

        }

        val alertDialog = dialog.create()
        alertDialog.setCancelable(false)
        alertDialog.show()


    }

    private fun setUpRecyclerView(items:ArrayList<Posts>){

        postAdapter = PostAdapter(items,this)

        love_rv.apply {

            adapter = postAdapter
            layoutManager = LinearLayoutManager(activity)
        }




    }


}