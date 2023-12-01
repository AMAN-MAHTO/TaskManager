package com.example.taskmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class todoData(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val startDate:LocalDate,
    val endDate: LocalDate,
    val weekDay:List<Int>,
    val title: String,
    val description: String,
    val isDone: Int = 0

)
