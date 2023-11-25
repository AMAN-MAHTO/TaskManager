package com.example.taskmanager.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.adapter.AdapterDate
import com.example.taskmanager.models.Task

import com.example.taskmanager.adapter.TaskAdapter
import com.example.taskmanager.databinding.ActivityHomeScreenBinding
import com.example.taskmanager.fragments.CalenderSheet
import com.example.taskmanager.fragments.NewOptionsSheet
import com.example.taskmanager.mvvm.DatesViewModel
import com.example.taskmanager.mvvm.HomeViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import com.example.taskmanager.utils.SwipeGesture
import com.example.taskmanager.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class HomeScreen : AppCompatActivity(){
    private val binding: ActivityHomeScreenBinding by lazy {
        ActivityHomeScreenBinding.inflate(layoutInflater)
    }

    lateinit var homeViewModel: HomeViewModel
    lateinit var datesViewModel: DatesViewModel
    lateinit var datesAdapter:AdapterDate
    lateinit var taskAdapter:TaskAdapter


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
                Log.d("taskList", "onCreate: "+it.toString())
                if(it!=null){
                    setUpTaskListView(it)

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
        val swipeGesture = object : SwipeGesture(this){

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){

                    ItemTouchHelper.LEFT ->{

                        val taskId = taskAdapter.getTaskId(viewHolder.absoluteAdapterPosition)
                        taskAdapter.deleteItem(viewHolder.absoluteAdapterPosition)
                        taskAdapter.notifyDataSetChanged()

                        Log.d("swipeGesture", "onSwiped: taskId = ${taskId}")
                        GlobalScope.launch {
                            homeViewModel.taskDatabase.taskDoa().deleteTaskBytaskId(taskId)



                        }

                    }

                }
            }

        }

        val itemtouchHelper = ItemTouchHelper(swipeGesture)
        itemtouchHelper.attachToRecyclerView(binding.recyclerViewTaskList)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCreatNewTaskButtonState(it: LocalDate) {
        binding.imageButtonCreateNewTask.isClickable = it >= datesViewModel._today
    }

    private fun setUpTaskListView(it: LocalDate) {
        //feacting data form database, and seting up the recyler view
        homeViewModel.taskDatabase.taskDoa().getTasksByDate(it).observe(this,
            Observer {
                Log.d("taskList", "onCreate: "+it.toString())
                setUpRecyclerViewTaskList(it.toMutableList())

            })
    }


    private fun setUpRecyclerViewTaskList(it: MutableList<Task>) {
        taskAdapter = TaskAdapter(this, it)
        binding.recyclerViewTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTaskList.adapter = taskAdapter


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










//
//// ALL DATES WITH TASK
//homeViewModel.allDistinctDates.observe(
//this,
//Observer {
//    if(!it.isEmpty()){
//
//        var sortedIt  = it.sortedBy {
//            it.dayOfYear
//        }
//
//        Log.d("taskList", "onCreate: allDistinctDatessorted: "+sortedIt.toString())
//        val adapter = DateAdapter(this,sortedIt, viewModel)
//        binding.recylerViewDate.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
//        binding.recylerViewDate.adapter = adapter
//        homeViewModel.taskDatabase.taskDoa().getTasksByDate(sortedIt[0]).observe(this,
//            Observer {
//                Log.d("taskList", "onCreate: "+it.toString())
//                setUpRecyclerViewTaskList(it)
//
//            })
//
//    }
//}
//)