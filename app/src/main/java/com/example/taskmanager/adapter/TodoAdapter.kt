package com.example.taskmanager.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.Habit
import com.example.taskmanager.models.HabitProgress
import com.example.taskmanager.models.Task
import com.example.taskmanager.utils.Utils

class TodoAdapter(private val context:Context,private val tasks: MutableList<Task>,private val habits: MutableList<Habit>,private val habitsProgressList: MutableList<HabitProgress>):RecyclerView.Adapter<TodoAdapter.TodoAdapterViewHolder>() {

    private val TASK_VIEW_TYPE = Utils().TASK_VIEW_TYPE
    private val HABIT_VIEW_TYPE = Utils().HABIT_VIEW_TYPE
    private val HABIT_TARGATED_VIEW_TYPE = Utils().HABIT_TARGATED_VIEW_TYPE
    private lateinit var mListener: onCheckBoxChangeListener
    private lateinit var nListener: onNumberPickerChangeListener
    private lateinit var hListener: onCheckBoxChangeListenerHabit
    interface onCheckBoxChangeListener{
        fun onChange(taskId: Long, isDone: Boolean)
    }
    fun setOnCheckBoxChangeListener(listener: onCheckBoxChangeListener){
        mListener = listener
    }
    interface onNumberPickerChangeListener{
        fun onChange(habitId: Long, number: Int)
    }
    fun setOnNumberPickerChangeListener(listener: onNumberPickerChangeListener){
        nListener = listener
    }
    interface onCheckBoxChangeListenerHabit{
        fun onChange(habitId:Long,isDone: Boolean)
    }
    fun setOnCheckBoxChangeListenerHabit(listener: onCheckBoxChangeListenerHabit){
        hListener = listener
    }



    inner class TodoAdapterViewHolder(
        item: View,
        mlistener:onCheckBoxChangeListener,
        nListener: onNumberPickerChangeListener,
        hListener: onCheckBoxChangeListenerHabit
    ):RecyclerView.ViewHolder(item){
        val textViewTitle = item.findViewById<TextView>(R.id.textViewTitle)
        val textViewDescription = item.findViewById<TextView>(R.id.textViewDescription)
        val checkBox = item.findViewById<CheckBox>(R.id.checkBoxTask)
        val editTextCurrentNumber = item.findViewById<EditText>(R.id.editTextCurrentNumber)
        val decrementButton = item.findViewById<Button>(R.id.decrementButton)
        val incrementButton = item.findViewById<Button>(R.id.incrementButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapterViewHolder {
        val view:View =
        when(viewType){
            TASK_VIEW_TYPE,HABIT_VIEW_TYPE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_view, parent, false)
            HABIT_TARGATED_VIEW_TYPE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_habit_progress_target_number, parent, false)

            else -> {LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_view, parent, false)}
        }

        return TodoAdapterViewHolder(view,mListener,nListener,hListener)
    }

    override fun getItemCount(): Int {
        return tasks.size + habits.size
    }

    override fun onBindViewHolder(holder: TodoAdapterViewHolder, position: Int) {
        Log.d("habit", "onBindViewHolder: getItemViewType(position): ${getItemViewType(position)}")
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

            HABIT_VIEW_TYPE ->{
                holder.textViewTitle.text = habits[position].title.toString()
                holder.textViewDescription.text = habits[position].description.toString()
                val progress = habitsProgressList.filter {
                    it.habitId == habits[position].id
                }
                if(progress.isNotEmpty()){
                    holder.checkBox.isChecked = progress[0].isDone
                }

                holder.checkBox.setOnCheckedChangeListener { _, b ->
                    if (position != RecyclerView.NO_POSITION) {
                        val habitId = habits[position].id
                        hListener.onChange(habitId,b)
                    }
                }
            }

            HABIT_TARGATED_VIEW_TYPE -> {
                holder.textViewTitle.text = habits[position].title.toString()
                holder.textViewDescription.text = habits[position].description.toString()
                var number = 0

                holder.incrementButton.setOnClickListener(){
                    number++
                    holder.editTextCurrentNumber.setText(number.toString())
                }
                holder.decrementButton.setOnClickListener(){
                    number--
                    holder.editTextCurrentNumber.setText(number.toString())
                }

                holder.editTextCurrentNumber.doOnTextChanged { text, start, before, count ->
                    if (position != RecyclerView.NO_POSITION){
                        if (text != null) {
                            if(text.isNotEmpty()){
                                number = text.toString().toInt()

                            }else{
                                number = 0
                            }
                            val habitId = habits[position].id
                            nListener.onChange(habitId,number)
                        }

                    }
                }



            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position < tasks.size){
            TASK_VIEW_TYPE
        }else{
            // check habit
            Log.d("habit", "getItemViewType: positio ${position} ${habits[position].progressType}")
            when(habits[position].progressType){
                HabitProgress.ProgressType.YES_OR_NO -> HABIT_VIEW_TYPE
                HabitProgress.ProgressType.TARGET_NUMBER -> HABIT_TARGATED_VIEW_TYPE
            }

        }
    }
    fun deleteItem(position: Int){
        when(getItemViewType(position)){
            TASK_VIEW_TYPE -> tasks.removeAt(position)
            HABIT_VIEW_TYPE,HABIT_TARGATED_VIEW_TYPE -> habits.removeAt(position)

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