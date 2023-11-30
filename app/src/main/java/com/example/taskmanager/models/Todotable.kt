package com.example.taskmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity
data class Todotable(
    @PrimaryKey
    val date: LocalDate,
    var taskIds: List<Long>
)
