package com.example.taskmanager.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentHabitBottomSheetBinding
import com.example.taskmanager.databinding.FragmentTaskBottomSheetBinding
import com.example.taskmanager.models.Habit
import com.example.taskmanager.models.Task
import com.example.taskmanager.mvvm.MainDataViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HabitBottomSheetFragment(private val habit: Habit,val mainDataViewModel: MainDataViewModel): BottomSheetDialogFragment() {
    private lateinit var binding: FragmentHabitBottomSheetBinding
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

        binding.textViewbottomSheetHabitName.text = habit.title
        binding.textViewbottomSheetHabitStartingDate.text = habit.startDate.toString()
        binding.textViewbottomSheetHabitRangeType.text = when(habit.rangeType){
            Habit.HabitRangeType.CONTINUOUS -> "Every Day"
            Habit.HabitRangeType.SPECIFIC_WEEKDAYS -> habit.weekDays.toString()
            Habit.HabitRangeType.REPEATED_INTERVAL -> "Repeat every ${habit.repeatedInterval} days."
        }
        if (habit.endDate != null) {
            binding.textViewbottomSheetHabitEndDate.text = habit.endDate.toString()
        } else {
            binding.textViewbottomSheetHabitEndDate.text = "End date not define"
        }

        binding.navigationViewHabitBottomSheet.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottomSheetHabitDeleteProgress -> {
                    val alertDialogBuilder = AlertDialog.Builder(context)

                    alertDialogBuilder.setMessage("Restarting habit progress, previous progress will be deleted.\n Do you want to do this?")
                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton("YES") { _, _, ->
                        mainDataViewModel.deleteHabitAllProgress(habit.id)

                    }
                    alertDialogBuilder.setNegativeButton("NO"){_,_, ->

                    }
                    val alertDialogBox = alertDialogBuilder.create()
                    alertDialogBox.show()
                }
                R.id.bottomSheetHabitDelete -> {
                    val alertDialogBuilder = AlertDialog.Builder(context)

                    alertDialogBuilder.setMessage("Do you want Delete this habit?")
                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton("YES") { _, _, ->
                        mainDataViewModel.deleteHabit(habit.id)
                        itemDeleteCallback.onItemDeleted()
                    }
                    alertDialogBuilder.setNegativeButton("NO"){_,_, ->

                    }
                    val alertDialogBox = alertDialogBuilder.create()
                    alertDialogBox.show()

                }
                R.id.bottomSheetHabitEdit -> {

                }
            }
            dismiss()
            true
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHabitBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

}