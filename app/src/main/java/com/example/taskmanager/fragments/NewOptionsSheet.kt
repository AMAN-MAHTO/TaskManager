package com.example.taskmanager.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import com.example.taskmanager.R
import com.example.taskmanager.activity.NewHabit
import com.example.taskmanager.activity.NewTask
import com.example.taskmanager.databinding.FragmentNewOptionsSheetBinding
import com.example.taskmanager.mvvm.DatesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewOptionsSheet(val datesViewModel: DatesViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewOptionsSheetBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.constraintLayoutHabit.setOnClickListener(){
            val intent = Intent(context, NewHabit::class.java)
            intent.putExtra("selectedDate",datesViewModel.selectedDate.value.toString())
            startActivity(intent)
            dismiss()
        }

        binding.constraintLayoutTask.setOnClickListener(){
            val intent = Intent(context, NewTask::class.java)
            intent.putExtra(R.string.newTaskIntentKey.toString(),datesViewModel.selectedDate.value.toString())
            startActivity(intent)
            dismiss()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewOptionsSheetBinding.inflate(inflater,container,false)

        return binding.root
    }

}