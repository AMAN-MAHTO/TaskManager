package com.example.taskmanager.models

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import kotlin.math.log

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

    //     list<int> <-> string
    @TypeConverter
    fun fromStringToListOfInt(value: String):List<Int>{
        return try {
            val type = object : TypeToken<List<Int>>() {}.type
            Gson().fromJson(value, type)
        } catch (e:Exception){
            emptyList()
        }
    }

    @TypeConverter
    fun fromListofIntToString(value: List<Int>):String{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromProgressTypeToString(value: HabitProgress.ProgressType):String{
        return value.toString()
    }

    @TypeConverter
    fun fromStringToProgressType(value: String): HabitProgress.ProgressType{
        return when(value){
            "YES_OR_NO" -> HabitProgress.ProgressType.YES_OR_NO
            "TARGET_NUMBER" -> HabitProgress.ProgressType.TARGET_NUMBER
            else -> {HabitProgress.ProgressType.TARGET_NUMBER}
        }
    }




}