package com.example.taskmanager.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface todoDataDAO {

    @Insert
    suspend fun insertTodoData(todoData: todoData)

    @Delete
    suspend fun deleteTodoData(todoData: todoData)

    @Update
    suspend fun updateTodoData(todoData: todoData)



}