package com.example.taskmanager.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.mvvm.DatesViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class AdapterDate(val viewModel: DatesViewModel): RecyclerView.Adapter<AdapterDate.AdapterDateViewHolder>() {


    inner class AdapterDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val textViewDate = itemView.findViewById<TextView>(R.id.textViewDate)
        val textViewDay = itemView.findViewById<TextView>(R.id.textViewDay)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_date_unit_cell,parent,false)
        return AdapterDateViewHolder(view)

    }

    override fun getItemCount(): Int {
        return viewModel._dates.value!!.size
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AdapterDateViewHolder, position: Int) {
        holder.textViewDate.text = viewModel._dates.value?.get(position)?.dayOfMonth.toString()
        holder.textViewDay.text = viewModel._dates.value?.get(position)?.dayOfWeek.toString().subSequence(0,2)

        holder.itemView.setOnClickListener(){
            viewModel.selectedDate.value = viewModel._dates.value?.get(position)
            notifyDataSetChanged()
        }

        if(viewModel.selectedDate.value == viewModel._dates.value?.get(position)){
            holder.itemView.setBackgroundResource(R.drawable.bg_date_selected)
        }else{
            holder.itemView.setBackgroundResource(R.drawable.bg_date)
        }


    }

    fun getItem(firstVisibleItemPosition: Int): LocalDate {
        return viewModel._dates.value!![firstVisibleItemPosition]
    }
}