package com.example.taskmanager.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
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
}