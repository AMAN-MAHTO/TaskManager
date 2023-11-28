package com.example.taskmanager.activity

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityNewHabitBinding
import com.example.taskmanager.utils.DatePickerUtil
import com.example.taskmanager.utils.Utils
import java.time.LocalDate
import java.time.ZoneId

class NewHabit : AppCompatActivity() {
    private val binding: ActivityNewHabitBinding by lazy {
        ActivityNewHabitBinding.inflate(layoutInflater)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //setup date picker dialog
        val datePickerDialogSD = DatePickerUtil(this)
        val datePickerDialogED = DatePickerUtil(this)

        //starting date
        binding.imageButtonCalender.setOnClickListener(){
            datePickerDialogSD.showDatePickerDialog(DatePickerDialog.OnDateSetListener { datePicker, p1, p2, p3 ->
                datePickerDialogSD.calendar.set(p1,p2,p3)
                binding.editTextDateSD.setText((Utils().intToLocalDate(p1,p2,p3)).toString())
            })
        }



        binding.editTextDaysCount.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                val sd = datePickerDialogSD.calendar.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                if(text.isNotEmpty()) {
                    val ed = sd.plusDays(text.toString().toLong())
                    datePickerDialogED.calendar.set(ed.year, ed.monthValue - 1, ed.dayOfMonth)
                }else{
                    datePickerDialogED.calendar.set(sd.year, sd.monthValue - 1, sd.dayOfMonth)

                }
            }
        }

        //Ending date
        binding.imageButtonCalender2.setOnClickListener(){
            datePickerDialogED.showDatePickerDialog(DatePickerDialog.OnDateSetListener { datePicker, p1, p2, p3 ->
                datePickerDialogED.calendar.set(p1,p2,p3)
                binding.editTextDateED.setText((Utils().intToLocalDate(p1,p2,p3)).toString())
            })
        }



    }
}