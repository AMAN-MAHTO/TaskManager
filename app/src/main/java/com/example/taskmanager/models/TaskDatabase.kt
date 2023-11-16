package com.example.taskmanager.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDoa(): TaskDAO

    companion object{

        @Volatile
        private var INSTACE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase{

            if(INSTACE == null){
                synchronized(this){
                    INSTACE = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "TaskDB"
                    ).build()

                }
            }
            return INSTACE!!
        }

    }

}