package com.example.taskmanager.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class Converters {

    @TypeConverter
    @RequiresApi(Build.VERSION_CODES.O)
    fun localDateToLong(value: LocalDate): Long{
        return value.toEpochDay()
    }

    @TypeConverter
    @RequiresApi(Build.VERSION_CODES.O)
    fun longToLocalDate(value: Long): LocalDate{
        return LocalDate.ofEpochDay(value)
    }

    @TypeConverter
    fun stringToArray(value: String): Array<Long> {
        val listType = object : TypeToken<ArrayList<Long>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: Array<Long>): String {
        return Gson().toJson(list)
    }




}