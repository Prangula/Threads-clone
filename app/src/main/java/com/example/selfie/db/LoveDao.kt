package com.example.selfie.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LoveDao {

    // თუ იგივე პოსტია მაგ შეთხვევაში, რო ჩანაცვლდეს

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun upsert(posts: Posts):Long

    // წაშლა პოსტის

    @Delete

    suspend fun delete(posts: Posts)

    // მიღება ყველა პოსტის

    @Query("SELECT * FROM 'posts-table'")

    fun getAllPosts():LiveData<List<Posts>>




}