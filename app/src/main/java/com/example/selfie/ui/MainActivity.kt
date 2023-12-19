package com.example.selfie.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.selfie.R
import com.example.selfie.db.LoveDb
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.utils.Constants
import com.example.selfie.viewModel.SelfieViewModel
import com.example.selfie.viewModel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SelfieViewModel
    lateinit var repository: Repository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottom_nav.setupWithNavController(navController)


        repository = Repository(LoveDb(this))
        val viewModelProvideFactory = ViewModelProviderFactory(repository, this)
        viewModel =
            ViewModelProvider(this, viewModelProvideFactory).get(SelfieViewModel::class.java)






    }
}
