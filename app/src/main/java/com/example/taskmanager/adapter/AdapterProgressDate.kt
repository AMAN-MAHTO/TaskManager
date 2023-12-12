package com.example.taskmanager.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.HabitProgress
import java.time.LocalDate

class AdapterProgressDate(val habitProgresses: List<HabitProgress>,val dates: List<LocalDate>) :RecyclerView.Adapter<AdapterProgressDate.AdapterProgressDateViewHolder>() {

    inner class AdapterProgressDateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cardViewProgressDate = itemView.findViewById<ConstraintLayout>(R.id.cardViewProgressDate)
        val textViewProgressDate = itemView.findViewById<TextView>(R.id.textViewProgressDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterProgressDateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_progress_date,parent,false)
        return AdapterProgressDateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AdapterProgressDateViewHolder, position: Int) {
            holder.textViewProgressDate.text = dates[position].dayOfMonth.toString()
            val progressOfDay = habitProgresses.filter {
                it.date == dates[position]
            }
            if(progressOfDay.isNotEmpty()){
                if(progressOfDay[0].isDone)holder.cardViewProgressDate.setBackgroundColor(R.color.tertiary)
                else holder.cardViewProgressDate.setBackgroundColor(R.color.md_theme_light_error)
            }else{
                holder.cardViewProgressDate.setBackgroundColor(R.color.md_theme_dark_surfaceTint)
            }
    }
}