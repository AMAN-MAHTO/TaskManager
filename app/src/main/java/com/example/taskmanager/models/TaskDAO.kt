package com.example.taskmanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface TaskDAO {

    @Insert
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM task WHERE date = :localDate")
    fun getTasksByDate(localDate: LocalDate): LiveData<List<Task>>

    @Query("SELECT DISTINCT date FROM task")
    fun getAllDistictDates(): LiveData<List<LocalDate>>

    @Query("DELETE FROM task WHERE taskId = :taskId")
    fun deleteTaskBytaskId(taskId: Long)

}