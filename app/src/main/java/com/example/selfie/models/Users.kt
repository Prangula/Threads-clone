package com.example.selfie.models

data class Users(

    var id:String = "",
    val name:String = "",
    val lastname:String = "",
    var selfieName:String = "",
    val email:String = "",
    var image:String = "",
    var followers:String = "0",
    var followersList:List<String> = emptyList(),
    var following:String = "0",
    var followingList:List<String> = emptyList(),


):java.io.Serializable
