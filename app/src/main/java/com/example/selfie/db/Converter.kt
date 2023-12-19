package com.example.selfie.db

import androidx.room.TypeConverter
import com.example.selfie.models.Users
import java.util.Date

class Converter {

    @TypeConverter
    fun fromDate(date: Date): String {
        return date.toString()
    }

    @TypeConverter
    fun toDate(dateString: String): Date {
        return Date(dateString)
    }

    @TypeConverter
    @JvmName("fromLikedByToString")
    fun fromLikedBy(likedBy: List<String>): String {
        return likedBy.joinToString(separator = ",")
    }

    @TypeConverter
    @JvmName("toLikedByFromString")
    fun toLikedBy(likedByString: String): List<String> {
        return likedByString.split(",").map { it.trim() }
    }







}
