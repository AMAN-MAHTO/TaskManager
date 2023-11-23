package com.example.taskmanager.fragments

import android.icu.util.LocaleData
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import com.example.taskmanager.databinding.FragmentCalenderSheetBinding
import com.example.taskmanager.mvvm.DatesViewModel
import com.example.taskmanager.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate


class CalenderSheet(val datesViewModel: DatesViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCalenderSheetBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = datesViewModel._calendar
        val selectedDate = datesViewModel.selectedDate.value
        calendar.set(selectedDate!!.year,selectedDate.monthValue - 1, selectedDate.dayOfMonth)

        val millis = calendar.timeInMillis
        binding.calendarView.date = millis

        binding.buttonCancle.setOnClickListener(){
            datesViewModel.selectedDate.value = selectedDate
            calendar.set(selectedDate.year,selectedDate.monthValue - 1, selectedDate.dayOfMonth)

            dismiss()
        }

        binding.buttonToday.setOnClickListener(){
            val today = LocalDate.now()
            datesViewModel.selectedDate.value = today
            calendar.set(today.year,today.monthValue - 1, today.dayOfMonth)
            // chagne center value
            datesViewModel.centerDate.value = datesViewModel.selectedDate.value
            // get new dates list for horizontal dates picker
            datesViewModel.getDatesBetween(datesViewModel.centerDate.value!!)
            dismiss()
        }

        binding.calendarView.setOnDateChangeListener(object: CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
                calendar.set(p1,p2+1,p3)
                //update selected value
                datesViewModel.selectedDate.value = Utils().intToLocalDate(p1,p2,p3)

                Log.d("adapterDate", "onDateSet: "+datesViewModel.selectedDate.value.toString())
                // chagne center value
                datesViewModel.centerDate.value = datesViewModel.selectedDate.value
                // get new dates list for horizontal dates picker
                datesViewModel.getDatesBetween(datesViewModel.centerDate.value!!)
                dismiss()
            }

        })


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalenderSheetBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}