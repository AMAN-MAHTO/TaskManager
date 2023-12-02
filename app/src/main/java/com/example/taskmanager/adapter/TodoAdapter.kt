package com.example.taskmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.Habit
import com.example.taskmanager.models.Task
import com.example.taskmanager.utils.Utils

class TodoAdapter(private val context:Context,private val tasks: MutableList<Task>,private val habits: MutableList<Habit>):RecyclerView.Adapter<TodoAdapter.TodoAdapterViewHolder>() {

    private val TASK_VIEW_TYPE = Utils().TASK_VIEW_TYPE
    private val HABIT_VIEW_TYPE = Utils().HABIT_VIEW_TYPE
    private lateinit var mListener: onCheckBoxChangeListener
    interface onCheckBoxChangeListener{
        fun onChange(taskId: Long, isDone: Boolean)
    }

    fun setOnCheckBoxChangeListener(listener: onCheckBoxChangeListener){
        mListener = listener
    }
    inner class TodoAdapterViewHolder(item: View,listener:onCheckBoxChangeListener):RecyclerView.ViewHolder(item){
        val textViewTitle = item.findViewById<TextView>(R.id.textViewTitle)
        val textViewDescription = item.findViewById<TextView>(R.id.textViewDescription)
        val checkBox = item.findViewById<CheckBox>(R.id.checkBoxTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_view, parent, false)
        return TodoAdapterViewHolder(view,mListener)
    }

    override fun getItemCount(): Int {
        return tasks.size + habits.size
    }

    override fun onBindViewHolder(holder: TodoAdapterViewHolder, position: Int) {
        when(getItemViewType(position)){
            TASK_VIEW_TYPE -> {
                holder.textViewTitle.text = tasks[position].title.toString()
                holder.textViewDescription.text = tasks[position].description.toString()

                holder.checkBox.isChecked = tasks[position].isDone
                holder.checkBox.setOnCheckedChangeListener { _, b ->
                    if (position != RecyclerView.NO_POSITION) {
                        val taskId = tasks[position].id
                        mListener.onChange(taskId,b)
                    }
                }
            }

            HABIT_VIEW_TYPE -> {
                holder.textViewTitle.text = habits[position].title.toString()
                holder.textViewDescription.text = habits[position].description.toString()


            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position < tasks.size){
            TASK_VIEW_TYPE
        }else{
            HABIT_VIEW_TYPE
        }
    }
    fun deleteItem(position: Int){
        when(getItemViewType(position)){
            TASK_VIEW_TYPE -> tasks.removeAt(position)
            HABIT_VIEW_TYPE -> habits.removeAt(position)

        }

        notifyDataSetChanged()
    }

    fun getTaskId(position: Int): Long {
        return when(getItemViewType(position)){
            TASK_VIEW_TYPE -> tasks[position].id
            HABIT_VIEW_TYPE -> habits[position].id

            else -> {0} //not be called
        }

    }
}