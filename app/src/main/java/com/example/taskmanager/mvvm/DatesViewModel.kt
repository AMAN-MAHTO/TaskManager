package com.example.taskmanager.mvvm

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.models.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
class DatesViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val _today: LocalDate = LocalDate.now()

    val _calendar = Calendar.getInstance()

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


    @RequiresApi(Build.VERSION_CODES.O)
    fun generateDates(habit: Habit): LiveData<List<LocalDate>> {
        var datesLiveData = MutableLiveData<List<LocalDate>>()

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val _today: LocalDate = LocalDate.now()
                val dates = mutableListOf<LocalDate>()
                var date:LocalDate = habit.startDate
                var endDate = _today
                if(habit.endDate != null){
                    if(habit.endDate < _today){
                        endDate = habit.endDate
                    }
                }


                Log.d("progress", "generateDates: ${date} - ${endDate}")
                if(date <= _today){
                    when (habit.rangeType){
                        Habit.HabitRangeType.CONTINUOUS -> {
                            while (date.isBefore(endDate) || date == endDate){
                                dates.add(date)
                                date = date.plusDays(1)
                            }

                        }
                        Habit.HabitRangeType.SPECIFIC_WEEKDAYS -> {
                            while (date.isBefore(endDate) || date == endDate){
                                if(habit.weekDays?.contains(date.dayOfWeek.value) == true){
                                    dates.add(date)
                                }
                                date = date.plusDays(1)
                            }
                        }
                        Habit.HabitRangeType.REPEATED_INTERVAL -> {
                            while (date.isBefore(endDate) || date == endDate){
                                if(ChronoUnit.DAYS.between(date, habit.startDate) % habit.repeatedInterval!! == 0L){
                                    dates.add(date)
                                }
                                date = date.plusDays(1)
                            }
                        }
                    }
                }


                //updating the live data on main thread
                withContext(Dispatchers.Main){
                    datesLiveData.value = dates
                    Log.d("progress", "generateDates: dates live data updated")
                }

            }
        }



        return datesLiveData
    }
}