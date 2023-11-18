package com.example.taskmanager.mvvm

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
class DatesViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val _today: LocalDate = LocalDate.now()


    val centerDate = MutableLiveData<LocalDate>(_today)
    val centerPos =  MutableLiveData(centerDate.value!!.dayOfYear - centerDate.value!!.minusMonths(2).dayOfYear)

    val _dates = MutableLiveData<List<LocalDate>>()

    val month = MutableLiveData<String>(_today.month.toString())
    val year = MutableLiveData<String>(_today.year.toString())

    val selectedDate = MutableLiveData<LocalDate>(_today)



    init{

        getDatesBetween(centerDate.value!!)
        Log.d("adapterDate", "onCreateViewHolder: "+_dates.toString())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDatesBetween(centerDate: LocalDate){
        val startDate = centerDate.minusMonths(3)
        val endDate = centerDate.plusMonths(3)
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        _dates.value = dates
        centerPos.value = selectedDate.value!!.dayOfYear - startDate.dayOfYear
    }
}