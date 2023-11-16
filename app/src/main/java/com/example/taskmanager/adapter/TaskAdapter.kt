package com.example.taskmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.Task


class TaskAdapter(val context:Context, val dataset: List<Task>): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>()
    {

    inner class TaskViewHolder(item:View) : RecyclerView.ViewHolder(item){
        val textViewTitle = item.findViewById<TextView>(R.id.textViewTitle)
        val textViewDescription = item.findViewById<TextView>(R.id.textViewDescription)
        val textViewTime = item.findViewById<TextView>(R.id.textViewTime)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_view, parent, false)
            return TaskViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataset.size
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.textViewTitle.text = dataset[position].title.toString()
            holder.textViewDescription.text = dataset[position].description.toString()
            holder.textViewTime.text = dataset[position].startTime +" - "+ dataset[position].endTime
        }
    }