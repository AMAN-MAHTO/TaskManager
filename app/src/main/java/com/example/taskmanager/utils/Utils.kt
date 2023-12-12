package com.example.taskmanager.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskmanager.models.Habit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class Utils {

    // constants
    val TASK_VIEW_TYPE = 0
    val HABIT_VIEW_TYPE = 1
    val HABIT_TARGATED_VIEW_TYPE = 2


    // local date formater
    @RequiresApi(Build.VERSION_CODES.O)
    val dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)

    @RequiresApi(Build.VERSION_CODES.O)
    fun intToLocalDate(p1: Int, p2: Int, p3: Int): LocalDate {
        var year:String = p1.toString()
        var month: String
        var day: String
        if(p3<10){day = "0$p3"
        }else{day = p3.toString()}
        if(p2<9){month = "0${p2+1}"}else{month = (p2+1).toString()}

        return  LocalDate.parse(day+"/"+month+"/"+year,
            DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ENGLISH))
    }

}