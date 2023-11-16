package com.example.taskmanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.mvvm.HomeViewModel
import java.time.LocalDate


class DateAdapter(
    val context: Context,
    private val dataset: List<LocalDate>,
    val viewModel: HomeViewModel
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    class DateViewHolder(view:View): RecyclerView.ViewHolder(view){
        val textViewDate = view.findViewById<TextView>(R.id.textViewDate)
        val textViewDay = view.findViewById<TextView>(R.id.textViewDay)
        val constraintLayoutMainDate = view.findViewById<ConstraintLayout>(R.id.contraintLayoutMainDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_date_day_view,parent,false)
        return DateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.textViewDate.text = dataset[position].dayOfMonth.toString()
        holder.textViewDay.text = dataset[position].dayOfWeek.toString().subSequence(0,3)
        if(dataset[position] == viewModel.selectedDate.value){

            holder.constraintLayoutMainDate.setBackgroundResource(R.drawable.bg_date_adapter_selected)
            holder.textViewDay.setTextColor(R.color.white)
            holder.textViewDate.setTextColor(R.color.white)
        }else{
            holder.constraintLayoutMainDate.setBackgroundResource(R.drawable.bg_date_adapter_unselected)
            holder.textViewDay.setTextColor(R.color.black)
            holder.textViewDate.setTextColor(R.color.black)
        }

        holder.constraintLayoutMainDate.setOnClickListener(){
            viewModel.updateSelectedDate(dataset[position])

            notifyDataSetChanged()

        }

    }



}