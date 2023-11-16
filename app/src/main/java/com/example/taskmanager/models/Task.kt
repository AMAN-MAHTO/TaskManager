package com.example.taskmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

data class TaskDataset(
    val TaskList: Array<Task>,
    val date: LocalDate
    )

@Entity(primaryKeys = ["date","startTime"] )
data class Task(
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val date: LocalDate
)


