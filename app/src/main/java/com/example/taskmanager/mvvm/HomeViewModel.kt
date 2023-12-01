package com.example.taskmanager.mvvm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.taskmanager.models.MainDatabase

import com.example.taskmanager.models.todoData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(context: Context): ViewModel() {

    // initializing database instance

    var taskDatabase = MainDatabase.getDatabase(context)


    //get dates between returns the, localdate list of all the date in between given date input
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return dates
    }





//    private var _taskListByDate = MutableLiveData<List<Task>>()
//    val taskListByDate:LiveData<List<Task>> = _taskListByDate




    private var _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate


     @RequiresApi(Build.VERSION_CODES.O)
     fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date

    }






}