package com.example.taskmanager.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.taskmanager.R
import com.example.taskmanager.activity.NewTask
import com.example.taskmanager.databinding.FragmentTaskBottomSheetBinding
import com.example.taskmanager.models.Task
import com.example.taskmanager.mvvm.MainDataViewModel
import com.example.taskmanager.utils.DatePickerUtil
import com.example.taskmanager.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class TaskBottomSheetFragment(private val task: Task,val mainDataViewModel: MainDataViewModel): BottomSheetDialogFragment() {
    private lateinit var binding: FragmentTaskBottomSheetBinding
    lateinit var itemDeleteCallback: onItemDeleteCallback

    interface onItemDeleteCallback{
        fun onItemDeleted()
    }
    fun setOnItemDeleteCallback(callback: onItemDeleteCallback){
        itemDeleteCallback = callback
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewbottomSheetTaskName.text = task.title
        binding.textViewbottomSheetTaskDate.text = task.date.toString()
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        binding.navigationViewTaskBottomSheet.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottomSheetTaskReshedule -> {
                    val datePickerDialog = DatePickerUtil(requireContext())
                    val cDate = task.date
                    datePickerDialog.calendar.set(cDate.year, cDate.monthValue - 1, cDate.dayOfMonth)
                    datePickerDialog.showDatePickerDialog(DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                        datePickerDialog.calendar.set(i,i2,i3)
                        alertDialogBuilder.setMessage("Do you want Recreate the ${task.title} on ${i3}/${i2-1}/${i}")
                        alertDialogBuilder.setCancelable(false)
                        alertDialogBuilder.setPositiveButton("YES") { _, _, ->
                                mainDataViewModel.recreateTask(task, Utils().intToLocalDate(i,i2,i3))
                            dismiss()

                        }
                        alertDialogBuilder.setNegativeButton("NO"){_,_, ->
                            dismiss()

                        }
                        val alertDialogBox = alertDialogBuilder.create()
                        alertDialogBox.show()

                    })

                }
                R.id.bottomSheetTaskDelete -> {
                    alertDialogBuilder.setMessage("Do you want Delete?\nTask: ${task.title}")
                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton("YES") { _, _, ->
                        mainDataViewModel.deleteTask(task.id)
                        itemDeleteCallback.onItemDeleted()

                    }
                    alertDialogBuilder.setNegativeButton("NO"){_,_, ->

                    }
                    val alertDialogBox = alertDialogBuilder.create()
                    alertDialogBox.show()

                    dismiss()

                }
                R.id.bottomSheetTaskEdit -> {
                    val intent = Intent(context, NewTask::class.java)
                    intent.putExtra(R.string.editTaskIntentKey.toString(),task.id)
                    startActivity(intent)
                    dismiss()

                }
            }

            true
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTaskBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

}