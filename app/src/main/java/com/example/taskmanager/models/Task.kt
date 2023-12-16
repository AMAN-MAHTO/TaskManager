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
    var date:LocalDate,
    var title: String,
    var description: String,
    var isDone: Boolean = false
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
    @Query("SELECT * FROM Task WHERE id =:taskId")
    fun getTaskById(taskId: Long):LiveData<Task>

}