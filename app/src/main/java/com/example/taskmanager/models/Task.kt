package com.example.taskmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

data class TaskDataset(
    val TaskList: Array<Task>,
    val date: LocalDate
    )

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId:Long=0,
    val title: String,
    val description: String,
    val date: LocalDate,
    val isDone: Boolean = false
)



