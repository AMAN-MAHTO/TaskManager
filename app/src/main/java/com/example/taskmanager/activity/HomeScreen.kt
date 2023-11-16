package com.example.taskmanager.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.models.Task
import com.example.taskmanager.models.TaskDataset

import com.example.taskmanager.adapter.DateAdapter
import com.example.taskmanager.adapter.TaskAdapter
import com.example.taskmanager.databinding.ActivityHomeScreenBinding
import com.example.taskmanager.models.Converters
import com.example.taskmanager.mvvm.HomeViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeScreen : AppCompatActivity() {
    private val binding: ActivityHomeScreenBinding by lazy {
        ActivityHomeScreenBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // VIEW MODEL
        val viewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(HomeViewModel::class.java)
//        viewModel.initializeDataset()


        // VIEW MODEL OBSERVERS
        // ALL DATES WITH TASK
        viewModel.allDistinctDates.observe(
            this,
            Observer {
                if(!it.isEmpty()){

                    var sortedIt  = it.sortedBy {
                        it.dayOfYear
                    }

                    Log.d("taskList", "onCreate: allDistinctDatessorted: "+sortedIt.toString())
                    val adapter = DateAdapter(this,sortedIt, viewModel)
                    binding.recylerViewDate.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                    binding.recylerViewDate.adapter = adapter
                    viewModel.taskDatabase.taskDoa().getTasksByDate(sortedIt[0]).observe(this,
                        Observer {
                            Log.d("taskList", "onCreate: "+it.toString())
                            setUpRecyclerViewTaskList(it)

                        })

                }
            }
        )

        // ALL TASK AT A DATE
        viewModel.selectedDate.observe(
            this,
            Observer {
                Log.d("taskList", "onCreate: "+it.toString())

                viewModel.taskDatabase.taskDoa().getTasksByDate(it).observe(this,
                    Observer {
                        Log.d("taskList", "onCreate: "+it.toString())
                        setUpRecyclerViewTaskList(it)

                    })


            }
        )

//



        binding.imageButtonCreateNewTask.setOnClickListener(){
            val intent = Intent(this,NewTask::class.java)
            startActivity(intent)
        }


    }

    private fun setUpRecyclerViewTaskList(it: List<Task>) {
        val adapter = TaskAdapter(this, it)
        binding.recyclerViewTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTaskList.adapter = adapter
    }
}