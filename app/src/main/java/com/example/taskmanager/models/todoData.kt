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
data class todoData(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val startDate:LocalDate,
    val endDate: LocalDate,
    val weekDay:List<Int>,
    val title: String,
    val description: String,
    val isDone: Boolean = false

)



@Dao
interface todoDataDAO {

    @Insert
    suspend fun insertTodoData(todoData: todoData)

    @Delete
    suspend fun deleteTodoData(todoData: todoData)

    @Update
    suspend fun updateTodoData(todoData: todoData)

    @Query("SELECT * FROM todoData WHERE :date BETWEEN startDate AND endDate")
    fun getTodoByDate(date: LocalDate): LiveData<List<todoData>>

    @Query("UPDATE todoData SET isDone = :isDone WHERE id = :id")
    fun updateIsDone(id:Long, isDone:Boolean)

    @Query("DELETE FROM todoData WHERE id = :id ")
    fun deleteTodoBytaskId(id:Long)

}