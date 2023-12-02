package com.example.taskmanager.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.Task

class AdapterTodo(val context:Context, var dataset: MutableList<Task>):
    RecyclerView.Adapter<AdapterTodo.TodoViewHolder>()
{
    private lateinit var mListener: onCheckBoxChangeListener
    interface onCheckBoxChangeListener{
        fun onChange(taskId: Long, isDone: Boolean)
    }

    fun setOnCheckBoxChangeListener(listener: onCheckBoxChangeListener){
        mListener = listener
    }

    inner class TodoViewHolder(item:View,listener: onCheckBoxChangeListener):RecyclerView.ViewHolder(item){
        val textViewTitle = item.findViewById<TextView>(R.id.textViewTitle)
        val textViewDescription = item.findViewById<TextView>(R.id.textViewDescription)
        val checkBox = item.findViewById<CheckBox>(R.id.checkBoxTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_view, parent, false)
        return TodoViewHolder(view,mListener)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.textViewTitle.text = dataset[position].title.toString()
        holder.textViewDescription.text = dataset[position].description.toString()

        holder.checkBox.isChecked = dataset[position].isDone
        holder.checkBox.setOnCheckedChangeListener { _, b ->
            if (position != RecyclerView.NO_POSITION) {
                val taskId = dataset[position].id
                mListener.onChange(taskId,b)
            }
        }
    }

    fun deleteItem(position: Int){
        dataset.removeAt(position)
        notifyDataSetChanged()
    }

    fun getTaskId(position: Int): Long {
        return dataset[position].id
    }


}