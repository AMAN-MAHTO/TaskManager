package com.example.taskmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity
data class Todo(
    @PrimaryKey
    val date: LocalDate,
    val list: Array<Long>
)
