package com.example.selfie.db

import android.content.Context
import androidx.room.*


@Database(
    entities = [Posts::class],
    version = 2

)
// გადაყვანამ რო იმუშაოს
@TypeConverters(Converter::class)
abstract class LoveDb:RoomDatabase() {


    abstract fun getLoveDao():LoveDao

    companion object{

        @Volatile
        private var instance:LoveDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){

            instance ?: createDatabase(context).also { instance=it }

        }

        private fun createDatabase(context: Context)=

            Room.databaseBuilder(
                context.applicationContext,
                LoveDb::class.java,
                "love_db.db"
            ).build()



    }


}