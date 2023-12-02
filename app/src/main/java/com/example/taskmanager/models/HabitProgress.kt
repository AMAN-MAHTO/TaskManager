package com.example.taskmanager.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.Update
import java.time.LocalDate

@Entity
data class HabitProgress(
    @PrimaryKey(autoGenerate = true)
    val id:Long =0,
    val habitId:Long,
    val date: LocalDate,
    val progressType: ProgressType,
    val isDone: Boolean = false,
    val targetNumber: Int? = null,
    val elapsedTimeInSeconds: Long? = null

    ){
    enum class ProgressType{
        YES_OR_NO,  // Simple yes or no progress
        TARGET_NUMBER, // progress based on reaching a target number
        TIMER_BASED // progress based on time
    }
}


@Dao
interface HabitProgressDAO{
    @Insert
    suspend fun insert(habitProgress: HabitProgress)

    @Delete
    suspend fun delete(habitProgress: HabitProgress)

    @Update
    suspend fun update(habitProgress: HabitProgress)
}
