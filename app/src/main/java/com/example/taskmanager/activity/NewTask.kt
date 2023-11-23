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
import com.example.taskmanager.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewTask : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private val binding: ActivityNewTaskBinding by lazy {
        ActivityNewTaskBinding.inflate(layoutInflater)
    }
    private val calendar = Calendar.getInstance()

    val arrayOfTime = arrayOf("00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00")
    var startTime = arrayOfTime[0]
    var endTime = arrayOfTime[0]
    @RequiresApi(Build.VERSION_CODES.O)
    val dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
    @RequiresApi(Build.VERSION_CODES.O)
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm",Locale.ENGLISH)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = getIntent()
        if(intent.extras != null) {
            setUpDefaultView(intent)
        }


        val viewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(HomeViewModel::class.java)

        setUpSpinnerView()


        binding.buttonCreateTask.setOnClickListener(){
            //new task is created
            val newTask = Task(
                binding.editTextTaskName.text.toString(),
                binding.editTextDescription.text.toString(),
                startTime,
                endTime,
                LocalDate.parse(binding.editTextDate.text.toString(),dateFormatter)
            )
            Log.d("taskList", "onCreate: "+newTask.toString())

            // inserting task in database
            GlobalScope.launch {
                viewModel.taskDatabase.taskDoa().insertTask(newTask)
            }

            // starting again the HomeScreen Activity
            Toast.makeText(this,"Task Created!",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        binding.imageButtonCalender.setOnClickListener(){
            DatePickerDialog(this,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(
                Calendar.DAY_OF_MONTH)).show()

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpDefaultView(intent: Intent?) {
        val selectedDate = LocalDate.parse(intent?.getStringExtra("selectedDate"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH))
        Log.d("newTask","selectedDate"+selectedDate.toString())
        calendar.set(selectedDate.year,selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        binding.editTextDate.setText(selectedDate.toString())


    }


    private fun setUpSpinnerView() {

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayOfTime)
        binding.spinnerStartTime.adapter = adapter
        binding.spinnerEndTime.adapter = adapter

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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        calendar.set(p1,p2+1,p3)
        binding.editTextDate.setText((Utils().intToLocalDate(p1,p2,p3)).toString())
    }
}