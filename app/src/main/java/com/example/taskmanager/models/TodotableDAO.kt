package com.example.taskmanager.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import java.time.LocalDate


@Dao
interface TodotableDAO{

    @Insert
    suspend fun insert(todo: Todotable)

    @Delete
    suspend fun delete(todo: Todotable)

    @Update
    suspend fun update(todo: Todotable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTodos(todotables:List<Todotable>)

    @Query("SELECT * FROM Todotable WHERE date = :date")
    fun getTodoByDate(date: LocalDate):Todotable

    @Query("SELECT * FROM Todotable WHERE date in (:dates) ")
    suspend fun getTodosByDates(dates: List<LocalDate>):List<Todotable>



    @Transaction
    suspend fun insertTaskIdIntoTodoListByDate(date: LocalDate,taskId: Long){
        val todo = getTodoByDate(date)
        if (todo == null){
            val newTodo = Todotable(date, listOf(taskId))
            insert(newTodo)
        }else {
            val updateTaskIds = todo.taskIds.toMutableList()
            updateTaskIds.add(taskId)
            todo.taskIds = updateTaskIds
            update(todo)
        }
    }


}