package com.example.taskmanager.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
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
    val currentNumber: Int? = null,
    val elapsedTimeInSeconds: Long? = null

    ){
    enum class ProgressType{
        YES_OR_NO,  // Simple yes or no progress
        TARGET_NUMBER, // progress based on reaching a target number
//        TIMER_BASED // progress based on time
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

    @Query("SELECT * FROM HabitProgress WHERE date =:date")
    suspend fun getProgressByDate(date: LocalDate): List<HabitProgress>

    @Query("SELECT * FROM HabitProgress WHERE habitId =:habitId")
    suspend fun getProgressByHabitId(habitId: Long): List<HabitProgress>

    @Query("UPDATE HabitProgress SET currentNumber =:value WHERE id =:progressId")
    suspend fun updateNumber(value:Int,progressId:Long)

    @Query("UPDATE HabitProgress SET isDone =:value WHERE id =:progressId")
    suspend fun updateIsDone(value:Boolean,progressId:Long)

    @Query("SELECT COUNT(*) FROM HabitProgress WHERE habitId =:habitId AND isDone = 1")
    fun getIsDoneCount(habitId:Long):Int
}
