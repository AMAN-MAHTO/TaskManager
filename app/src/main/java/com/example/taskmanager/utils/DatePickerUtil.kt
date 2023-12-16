package com.example.taskmanager.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar

class DatePickerUtil(val context: Context) {

    val calendar = Calendar.getInstance()
    fun showDatePickerDialog(onDateSetListener: DatePickerDialog.OnDateSetListener) {

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)



        val datePickerDialog = DatePickerDialog(context, onDateSetListener, year, month, dayOfMonth)
        datePickerDialog.show()

    }

}