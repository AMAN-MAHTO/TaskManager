package com.example.taskmanager.mvvm

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskmanager.models.Converters
import com.example.taskmanager.models.Task
import com.example.taskmanager.models.TaskDatabase
import com.example.taskmanager.models.TaskDataset
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate


class HomeViewModel(context: Context): ViewModel() {

    // initializing database instance

    var taskDatabase = TaskDatabase.getDatabase(context)

    @RequiresApi(Build.VERSION_CODES.O)
    val dataset: Array<TaskDataset> = arrayOf(
        TaskDataset(
            arrayOf(
                Task("Title11","description is here","12:00AM","01:00PM",LocalDate.of(2002,12,3)),
                Task("Title12","description is here","01:00AM","02:00PM",LocalDate.of(2002,12,3)),
                Task("Title13","description is here","02:00AM","03:00PM",LocalDate.of(2002,12,3)),
                Task("Title14","description is here","03:00AM","04:00PM",LocalDate.of(2002,12,3)),
                Task("Title15","description is here","04:00AM","05:00PM",LocalDate.of(2002,12,3))
            ),
            LocalDate.of(2002,12,3)
        ), TaskDataset(
            arrayOf(
                Task("Title0","description is here","12:00AM","01:00PM",LocalDate.of(2002,12,4)),
                Task("Title2","description is here","01:00AM","02:00PM",LocalDate.of(2002,12,4)),
                Task("Title3","description is here","02:00AM","03:00PM",LocalDate.of(2002,12,4)),
                Task("Title4","description is here","03:00AM","04:00PM",LocalDate.of(2002,12,4)),
                Task("Title5","description is here","04:00AM","05:00PM",LocalDate.of(2002,12,4))
            ),
            LocalDate.of(2002,12,4)
        ),
        TaskDataset(
            arrayOf(
                Task("Title6","description is here","12:00AM","01:00PM",LocalDate.of(2002,12,5)),
                Task("Title7","description is here","01:00AM","02:00PM",LocalDate.of(2002,12,5)),
                Task("Title8","description is here","02:00AM","03:00PM", LocalDate.of(2002,12,5)),
                Task("Title9","description is here","03:00AM","04:00PM", LocalDate.of(2002,12,5)),
                Task("Title10","description is here","04:00AM","05:00PM", LocalDate.of(2002,12,5))
            ),
            LocalDate.of(2002,12,5)
        )

    )



    //inserting task in taskDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    fun initializeDataset(){
        var i:TaskDataset
        for (i in dataset){
            for (j in i.TaskList){
                GlobalScope.launch{
                    taskDatabase.taskDoa().insertTask(j)
                }
            }
        }
    }

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


    val allDistinctDates = taskDatabase.taskDoa().getAllDistictDates()

    private var _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate


     @RequiresApi(Build.VERSION_CODES.O)
     fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date

    }




}