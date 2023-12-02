package com.example.taskmanager.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [todoData::class,Task::class,Habit::class,HabitProgress::class], version = 1)
@TypeConverters(Converters::class)
abstract class MainDatabase: RoomDatabase() {



    abstract fun todoDataDoa():todoDataDAO
    abstract fun taskDoa():TaskDAO
    abstract fun habitDoa():HabitDAO
    abstract fun habitProgressDoa():HabitProgressDAO

    companion object{

        @Volatile
        private var INSTACE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase{

            if(INSTACE == null){
                synchronized(this){
                    INSTACE = Room.databaseBuilder(
                        context.applicationContext,
                        MainDatabase::class.java,
                        "TaskDB"
                    ).build()

                }
            }
            return INSTACE!!
        }

    }

}