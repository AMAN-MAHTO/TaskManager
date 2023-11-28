package com.example.taskmanager.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityNewTaskBinding
import com.example.taskmanager.models.Task
import com.example.taskmanager.mvvm.HomeViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import com.example.taskmanager.utils.DatePickerUtil
import com.example.taskmanager.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewTask : AppCompatActivity(){
    private val binding: ActivityNewTaskBinding by lazy {
        ActivityNewTaskBinding.inflate(layoutInflater)
    }
    private val calendar = Calendar.getInstance()
    private lateinit var viewModel:HomeViewModel


    @RequiresApi(Build.VERSION_CODES.O)
    val dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // get intent
        val intent = getIntent()
        if(intent.extras != null) {
            setUpDefaultView(intent)
        }

        // homeViewModel instance
        viewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(HomeViewModel::class.java)



        // create new task
        binding.buttonCreateTask.setOnClickListener(){

            if(binding.editTextTaskName.text.isNotEmpty()){
                //new task is created
                createTask()

                // starting again the HomeScreen Activity
                Toast.makeText(this,"Task Created!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Enter Task Name",Toast.LENGTH_SHORT).show()
            }


        }

        // date picker dialog
        val datePicker = DatePickerUtil(this)
        val datePickerListener = DatePickerDialog.OnDateSetListener{p0: DatePicker?, p1: Int, p2: Int, p3: Int ->
            datePicker.calendar.set(p1,p2,p3)
            binding.editTextDate.setText((Utils().intToLocalDate(p1,p2,p3)).toString())
        }
        binding.imageButtonCalender.setOnClickListener(){
            datePicker.showDatePickerDialog(datePickerListener)

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTask() {
        val newTask = Task(0,
            binding.editTextTaskName.text.toString(),
            binding.editTextDescription.text.toString(),
            LocalDate.parse(binding.editTextDate.text.toString(),dateFormatter)
        )
        Log.d("taskList", "onCreate: "+newTask.toString())

        // inserting task in database
        GlobalScope.launch {
            viewModel.taskDatabase.taskDoa().insertTask(newTask)
        }    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpDefaultView(intent: Intent?) {
        val selectedDate = LocalDate.parse(intent?.getStringExtra("selectedDate"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH))
        Log.d("newTask","selectedDate"+selectedDate.toString())
        calendar.set(selectedDate.year,selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        binding.editTextDate.setText(selectedDate.toString())


    }

}