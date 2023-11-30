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
    fun stringToList(value: String): List<Long> {
        return try {
            val listType = object : TypeToken<List<Long>>() {}.type
            Gson().fromJson(value, listType)
        } catch (e: Exception) {
            emptyList() // Handle conversion error by returning an empty list or another default value
        }
    }

    @TypeConverter
    fun fromListToString(list: List<Long>): String {
        return Gson().toJson(list)
    }




}