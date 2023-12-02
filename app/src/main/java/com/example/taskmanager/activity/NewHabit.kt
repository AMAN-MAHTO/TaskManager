package com.example.taskmanager.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.databinding.ActivityNewHabitBinding
import com.example.taskmanager.models.Habit
import com.example.taskmanager.models.todoData
import com.example.taskmanager.mvvm.HomeViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import com.example.taskmanager.utils.DatePickerUtil
import com.example.taskmanager.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class NewHabit : AppCompatActivity() {
    private val binding: ActivityNewHabitBinding by lazy {
        ActivityNewHabitBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel:HomeViewModel
    private lateinit var datePickerDialogSD:DatePickerUtil


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // homeViewModel instance
        viewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(HomeViewModel::class.java)

        // get intent
        val intent = getIntent()
        if(intent.extras != null) {
            val selectedDate = LocalDate.parse(intent?.getStringExtra("selectedDate"),
                DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH))
            binding.editTextDateSD.setText(selectedDate.toString())
        }

        //setup date picker dialog
        datePickerDialogSD = DatePickerUtil(this)

        //starting date
        binding.imageButtonCalender.setOnClickListener(){
            datePickerDialogSD.showDatePickerDialog(DatePickerDialog.OnDateSetListener { datePicker, p1, p2, p3 ->
                datePickerDialogSD.calendar.set(p1,p2,p3)
                binding.editTextDateSD.setText((Utils().intToLocalDate(p1,p2,p3)).toString())
                if(binding.editTextDaysCount.text != null){
                    setEndDate(binding.editTextDaysCount.text.toString())
                }
            })
        }



        binding.editTextDaysCount.doOnTextChanged { text, start, before, count ->
            setEndDate(text)

        }

        binding.buttonCreateHabit.setOnClickListener() {
            val sDate =
                LocalDate.parse(binding.editTextDateSD.text.toString(), Utils().dateFormatter)
            val eDate =
                LocalDate.parse(binding.editTextDateED.text.toString(), Utils().dateFormatter)
            val name = binding.editTextHabitName.text.toString()
            val desc = binding.editTextDescription.text.toString()
            if (name.isNotEmpty() && desc.isNotEmpty() && eDate.toString()
                    .isNotEmpty() && sDate.toString().isNotEmpty()
            ) {


                GlobalScope.launch {
                    createNewHabit(sDate, eDate, name, desc)
                }

                startActivity(Intent(this, HomeScreen::class.java))

            }else{
                Toast.makeText(this, "fill all the fields", Toast.LENGTH_SHORT).show()

            }
        }




    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setEndDate(daysCount: CharSequence?) {
        if (daysCount != null) {
            val sd = datePickerDialogSD.calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            if(daysCount.isNotEmpty()) {
                val ed = sd.plusDays(daysCount.toString().toLong() - 1)
                binding.editTextDateED.setText(ed.toString())
            }else{
                binding.editTextDateED.setText(sd.toString())
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun createNewHabit(sDate: LocalDate, eDate: LocalDate, name:String, description:String){

        val newHabit = Habit(0, name, description,Habit.HabitRangeType.CONTINUOUS,sDate,eDate)
        viewModel.taskDatabase.habitDoa().insert(newHabit)
    }
}