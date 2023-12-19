package com.example.selfie.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfie.db.Posts
import com.example.selfie.models.Chat
import com.example.selfie.models.Users
import com.example.selfie.repository.Repository
import com.example.selfie.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.followers_item.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SelfieViewModel(val repository: Repository,val context:Context):ViewModel() {


    val registerUser: MutableLiveData<Resource<Users>> = MutableLiveData()
    val registerChat: MutableLiveData<Resource<Chat>> = MutableLiveData()
    val uploadImageToCloudStorageLiveData:MutableLiveData<Resource<String>> = MutableLiveData()
    val loginUser:MutableLiveData<Resource<Users>> = MutableLiveData()
    val profileDetails:MutableLiveData<Resource<Users>> = MutableLiveData()
    val getSearchUser:MutableLiveData<Resource<ArrayList<Users>>> = MutableLiveData()
    val getCurrentUserFollowing:MutableLiveData<Resource<ArrayList<Users>>> = MutableLiveData()
    val getChatList:MutableLiveData<Resource<ArrayList<Chat>>> = MutableLiveData()


    val registerPost:MutableLiveData<Resource<Posts>> = MutableLiveData()
    val getRegisterPost:MutableLiveData<Resource<ArrayList<Posts>>> = MutableLiveData()
    val getMyPost:MutableLiveData<Resource<ArrayList<Posts>>> = MutableLiveData()
    val getConcreteUsersPost:MutableLiveData<Resource<ArrayList<Posts>>> = MutableLiveData()





    fun updateFollowers(id:String,followers:String,followersList:List<String>){

        viewModelScope.launch {

            var hashMap = HashMap<String,Any>()
            hashMap["followersList"] = followersList
            hashMap["followers"] = followers

            repository.updateFollowers(id,hashMap)

        }
    }

    fun updateFollowing(id: String, following: String, newFollowingList: List<String>) {
        viewModelScope.launch {
            repository.updateFollowing(id, following, newFollowingList)
        }
    }

    fun removeFollowing(id: String, following: String, removedFollowingList: List<String>) {
        viewModelScope.launch {
            repository.removeFollowing(id, following, removedFollowingList)
        }
    }





    fun updateSaves(id:String,savedBy:List<String>){

        viewModelScope.launch {

            var hashmap = HashMap<String,Any>()

            hashmap["savedBy"] = savedBy

           repository.updateSaves(id,hashmap)


        }
    }

    fun updateLikes(id:String,likeCount:Int,likedBy: List<String>){

        viewModelScope.launch {

            var hashmap = HashMap<String,Any>()

            hashmap["likeCount"] = likeCount
            hashmap["likedBy"] = likedBy

            repository.updateLikes(id,hashmap)

        }

    }

    fun getConcreteUserPost(uploaderId:String){

        viewModelScope.launch {

            getConcreteUsersPost.postValue(Resource.Loading())
            try {
                val posts = repository.getConcreteUsersPost(uploaderId)
                getConcreteUsersPost.postValue(Resource.Success(ArrayList(posts)))

            }catch (e:Exception){
                getConcreteUsersPost.value = Resource.Error("Error fetching register posts: ${e.message}")

            }
        }

    }


    fun getMyPost(){

        viewModelScope.launch {

            getMyPost.postValue(Resource.Loading())
            try {
                val posts = repository.getMyPost()
                getMyPost.postValue(Resource.Success(ArrayList(posts)))

            } catch (e: Exception) {
                getMyPost.value = Resource.Error("Error fetching register posts: ${e.message}")
            }

        }
    }

    fun getRegisterPost(){

        viewModelScope.launch {

            getRegisterPost.postValue(Resource.Loading())

            try {
                val posts = repository.getRegisterPost()
                getRegisterPost.postValue(Resource.Success(ArrayList(posts)))

            } catch (e: Exception) {
                getRegisterPost.value = Resource.Error("Error fetching register posts: ${e.message}")
            }

        }
    }

    fun getCurrentUserFollowing(currentUserId: String) {
        viewModelScope.launch {
            getCurrentUserFollowing.postValue(Resource.Loading())
            try {
                // Fetch the current user's followingList
                val followingList = repository.getCurrentUserFollowing(currentUserId)

                // Fetch the users corresponding to the followingList
                val users = repository.getUsersInFollowingList(followingList)

                // Convert the list to ArrayList
                val usersArrayList = ArrayList(users)

                // Update the LiveData with the result
                getCurrentUserFollowing.postValue(Resource.Success(usersArrayList))
            } catch (e: Exception) {
                getCurrentUserFollowing.value = Resource.Error("Error fetching user list: ${e.message}")
            }
        }
    }




    fun getChatList(id: String) {
        viewModelScope.launch {
            try {
                repository.getChatList(id) { updatedChatList ->
                    // Handle the updated chat list
                    getChatList.postValue(Resource.Success(ArrayList(updatedChatList)))
                }
            } catch (e: Exception) {
                getChatList.postValue(Resource.Error("Error fetching chat list: ${e.message}"))
            }
        }
    }





    fun getSearchUser(query:String){

        viewModelScope.launch {

            getSearchUser.postValue(Resource.Loading())

            try {
                val search = repository.getUsers()
                    .filter { user->
                        user.name.contains(query,ignoreCase = true)
                                || user.lastname.contains(query,ignoreCase = true)
                                || user.selfieName.contains(query,ignoreCase = true)
                    }
                getSearchUser.postValue(Resource.Success(ArrayList(search)))

            }catch (e:Exception){

                getSearchUser.value = Resource.Error("Error fetching register posts: ${e.message}")
            }

        }
    }

    fun profileDetails(){

        viewModelScope.launch {

            profileDetails.postValue(Resource.Loading())

            try {
                val user = repository.loginUser()
                profileDetails.postValue(Resource.Success(user))
            } catch (e: Exception) {
                profileDetails.postValue(Resource.Error(e.message ?: "Error"))
            }
        }
    }


    fun loginUser(email: String,password: String){

        viewModelScope.launch {

            loginUser.postValue(Resource.Loading())
            try {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).await()
                val user = repository.loginUser()
                loginUser.postValue(Resource.Success(user))


            }catch (e:java.lang.Exception){

                loginUser.postValue(Resource.Error(e.message?:"Failed"))

            }


        }

    }

    fun registerPost(uploaderSelfieName:String,uploaderImage:String,text:String,image:String){

        viewModelScope.launch {

            registerPost.postValue(Resource.Loading())

            try {

                val post = Posts(


                    repository.getCurrentUserId(),
                    uploaderSelfieName,
                    uploaderImage,
                    text,
                    image,


                )
                repository.registerPost(post)
                registerPost.postValue(Resource.Success(post))


            }catch (e:java.lang.Exception){
                registerPost.postValue(Resource.Error(e.message?:"Error"))
            }


        }


    }

    fun registerChat(message:String,title:String, date: Date, senderId:String, receiverId:String,lastMessage:String){

        viewModelScope.launch {

            registerChat.postValue(Resource.Loading())

            val chat = Chat(
                message,
                title,
                date,
                senderId,
                receiverId,
                lastMessage
            )

            repository.registerChat(chat)
            registerChat.postValue(Resource.Success(chat))


            try {

            }catch (e:Exception){

                registerChat.postValue(Resource.Error(e.message?:"Error"))

            }


        }

    }




     fun registerUser(name:String, lastname:String, selfieName:String, email:String, image:String, password:String,
     fcmToken:String){

        viewModelScope.launch{

            registerUser.postValue(Resource.Loading())

            try {
                val firebaseUser =  FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).await()
                val user = Users(

                    firebaseUser.user!!.uid,
                    name,
                    lastname,
                    selfieName,
                    email,
                    image,

                )

                repository.registerUser(user,context)
                registerUser.postValue(Resource.Success(user))
            }catch (e:Exception){

                registerUser.postValue(Resource.Error(e.message?:"Error"))

            }



        }
     }



    @SuppressLint("SuspiciousIndentation")
    fun uploadImageToCloudStorage(activity: Activity, imageUri: Uri, imageType: String) {
        viewModelScope.launch {
            uploadImageToCloudStorageLiveData.postValue(Resource.Loading())
            try {
                val uri = repository.uploadImageToCloudStorage(activity, imageUri, imageType)
                uploadImageToCloudStorageLiveData.postValue(Resource.Success(uri.toString()))
            } catch (e: Exception) {
                uploadImageToCloudStorageLiveData.postValue(Resource.Error("Error uploading image: ${e.message}"))
            }
        }
    }


    fun lovePost(posts: Posts) =

        viewModelScope.launch {

            repository.upsert(posts)

        }

    fun deletePost(posts: Posts) =

        viewModelScope.launch {

            repository.delete(posts)
        }

    fun getAllPosts() = repository.getAllPosts()






}