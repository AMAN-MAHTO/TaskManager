package com.example.taskmanager.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.adapter.AdapterHabitProgress
import com.example.taskmanager.databinding.FragmentProgressBinding
import com.example.taskmanager.mvvm.DatesViewModel
import com.example.taskmanager.mvvm.MainDataViewModel
import kotlinx.coroutines.launch

class ProgressFragment(val mainDataViewModel: MainDataViewModel,val datesViewModel: DatesViewModel) : Fragment() {
    private lateinit var binding:FragmentProgressBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            setUpRecyclerViewHabitsProgress()

        }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpRecyclerViewHabitsProgress() {
        mainDataViewModel.getHabitAndProgress().observe(
            viewLifecycleOwner,
            Observer {

                Log.d("progress", "setUpRecyclerViewHabitsProgress: habits = ${it}")
                val adapter = AdapterHabitProgress(context, viewLifecycleOwner, mainDataViewModel,it.first,it.second)
                binding.recyclerViewHabitsProgress.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                binding.recyclerViewHabitsProgress.adapter = adapter

                Log.d("progress", "setUpRecyclerViewHabitsProgress: adapter is set")
            }
        )


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProgressBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}