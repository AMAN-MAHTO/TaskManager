package com.example.taskmanager.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.db.williamchart.view.DonutChartView
import com.example.taskmanager.R
import com.example.taskmanager.models.Habit
import com.example.taskmanager.mvvm.MainDataViewModel


class AdapterHabitProgress(
    val context: Context?,
    val lifecycleOwner: LifecycleOwner,
    val mainDataViewModel: MainDataViewModel,
    val habits: List<Habit>,
    val progress: Map<Long, List<Int>>,

    ): RecyclerView.Adapter<AdapterHabitProgress.AdapterHabitProgressViewHolder>() {



private val listOfColor = listOf(
    R.color.md_theme_dark_onPrimary,
    R.color.md_theme_dark_onTertiary,
    R.color.md_theme_light_tertiary

)
    inner class AdapterHabitProgressViewHolder(val itemView:View): RecyclerView.ViewHolder(itemView){
        val textViewHabitName = itemView.findViewById<TextView>(R.id.textViewHabitName)
        val textViewHabitDescription = itemView.findViewById<TextView>(R.id.textViewHabitDescription)
        val textViewHabitStartDate = itemView.findViewById<TextView>(R.id.textViewHabitStartDate)

        val donutChart = itemView.findViewById<DonutChartView>(R.id.donutChart)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHabitProgressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_habit_progress,parent,false)
        return AdapterHabitProgressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habits.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AdapterHabitProgressViewHolder, position: Int) {
        // Initialize the chart configuration
        holder.donutChart.donutColors = intArrayOf(
            listOfColor[position % listOfColor.size]
        )

        val data = listOf( progress[habits[position].id]!![0].toFloat())// Sample data, replace with your data
//        val data = listOf(2f,3f,4f)
        holder.donutChart.donutTotal = progress[habits[position].id]!![1].toFloat()
        holder.donutChart.animation.duration = 1000L
        holder.donutChart.animate(data)

        holder.textViewHabitName.text = habits[position].title
        holder.textViewHabitDescription.text = habits[position].description
        holder.textViewHabitStartDate.text = "Starting Date: "+habits[position].startDate.toString()


    }


}

//        datesViewModel.generateDates(habits[position]).observe(
//            lifecycleOwner,
//            Observer {
//                Log.d("progress", "onBindViewHolder: ${habits}")
//                holder.textViewHabitName.text = habits[position].title
//                holder.textViewHabitDescription.text = habits[position].description
//                Log.d("progress","dates: ${it}")
//                Log.d("progress", "onBindViewHolder: adapterHabitProgress: habitId = ${habitsIdMapProgress[habits[position].id]}")
//                if (habitsIdMapProgress[habits[position].id] != null){
//                    val adapter = AdapterProgressDate(habitsIdMapProgress[habits[position].id]!!,it)
//                    holder.recyclerViewHabitProgressDate.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
//                    holder.recyclerViewHabitProgressDate.adapter = adapter
//
//                }
//            }
//        )