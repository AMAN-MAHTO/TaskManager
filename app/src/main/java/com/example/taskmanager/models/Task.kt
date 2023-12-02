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
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id:Long =0,
    val date:LocalDate,
    val title: String,
    val description: String,
    val isDone: Boolean = false
)

@Dao
interface TaskDAO{
    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM Task WHERE date =:date")
    suspend fun getTaskByDate(date:LocalDate):List<Task>

    @Query("UPDATE Task SET isDone =:value WHERE id =:id")
    fun updateIsDone(id:Long,value: Boolean)

    @Query("DELETE FROM Task WHERE id =:id")
    fun deleteTaskById(id: Long)

}