package com.example.taskmanager.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityNewTaskBinding
import com.example.taskmanager.models.Task

import com.example.taskmanager.mvvm.MainDataViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import com.example.taskmanager.utils.DatePickerUtil
import com.example.taskmanager.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.log

class NewTask : AppCompatActivity() {
    private val binding: ActivityNewTaskBinding by lazy {
        ActivityNewTaskBinding.inflate(layoutInflater)
    }
    private lateinit var mainDataViewModel: MainDataViewModel
    private lateinit var datePicker: DatePickerUtil


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //top app bar
        binding.topAppBarTask.setOnClickListener() {
            onBackPressed()
        }

        // MainDataViewModel instance
        mainDataViewModel =
            ViewModelProvider(this, HomeViewModelFactory(this)).get(MainDataViewModel::class.java)

        // get intent
        val intent = getIntent()
        if (intent.extras != null) {
            Log.d("newTask", "onCreate: ${intent.extras}")
            val a = intent.getStringExtra(R.string.newTaskIntentKey.toString())
            Log.d("newTask", "onCreate: a ${a}")
            if(a!=null){
                setUpDefaultView(a)
            }else{

                val b = intent.getLongExtra(R.string.editTaskIntentKey.toString(),0)
                Log.d("newTask", "onCreate: b ${b}")

                setUpEditView(b)

            }





        }



        // date picker dialog
        datePicker = DatePickerUtil(this)
        val datePickerListener =
            DatePickerDialog.OnDateSetListener { p0: DatePicker?, p1: Int, p2: Int, p3: Int ->
                datePicker.calendar.set(p1, p2, p3)
                binding.editTextDate.setText((Utils().intToLocalDate(p1, p2, p3)).toString())
            }
        binding.imageButtonCalender.setOnClickListener() {
            datePicker.showDatePickerDialog(datePickerListener)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpEditView(taskId: Long) {

        mainDataViewModel.taskDatabase.taskDoa().getTaskById(taskId).observe(
            this,
            Observer { task ->
                Log.d("newTask", "setUpEditView: task ${task}")
                binding.editTextTaskName.setText(task.title)
                binding.editTextDescription.setText(task.description)
                binding.checkBoxEditTaskIsDone.visibility = View.VISIBLE
                binding.checkBoxEditTaskIsDone.isChecked = task.isDone

                setSelectedDate(task.date.toString())
                binding.buttonCreateTask.setText("Update Task")
                binding.buttonCreateTask.setOnClickListener {
                    updateTask(task)
                }
            }
        )


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTask(exitingTask: Task) {
        val name = binding.editTextTaskName.text.toString()
        val desc = binding.editTextDescription.text.toString()
        val date = LocalDate.parse(binding.editTextDate.text.toString(), Utils().dateFormatter)
        val isDone = binding.checkBoxEditTaskIsDone.isChecked

        if (name.isEmpty()) {
            Toast.makeText(this, "enter title", Toast.LENGTH_SHORT).show()
        } else if (desc.isEmpty()) {
            Toast.makeText(this, "enter desc", Toast.LENGTH_SHORT).show()
        } else {
            //todoData insert new task
            GlobalScope.launch {
                exitingTask.title = name
                exitingTask.description = desc
                exitingTask.date = date
                exitingTask.isDone = isDone

                mainDataViewModel.taskDatabase.taskDoa().update(exitingTask)
                // starting again the HomeScreen fragment
                Log.d("newTask", "updateTask: task is updated")
            }



            startActivity(Intent(this, MainActivity::class.java))

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTask() {
        val name = binding.editTextTaskName.text.toString()
        val desc = binding.editTextDescription.text.toString()
        val date = LocalDate.parse(binding.editTextDate.text.toString(), Utils().dateFormatter)

        if (name.isEmpty()) {
            Toast.makeText(this, "enter title", Toast.LENGTH_SHORT).show()
        } else if (desc.isEmpty()) {
            Toast.makeText(this, "enter desc", Toast.LENGTH_SHORT).show()
        } else {
            //todoData insert new task
            GlobalScope.launch {
                val newTask = Task(0, date, name, desc)
                mainDataViewModel.taskDatabase.taskDoa().insert(newTask)
            }



            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setSelectedDate(selectedDatePrev: String) {
        val selectedDate = LocalDate.parse(
            selectedDatePrev,
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
        )
        Log.d("newTask", "selectedDate" + selectedDate.toString())
        datePicker.calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        binding.editTextDate.setText(selectedDate.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpDefaultView(selectedDatePrev: String) {
        setSelectedDate(selectedDatePrev)


        binding.checkBoxEditTaskIsDone.visibility = View.GONE


        // create new task
        binding.buttonCreateTask.setOnClickListener() {
            //new task is created
            createTask()
        }


    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

}