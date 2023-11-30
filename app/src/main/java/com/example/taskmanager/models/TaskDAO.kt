package com.example.taskmanager.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import java.time.LocalDate

@Dao
interface TaskDAO {

    @Insert
    suspend fun insertTask(task: Task):Long

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

    @Query("UPDATE Task SET isDone = :isDone WHERE taskId = :taskId")
    fun updateIsDone(taskId:Long,isDone: Boolean)

    @Query("SELECT taskIds FROM Todotable WHERE date = :date")
    fun getTaskIds(date: LocalDate):LiveData<String>


    @Query("SELECT * FROM task WHERE taskId in (:taskIds)")
    fun getTaskListByIds(taskIds: List<Long>):LiveData<List<Task>>


    fun getTaskListByDate(date: LocalDate): LiveData<List<Task>> {
        val taskIds = Converters().stringToList(getTaskIds(date).value.toString())
        Log.d("taskList", "getTaskListByDate: ${taskIds}")
        return getTaskListByIds(taskIds)
    }






}