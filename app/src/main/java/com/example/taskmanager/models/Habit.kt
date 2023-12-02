package com.example.taskmanager.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val title: String,
    val description: String,
    val rangeType: HabitRangeType,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val weekDays: List<Int>? = null,
    val repeatedInterval: Int? = null,

){
    enum class HabitRangeType{
        CONTINUOUS, // habit form start date to end date ( if specified) or always, repeated every day
        SPECIFIC_WEEKDAYS, // Habit repeted on specific day of week,
        REPEATED_INTERVAL // habit repeated on days after an interval
    }
}


@Dao
interface HabitDAO{
    @Insert
    suspend fun insert(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Query("SELECT * FROM Habit WHERE startDate <= :date")
    suspend fun getHabitByDate(date: LocalDate): List<Habit>

    // this delte should be associated with habit progress
    @Query("DELETE FROM Habit WHERE id =:id")
    fun deleteHabitById(id: Long)
}