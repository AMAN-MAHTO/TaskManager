package com.example.taskmanager.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityNewTaskBinding
import com.example.taskmanager.models.Task
import com.example.taskmanager.mvvm.HomeViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class NewTask : AppCompatActivity() {
    private val binding: ActivityNewTaskBinding by lazy {
        ActivityNewTaskBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = getIntent()
        if(intent.extras != null) {
            val selectedDate = intent.getStringExtra("selectedDate")
            Log.d("newTask",selectedDate.toString())
        }


        val viewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(HomeViewModel::class.java)

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ENGLISH)
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm",Locale.ENGLISH)

        val arrayOfTime = arrayOf("00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayOfTime)
        binding.spinnerStartTime.adapter = adapter
        binding.spinnerEndTime.adapter = adapter

        var startTime = arrayOfTime[0]
        var endTime = arrayOfTime[0]

        binding.spinnerStartTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                val startTime = LocalTime.parse(arrayOfTime[p2],timeFormatter)
                 startTime = arrayOfTime[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                startTime = arrayOfTime[0]
            }

        }

        binding.spinnerEndTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                val endTime = LocalTime.parse(arrayOfTime[p2],timeFormatter)
                endTime = arrayOfTime[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                endTime = arrayOfTime[0]
            }

        }

        binding.buttonCreateTask.setOnClickListener(){
            val newTask = Task(
                binding.editTextTaskName.text.toString(),
                binding.editTextDescription.text.toString(),
                startTime,
                endTime,
                LocalDate.parse(binding.editTextDate.text.toString(),dateFormatter)
            )
            Log.d("taskList", "onCreate: "+newTask.toString())

            GlobalScope.launch {
                viewModel.taskDatabase.taskDoa().insertTask(newTask)

            }
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

    }
}