package com.example.selfie.db

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.selfie.models.Users
import java.util.Date


@Entity(tableName = "posts-table")
data class Posts(

    var uploaderId:String= "",
    var uploaderSelfieName:String= "",
    var uploaderImage:String= "",
    val text:String= "",
    val image:String= "",
    val commentClick:String = "",
    var likeCount:Int = 0,
    val date:Date = Date(),
    var id:String= "",
    val likedBy: List<String> = emptyList(),
    val savedBy:List<String> = emptyList(),


    @PrimaryKey(autoGenerate = true)
    val autoId:Long = 0,


    ):java.io.Serializable
