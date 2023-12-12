package com.example.taskmanager.mvvm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.models.Habit

import com.example.taskmanager.models.MainDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.time.LocalDate
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
class MainDataViewModel(context: Context): ViewModel() {

    // initializing database instance

    var taskDatabase = MainDatabase.getDatabase(context)


    //get dates between returns the, localdate list of all the date in between given date input





//    private var _taskListByDate = MutableLiveData<List<Task>>()
//    val taskListByDate:LiveData<List<Task>> = _taskListByDate

    //


    private var _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate


     @RequiresApi(Build.VERSION_CODES.O)
     fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date

    }

//    fun getHabitMapProgress(): MutableLiveData<Pair<Map<Long, Int>, List<Habit>>> {
//        var pair = MutableLiveData<Pair<Map<Long, Int>, List<Habit>>>()
//        var habits = listOf<Habit>()
//        val habitsIdMapProgress = mutableMapOf<Long,Int>()
//
//
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                habits = taskDatabase.habitDoa().getHabits()
//                habits.forEach {
//                    habitsIdMapProgress[it.id] =
//                        taskDatabase.habitProgressDoa().getIsDoneCount(it.id)
//                }
//            }
//            withContext(Dispatchers.Main){
//                pair.value = Pair(habitsIdMapProgress,habits)
//                Log.d("progress", "getHabitMapProgress: live data value updated")
//            }
//
//
//
//        }
//
//        return pair
//    }

    private fun getWeekDaysCount(it: Habit, _today: LocalDate): Int {
        var date = it.startDate
        var count = 0
        while (date.isBefore(_today) || date == _today) {
            if (it.weekDays?.contains(date.dayOfWeek.value) == true) {
                count++
            }
            date = date.plusDays(1)

        }
        return count
    }

    fun getHabitAndProgress(): MutableLiveData<Pair<List<Habit>, Map<Long, List<Int>>>> {
            val Live = MutableLiveData<Pair<List<Habit>, Map<Long, List<Int>>>>()
            var habits = listOf<Habit>()
            var progress = mutableMapOf<Long, List<Int>>()
        val _today = LocalDate.now()

        viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    habits = taskDatabase.habitDoa().getHabits()
                    habits.forEach {
                        progress[it.id] =
                            listOf(
                                taskDatabase.habitProgressDoa().getIsDoneCount(it.id),
                                if(it.startDate < _today){
                                    when (it.rangeType) {
                                        Habit.HabitRangeType.CONTINUOUS -> ChronoUnit.DAYS.between(
                                            it.startDate,
                                            _today
                                        ).toInt()

                                        Habit.HabitRangeType.SPECIFIC_WEEKDAYS -> getWeekDaysCount(it,_today)
                                        Habit.HabitRangeType.REPEATED_INTERVAL -> (ChronoUnit.DAYS.between(
                                            it.startDate,
                                            _today
                                        ) / it.repeatedInterval!!).toInt()
                                    }
                                }else{
                                    0
                                }

                            )
                    }
                }
                withContext(Dispatchers.Main) {
                    Live.value = Pair(habits, progress)
                }
            }
            return Live

        }

}