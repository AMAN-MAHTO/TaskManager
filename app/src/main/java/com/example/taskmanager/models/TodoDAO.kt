package com.example.taskmanager.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update


@Dao
interface TodoDAO{

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)


}