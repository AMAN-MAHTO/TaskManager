package com.example.taskmanager.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityNewHabitBinding
import com.example.taskmanager.models.Habit
import com.example.taskmanager.models.HabitProgress
import com.example.taskmanager.mvvm.MainDataViewModel
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
    private lateinit var viewModel:MainDataViewModel
    private lateinit var datePickerDialogSD:DatePickerUtil

    lateinit var selectedDate:LocalDate
    // habit properties
    var rangeType = Habit.HabitRangeType.CONTINUOUS
    var repeatedInterval: Int? = null
    var weekDays = mutableListOf<Int>()
    // habit progress
    var progressType = HabitProgress.ProgressType.YES_OR_NO
    var targetValue: Int? = null
    // end date
    lateinit var startDate:LocalDate
    var endDate:LocalDate? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // homeViewModel instance
        viewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(MainDataViewModel::class.java)

        // get intent
        val intent = getIntent()
        if(intent.extras != null) {
            selectedDate = LocalDate.parse(intent?.getStringExtra("selectedDate"),
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
                startDate = Utils().intToLocalDate(p1,p2,p3)
            })
        }




        // creat new habit
        binding.buttonCreateHabit.setOnClickListener() {
            val sDate =
                LocalDate.parse(binding.editTextDateSD.text.toString(), Utils().dateFormatter)
            val eDate =
                when(binding.switchEndDate.isChecked){
                    true -> endDate
                    false -> null
                }



            val name = binding.editTextHabitName.text.toString()
            val desc = binding.editTextDescription.text.toString()
            if (name.isNotEmpty() && desc.isNotEmpty() && eDate.toString()
                    .isNotEmpty() && sDate.toString().isNotEmpty()
            ) {


                GlobalScope.launch {
                    createNewHabit(name,desc,rangeType,sDate,eDate,weekDays, repeatedInterval, progressType,targetValue)
                }

                startActivity(Intent(this, MainActivity::class.java))

            }else{
                Toast.makeText(this, "fill all the fields", Toast.LENGTH_SHORT).show()

            }
        }

        // Radio Group
        // habit type selector
        binding.radioGroupHabitRangeType.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                Log.d("RadioGroup", "onCheckedChanged: ${p1}")
                when(p1){
                    R.id.radioButton1 -> {
                        rangeType = Habit.HabitRangeType.CONTINUOUS
                        binding.viewStubRepetedIntervalHabit.visibility = View.GONE
                        binding.viewStubSpecificWeekDayHabit.visibility = View.GONE
                    }
                    R.id.radioButton2 ->{
                        rangeType = Habit.HabitRangeType.SPECIFIC_WEEKDAYS
                        binding.viewStubSpecificWeekDayHabit.visibility = View.VISIBLE
                        binding.viewStubRepetedIntervalHabit.visibility = View.GONE
                    }
                    R.id.radioButton3 ->{
                        rangeType = Habit.HabitRangeType.REPEATED_INTERVAL
                        binding.viewStubRepetedIntervalHabit.visibility = View.VISIBLE
                        binding.viewStubSpecificWeekDayHabit.visibility = View.GONE
                    }

                }
            }

        })
        binding.radioGroupHabitRangeType.check(R.id.radioButton1)  // seting the default type

        //habit progress type selection
        binding.radioGroupHabitProgressType.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when(p1){
                    R.id.radioButton4 ->{
                        progressType = HabitProgress.ProgressType.YES_OR_NO
                        binding.viewStubTargetHabitProgress.visibility = View.GONE
                    }
                    R.id.radioButton5 ->{
                        progressType = HabitProgress.ProgressType.TARGET_NUMBER
                        binding.viewStubTargetHabitProgress.visibility = View.VISIBLE
                    }

                }
            }

        })

        // switch end date view stub
        binding.switchEndDate.setOnCheckedChangeListener { compoundButton, b ->
            when(b){
                true -> binding.viewStubEndDateInput.visibility = View.VISIBLE
                false -> binding.viewStubEndDateInput.visibility = View.GONE
            }
        }

        // setup view stub, layout inflator
        //end date view stub
        binding.viewStubEndDateInput.setOnInflateListener { viewStub, view ->
            val editTextDaysCount = view.findViewById<EditText>(R.id.editTextDaysCount)
            val editTextDateED = view.findViewById<TextView>(R.id.editTextDateED)
            editTextDaysCount.doOnTextChanged { text, start, before, count ->
                setEndDate(text,editTextDateED)
            }
        }

        // week day habit view stub
        binding.viewStubSpecificWeekDayHabit.setOnInflateListener { viewStub, view ->
            val checkBoxList = listOf(R.id.checkBox0,R.id.checkBox1,R.id.checkBox2,R.id.checkBox3,R.id.checkBox4,
                R.id.checkBox5,R.id.checkBox6)
            val checkBoxViewList = mutableListOf<CheckBox>()
            checkBoxList.forEach {
                checkBoxViewList.add(view.findViewById<CheckBox>(it))
            }

            checkBoxViewList.forEach {
                it.setOnCheckedChangeListener { compoundButton, b ->
                    when(b){
                        true -> weekDays.add(weekDayToInt(compoundButton.text.toString()))
                        false -> weekDays.remove(weekDayToInt(compoundButton.text.toString()))
                    }
                    Log.d("habit", "onCreate: weekdays: ${weekDays}")
                }
            }



        }

        // repeated interval habit view stub
        binding.viewStubRepetedIntervalHabit.setOnInflateListener { viewStub, view ->
            val editTextNumberRepetedDay = view.findViewById<EditText>(R.id.editTextNumberRepetedDay)
            editTextNumberRepetedDay.doOnTextChanged { text, start, before, count ->
                if (text != null) {
                    if(text.isNotEmpty()) {
                        repeatedInterval = text.toString().toInt()
                    }else{
                        repeatedInterval = null
                    }
                }
            }

        }

        // target value habit progress
        binding.viewStubTargetHabitProgress.setOnInflateListener { viewStub, view ->
            val editTextNumberTargetValue = view.findViewById<EditText>(R.id.editTextNumberTargetValue)
            editTextNumberTargetValue.doOnTextChanged { text, start, before, count ->
                if (text != null) {
                    if(text.isNotEmpty()) {
                        targetValue = text.toString().toInt()
                    }else{
                        targetValue = null
                    }
                }
            }

        }



    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setEndDate(daysCount: CharSequence?, editTextDateED: TextView) {
        if (daysCount != null) {
            val sd = datePickerDialogSD.calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            if(daysCount.isNotEmpty()) {
                val ed = sd.plusDays(daysCount.toString().toLong() - 1)
                editTextDateED.setText(ed.toString())
                endDate = ed
            }else{
                editTextDateED.setText(sd.toString())
                endDate = sd
            }
        }
    }

    private fun weekDayToInt(value:String):Int{
        return when(value){
            "Monday" -> 1
            "Tuesday" -> 2
            "Wednesday" -> 3
            "Thrusday" -> 4
            "Friday" -> 5
            "Saturday" -> 6
            "Sunday" -> 7

            else -> {1}
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun createNewHabit(
        name:String, description:String, rangeType: Habit.HabitRangeType,sDate: LocalDate, eDate: LocalDate?,  weekDays: List<Int>?,repeatedInterval: Int?,
        progressType: HabitProgress.ProgressType, targetValue: Int?
        ){

        val newHabit:Habit =
        when(rangeType){
            Habit.HabitRangeType.CONTINUOUS -> Habit(0, name, description,Habit.HabitRangeType.CONTINUOUS,sDate,eDate,progressType=progressType, tagetNumber = targetValue)
            Habit.HabitRangeType.SPECIFIC_WEEKDAYS -> Habit(0,name,description,Habit.HabitRangeType.SPECIFIC_WEEKDAYS,sDate,eDate,weekDays,progressType=progressType, tagetNumber = targetValue)
            Habit.HabitRangeType.REPEATED_INTERVAL -> Habit(0,name,description,Habit.HabitRangeType.REPEATED_INTERVAL,sDate,eDate, repeatedInterval = repeatedInterval,progressType=progressType, tagetNumber = targetValue)
        }


         viewModel.taskDatabase.habitDoa().insert(newHabit)



    }
}