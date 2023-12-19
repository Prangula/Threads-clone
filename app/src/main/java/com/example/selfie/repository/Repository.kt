package com.example.selfie.repository

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.selfie.db.LoveDb
import com.example.selfie.db.Posts
import com.example.selfie.models.Chat
import com.example.selfie.models.Users
import com.example.selfie.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.coroutineContext


class Repository(val db:LoveDb) {

  private val firestore = FirebaseFirestore.getInstance()

  // იუზერის რეგისტრაცია

    suspend fun registerUser(user: Users,context: Context) {
        val snapshot = firestore.collection(Constants.USERS)
            .whereEqualTo("selfieName", user.selfieName)
            .get().await()

        if (snapshot.isEmpty) {
            firestore.collection(Constants.USERS)
                .document(user.id)
                .set(user, SetOptions.merge()).await()
        }
        else {
            Toast.makeText(context,"Selfie name ${user.selfieName} already exists.",Toast.LENGTH_SHORT)
                .show()
            return
        }


    }


    // ჩათის რეგისტრაცია
    suspend fun registerChat(chat: Chat) {
        chat.id = UUID.randomUUID().toString()
        chat.senderId = getCurrentUserId()
        firestore.collection(Constants.CHAT)
            .document(chat.id)
            .set(chat, SetOptions.merge()).await()
    }


    // ჩათი
    suspend fun getChatList(id: String, onChatListUpdate: (List<Chat>) -> Unit) {
        firestore.collection(Constants.CHAT)
            .whereIn("senderId", listOf(getCurrentUserId(), id))
            .whereIn("receiverId", listOf(getCurrentUserId(), id))
            .orderBy("date")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle the error
                    Log.e("Firestore", "Error fetching chat list: ${error.message}")
                    return@addSnapshotListener // რეალურ დროში რო მოხდეს ცვლილება.
                }

                value?.let { snapshot ->
                    val chatList = snapshot.toObjects(Chat::class.java)
                    onChatListUpdate(chatList)
                }
            }
    }





    // ახლანდელი იუსერის ფოლოვერების სია
    suspend fun getUsersInFollowingList(userIds: List<String>): List<Users> =
        firestore.collection(Constants.USERS)
            .whereIn("id", userIds)
            .get().await()
            .toObjects(Users::class.java)


    // ახლანდელი იუსერის ფოლოვერების მიღება, რომ მერე მივწეროთ.
    suspend fun getCurrentUserFollowing(currentUserId: String): List<String> {
        val userDocument = firestore.collection(Constants.USERS)
            .document(currentUserId)
            .get().await()
        return userDocument.toObject(Users::class.java)?.followingList ?: emptyList()
    }








    // იუზერის შესვლა

    suspend fun loginUser() =

        firestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get().await().toObject(Users::class.java)


// პროფაილისთვის მივიღოთ იუზერის მონაცემები
    suspend fun getUsers() =
        firestore.collection(Constants.USERS)
            .get().await().toObjects(Users::class.java)

    // პოსტის რეგისტრაცია
    suspend fun registerPost(posts: Posts) {

        val postId = UUID.randomUUID().toString()
        posts.id = postId

        firestore.collection(Constants.POST)
            .document(posts.id)
            .set(posts, SetOptions.merge()).await()
    }

    // პოსტის მიღება

    suspend fun getRegisterPost() =

        firestore.collection(Constants.POST)
            .get().await().toObjects(Posts::class.java)

// ჩემი პოსტის მიღება
    suspend fun getMyPost() =

        firestore.collection(Constants.POST)
            .whereEqualTo(Constants.UPLOADER_ID,getCurrentUserId())
            .get().await().toObjects(Posts::class.java)


    // სხვა იუსერების პოსტები
    suspend fun getConcreteUsersPost(uploaderId:String)=

        firestore.collection(Constants.POST)
            .whereEqualTo(Constants.UPLOADER_ID,uploaderId)
            .get().await().toObjects(Posts::class.java)



    // სეივის აფდეითი
    suspend fun updateSaves(id:String,hashMap: HashMap<String,Any>) {

        firestore.collection(Constants.POST)
            .document(id)
            .update(hashMap).await()

    }


    suspend fun updateLikes(id:String,hashMap: HashMap<String, Any>){

        firestore.collection(Constants.POST)
            .document(id)
            .update(hashMap).await()

    }

    suspend fun updateFollowers(id:String,hashMap: HashMap<String, Any>){
        firestore.collection(Constants.USERS)
            .document(id)
            .update(hashMap).await()
    }

    suspend fun updateFollowing(id: String, following: String, newFollowingList: List<String>) {
        val userDocRef = firestore.collection(Constants.USERS).document(id)

        // Update following count
        userDocRef.update("following", following)

        // Update followingList by appending new user IDs
        userDocRef.update("followingList", FieldValue.arrayUnion(*newFollowingList.toTypedArray()))
            .await()
    }

    suspend fun removeFollowing(id: String, following: String, removedFollowingList: List<String>) {
        val userDocRef = firestore.collection(Constants.USERS).document(id)

        // Update following count
        userDocRef.update("following", following)

        // Update followingList by removing user IDs
        userDocRef.update("followingList", FieldValue.arrayRemove(*removedFollowingList.toTypedArray()))
            .await()
    }











    // აქტიური იუზერი
fun getCurrentUserId():String{

    val currentUser = FirebaseAuth.getInstance().currentUser

    var currentUserId = ""

    if(currentUser!=null){

      currentUserId = currentUser.uid
    }

    return currentUserId


  }

// ატვირთვა სტორიჯზე
suspend fun uploadImageToCloudStorage(activity: Activity, imageUri: Uri, imageType: String): Uri {
    return try {
        val taskSnapshot = FirebaseStorage.getInstance().reference.child(
            "$imageType${System.currentTimeMillis()}.${Constants.getFileExtension(activity, imageUri)}"
        ).putFile(imageUri).await()

        taskSnapshot.metadata?.reference?.downloadUrl?.await() ?: throw Exception("Download URL is null")
    } catch (e: Exception) {
        throw Exception("Error uploading image to cloud storage: ${e.message}")
    }
}




    suspend fun upsert(posts: Posts) = db.getLoveDao().upsert(posts)

    suspend fun delete(posts: Posts) = db.getLoveDao().delete(posts)

    fun getAllPosts() = db.getLoveDao().getAllPosts()




}