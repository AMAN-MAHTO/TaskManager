package com.example.taskmanager.activity

import android.annotation.SuppressLint
import android.app.AlertDialog

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.adapter.AdapterDate
import com.example.taskmanager.adapter.TodoAdapter


import com.example.taskmanager.databinding.ActivityHomeScreenBinding
import com.example.taskmanager.fragments.CalenderSheet
import com.example.taskmanager.fragments.NewOptionsSheet
import com.example.taskmanager.models.Habit
import com.example.taskmanager.models.HabitProgress
import com.example.taskmanager.models.Task

import com.example.taskmanager.mvvm.DatesViewModel
import com.example.taskmanager.mvvm.HomeViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import com.example.taskmanager.utils.SwipeGesture
import com.example.taskmanager.utils.Utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class HomeScreen : AppCompatActivity(){

    private val binding: ActivityHomeScreenBinding by lazy {
        ActivityHomeScreenBinding.inflate(layoutInflater)
    }

    lateinit var homeViewModel: HomeViewModel
    lateinit var datesViewModel: DatesViewModel
    lateinit var datesAdapter:AdapterDate
//    lateinit var taskAdapter:TaskAdapter
    lateinit var todoAdapter:TodoAdapter


    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // VIEW MODEL
         homeViewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(HomeViewModel::class.java)
//        viewModel.initializeDataset()

        // date view model
        datesViewModel = ViewModelProvider(this).get(DatesViewModel::class.java)
        datesViewModel._dates.observe(this,
            Observer {
                Log.d("adapterDate", "setUpHorizontalDatePicker: "+it.toString())
                datesAdapter = AdapterDate(datesViewModel)
                datesAdapter.notifyDataSetChanged()
                setUpHorizontalDatePicker(datesViewModel)
            })

        setUpHorizontalDatePicker(datesViewModel)
        datesViewModel.centerPos.observe(this,
            Observer {
                Log.d("adapterDate", "onCreateMainActivity centerPos  "+it.toString())

                binding.recyclerViewHorizontalDatePicker.scrollToPosition(it - 3)
            })




        // VIEW MODEL OBSERVERS
        // ALL TASK AT A DATE
        datesViewModel.selectedDate.observe(
            this,
            Observer {
                Log.d("taskList", "onCreate: selected Date"+it.toString())
                if(it!=null){
                    GlobalScope.launch {
                        setUpTaskListView(it)
                    }


                    // updating the state of new Task button
                    // active --> if the selected date is higher of equal to today
                    // deactivate --> if the selected date is lower than today
                    updateCreatNewTaskButtonState(it)



                }
            }
        )


//


        // buttons
        // Create new Task button
        binding.imageButtonCreateNewTask.setOnClickListener(){
//
            NewOptionsSheet(this,datesViewModel).show(supportFragmentManager,"NewOptionsSheet")
        }

        //date piccker dialog
        binding.imageButtonCalender.setOnClickListener(){
//

            CalenderSheet(datesViewModel).show(supportFragmentManager,"Calender Sheet")
        }


        //swipe gesture
        setUpSwipeGesture()



    }



    private fun setUpSwipeGesture() {
        val swipeGesture = object : SwipeGesture(this){

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){

                    ItemTouchHelper.LEFT ->{

                        val alertDialogBuilder = AlertDialog.Builder(this@HomeScreen)

                        alertDialogBuilder.setMessage("Do you want this todo?")
                        alertDialogBuilder.setCancelable(false)
                        alertDialogBuilder.setPositiveButton("YES"){_,_, ->
                            //geting item id and itemType, before delting it from adapter
                            val id = todoAdapter.getTaskId(viewHolder.absoluteAdapterPosition)
                            val itemType = todoAdapter.getItemViewType(viewHolder.absoluteAdapterPosition)
                            //delte item form adapter
                            Log.d("swipeGesture", "onSwiped: absoluteAdapterPosition = ${viewHolder.absoluteAdapterPosition}")
                            todoAdapter.deleteItem(viewHolder.absoluteAdapterPosition)
                            todoAdapter.notifyDataSetChanged()
                            Log.d("swipeGesture", "onSwiped: delete form adapter positive")

                            // deleting task by id, from database
                            Log.d("swipeGesture", "onSwiped: taskId = ${taskId}")
                            GlobalScope.launch {
                                when(itemType){
                                    Utils().TASK_VIEW_TYPE -> {
                                        homeViewModel.taskDatabase.taskDoa().deleteTaskById(id)
                                        Log.d("swipeGesture", "onSwiped: task delteed")

                                    }
                                    Utils().HABIT_VIEW_TYPE-> {
                                        homeViewModel.taskDatabase.habitDoa().deleteHabitById(id)
                                        Log.d("swipeGesture", "onSwiped: habit deleted form habit table")

                                    }
                                    Utils().HABIT_TARGATED_VIEW_TYPE ->{

                                    }
                                }

                            }

                        }

                        alertDialogBuilder.setNegativeButton("NO"){_,_, ->
                            todoAdapter.notifyDataSetChanged()
                        }

                        val alertDialogBox = alertDialogBuilder.create()
                        alertDialogBox.show()




                    }

                }
            }

        }

        val itemtouchHelper = ItemTouchHelper(swipeGesture)
        itemtouchHelper.attachToRecyclerView(binding.recyclerViewTaskList)    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCreatNewTaskButtonState(it: LocalDate) {
        binding.imageButtonCreateNewTask.isClickable = it >= datesViewModel._today
        if(it >= datesViewModel._today){
            binding.imageButtonCreateNewTask.setBackgroundResource(R.drawable.bg_date_selected)
        }else{
            binding.imageButtonCreateNewTask.setBackgroundResource(R.drawable.bg_date)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun setUpTaskListView(selectedDate: LocalDate) {

        val habits = homeViewModel.taskDatabase.habitDoa().getHabitByDate(selectedDate)
        val tasks = homeViewModel.taskDatabase.taskDoa().getTaskByDate(selectedDate) //        setUpRecyclerViewTodoList(tasks.toMutableList(),habits.toMutableList())
        val habitsProgressList = homeViewModel.taskDatabase.habitProgressDoa().getProgressByDate(selectedDate)
        Log.d("TODOLIST","${tasks}, ${habits}")

        // filter habits
        val filterHabits = habits.filter {
            it -> when(it.rangeType) {
                Habit.HabitRangeType.CONTINUOUS ->
                    it.endDate == null || it.endDate >= selectedDate

                Habit.HabitRangeType.SPECIFIC_WEEKDAYS ->
                    selectedDate.dayOfWeek.value  in it.weekDays!!

                Habit.HabitRangeType.REPEATED_INTERVAL ->
                    ChronoUnit.DAYS.between(selectedDate, it.startDate) % it.repeatedInterval!! == 0L


                else -> {false}
        }
        }

        Log.d("TODOLIST","after filter ${tasks}, ${filterHabits}")



        setUpRecyclerViewTodoList(tasks.toMutableList(),filterHabits.toMutableList(),habitsProgressList.toMutableList(),selectedDate)
        Log.d("TODOLIST","after recycler views ${tasks}, ${filterHabits}")

        runOnUiThread(object : Runnable{
            override fun run() {
                binding.viewStubNoData.run {
                    if(tasks.isNullOrEmpty() && habits.isNullOrEmpty()) this.visibility= View.VISIBLE
                    else this.visibility = View.GONE
                }
            }

        })






    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpRecyclerViewTodoList(tasks: MutableList<Task>, habits: MutableList<Habit>, habitsProgressList: MutableList<HabitProgress>, selectedDate:LocalDate) {

        todoAdapter = TodoAdapter(this, tasks,habits, habitsProgressList,selectedDate <= datesViewModel._today)
        //checkbox, callback listerner implementation, for task view
        todoAdapter.setOnCheckBoxChangeListener(object : TodoAdapter.onCheckBoxChangeListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChange(id:Long, isDone: Boolean) {
                Log.d("taskList", "onChange: box change state, ${id}")
                GlobalScope.launch {
                    homeViewModel.taskDatabase.taskDoa().updateIsDone(id,isDone)

                }

            }

        }
        )

        todoAdapter.setOnNumberPickerChangeListener(object : TodoAdapter.onNumberPickerChangeListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChange(habitId: Long, number: Int) {
                Log.d("taskList", "onChange: number picker value, ${habitId}")
                GlobalScope.launch {
                    val habitsProgressListByID = habitsProgressList.filter {
                        it.habitId == habitId
                    }
                    if(habitsProgressList.isEmpty()){
                        val progressType = homeViewModel.taskDatabase.habitDoa().getProgressTypeByHabitId(habitId)
                        val newProgress = HabitProgress(0,habitId,selectedDate,progressType, currentNumber = number)
                        homeViewModel.taskDatabase.habitProgressDoa().insert(newProgress)
                    }else{
                        val progressId = habitsProgressList[0].id
                        homeViewModel.taskDatabase.habitProgressDoa().updateNumber(number,progressId)

                    }
                }

            }

        })

        todoAdapter.setOnCheckBoxChangeListenerHabit(object : TodoAdapter.onCheckBoxChangeListenerHabit{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChange(habitId: Long, isDone: Boolean) {
                Log.d("habit", "ChangeListenerHabit: ${habitId}, ${isDone}")
                GlobalScope.launch {
                    val habitsProgressListByID = habitsProgressList.filter {
                        it.habitId == habitId
                    }
                    Log.d("habit", "ChangeListenerHabit: habitsProgressListByID: ${habitsProgressListByID}")

                    if(habitsProgressList.isEmpty()){
                        Log.d("habit", "ChangeListenerHabit: no old progress present")

                        val progressType = homeViewModel.taskDatabase.habitDoa().getProgressTypeByHabitId(habitId)
                        val newProgress = HabitProgress(0,habitId,selectedDate,progressType, isDone = isDone)
                        homeViewModel.taskDatabase.habitProgressDoa().insert(newProgress)

                    }else{
                        Log.d("habit", "ChangeListenerHabit: updating old progress")

                        val progressId = habitsProgressList[0].id
                        homeViewModel.taskDatabase.habitProgressDoa().updateIsDone(isDone,progressId)

                    }
                }
            }

        })


        runOnUiThread {
            binding.recyclerViewTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerViewTaskList.adapter = todoAdapter
        }



    }





    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpHorizontalDatePicker(viewModel: DatesViewModel) {
        datesAdapter = AdapterDate(viewModel)
        binding.recyclerViewHorizontalDatePicker.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerViewHorizontalDatePicker.adapter = datesAdapter

        Log.d("adapterDate", "onCreateMainActivity centerPos  "+viewModel.centerPos.value.toString())
        binding.recyclerViewHorizontalDatePicker.scrollToPosition(viewModel.centerPos.value!! - 4)


        // to update the month and year text view
        // month and year, value are comming from the first item of recylcer view
        binding.recyclerViewHorizontalDatePicker.addOnScrollListener(object:
            RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItemPosition = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0))
                val firstVisibleItemDate = datesAdapter.getItem(firstVisibleItemPosition) // Assuming the date is stored in a property named 'date'


                viewModel.month.value = firstVisibleItemDate.month.toString()
                viewModel.year.value = firstVisibleItemDate.year.toString()
            }
        }
        )


        //
        viewModel.month.observe(this,
            Observer {
                binding.textViewMonth.text = it
            })

        viewModel.year.observe(this,
            Observer {
                binding.textViewYear.text = ", "+it
            })



    }




}






