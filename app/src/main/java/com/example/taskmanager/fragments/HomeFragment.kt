package com.example.taskmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.adapter.AdapterDate
import com.example.taskmanager.adapter.TodoAdapter
import com.example.taskmanager.databinding.FragmentHomeBinding
import com.example.taskmanager.models.Habit
import com.example.taskmanager.models.HabitProgress
import com.example.taskmanager.models.Task
import com.example.taskmanager.mvvm.DatesViewModel
import com.example.taskmanager.mvvm.MainDataViewModel
import com.example.taskmanager.utils.SwipeGesture
import com.example.taskmanager.utils.Utils
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class HomeFragment(private val mainDataViewModel: MainDataViewModel, private val datesViewModel: DatesViewModel) : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var datesAdapter:AdapterDate
    lateinit var todoAdapter: TodoAdapter
    //to update top bar title
    lateinit var topAppBar:MaterialToolbar

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topAppBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.title = ""
        topAppBar.menu.getItem(0).setVisible(true) // seting visiblity of calender button visible

        // ---------- //
        datesViewModel._dates.observe(viewLifecycleOwner,
            Observer {
                Log.d("adapterDate", "setUpHorizontalDatePicker: "+it.toString())
                datesAdapter = AdapterDate(datesViewModel)
                datesAdapter.notifyDataSetChanged()
                setUpHorizontalDatePicker(datesViewModel)
            })

        setUpHorizontalDatePicker(datesViewModel)
        datesViewModel.centerPos.observe(viewLifecycleOwner,
            Observer {
                Log.d("adapterDate", "onCreateMainActivity centerPos  "+it.toString())

                binding.recyclerViewHorizontalDatePicker.scrollToPosition(it - 3)
            })




        // VIEW MODEL OBSERVERS
        // ALL TASK AT A DATE
        datesViewModel.selectedDate.observe(
            viewLifecycleOwner,
            Observer {
                Log.d("taskList", "onCreate: selected Date"+it.toString())
                if(it!=null){

                    viewLifecycleOwner.lifecycleScope.launch {
                        setUpTaskListView(it)
                    }


                    // updating the state of new Task button
                    // active --> if the selected date is higher of equal to today
                    // deactivate --> if the selected date is lower than today




                }
            }
        )



        // top bar
        topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.calenderDateSelector ->{
                    CalenderSheet(datesViewModel).show(childFragmentManager,"Calender Sheet")
                    true
                }

                else -> {true}
            }
        }




        // buttons


        // set the swipe gesture
//        setUpSwipeGesture()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }



    private fun setUpSwipeGesture() {
        val swipeGesture = object : SwipeGesture(requireContext()){

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){

                    ItemTouchHelper.LEFT ->{

                        val alertDialogBuilder = AlertDialog.Builder(context)

                        alertDialogBuilder.setMessage("Do you want Delete")
                        alertDialogBuilder.setCancelable(false)
                        alertDialogBuilder.setPositiveButton("YES"){_,_, ->
                            //geting item id and itemType, before delting it from adapter
                            val id = todoAdapter.getTaskId(viewHolder.absoluteAdapterPosition)
                            val itemType = todoAdapter.getItemViewType(viewHolder.absoluteAdapterPosition)
                            //delte item form adapter
                            Log.d("swipeGesture", "onSwiped: absoluteAdapterPosition = ${viewHolder.absoluteAdapterPosition}")
                            todoAdapter.deleteItem(viewHolder.absoluteAdapterPosition)
                            todoAdapter.notifyDataSetChanged()
                            Log.d("swipeGesture", "onSwiped: delete form adapter positive")

                            // deleting task by id, from database
                            viewLifecycleOwner.lifecycleScope.launch {
                                withContext(Dispatchers.IO){
                                    when(itemType){
                                        Utils().TASK_VIEW_TYPE -> {
                                            mainDataViewModel.taskDatabase.taskDoa().deleteTaskById(id)
                                            Log.d("swipeGesture", "onSwiped: task delteed")

                                        }
                                        Utils().HABIT_VIEW_TYPE-> {
                                            mainDataViewModel.taskDatabase.habitDoa().deleteHabitById(id)
                                            Log.d("swipeGesture", "onSwiped: habit deleted form habit table")

                                        }
                                        Utils().HABIT_TARGATED_VIEW_TYPE ->{

                                        }
                                    }

                                }

                            }

                        }

                        alertDialogBuilder.setNegativeButton("NO"){_,_, ->
                            todoAdapter.notifyDataSetChanged()
                        }

                        val alertDialogBox = alertDialogBuilder.create()
                        alertDialogBox.show()




                    }

                }
            }

        }

        val itemtouchHelper = ItemTouchHelper(swipeGesture)
        itemtouchHelper.attachToRecyclerView(binding.recyclerViewTaskList)
        }



    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun setUpTaskListView(selectedDate: LocalDate) {

        val habits = mainDataViewModel.taskDatabase.habitDoa().getHabitByDate(selectedDate)
        val tasks = mainDataViewModel.taskDatabase.taskDoa().getTaskByDate(selectedDate) //        setUpRecyclerViewTodoList(tasks.toMutableList(),habits.toMutableList())
        val habitsProgressList = mainDataViewModel.taskDatabase.habitProgressDoa().getProgressByDate(selectedDate)

        Log.d("TODOLIST","${tasks}, ${habits}")

        // filter habits
        val filterHabits = habits.filter {
                it -> when(it.rangeType) {
            Habit.HabitRangeType.CONTINUOUS ->
                it.endDate == null || it.endDate >= selectedDate

            Habit.HabitRangeType.SPECIFIC_WEEKDAYS ->
                selectedDate.dayOfWeek.value  in it.weekDays!!

            Habit.HabitRangeType.REPEATED_INTERVAL ->
                ChronoUnit.DAYS.between(selectedDate, it.startDate) % it.repeatedInterval!! == 0L


            else -> {false}
        }
        }

        Log.d("TODOLIST","after filter ${tasks}, ${filterHabits}")



        setUpRecyclerViewTodoList(tasks.toMutableList(),filterHabits.toMutableList(),habitsProgressList.toMutableList(),selectedDate)
        Log.d("TODOLIST","after recycler views ${tasks}, ${filterHabits}")


        binding.viewStubNoData.run {
            if(tasks.isNullOrEmpty() && habits.isNullOrEmpty()) this.visibility= View.VISIBLE
            else this.visibility = View.GONE
        }




    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpRecyclerViewTodoList(tasks: MutableList<Task>, habits: MutableList<Habit>, habitsProgressList: MutableList<HabitProgress>, selectedDate: LocalDate) {

        todoAdapter = TodoAdapter(tasks,habits, habitsProgressList,selectedDate <= datesViewModel._today)
//        todoAdapter = TodoAdapter(tasks,habits, habitsProgressList,true)

        //checkbox, callback listerner implementation, for task view
        todoAdapter.setOnCheckBoxChangeListener(object : TodoAdapter.onCheckBoxChangeListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChange(id:Long, isDone: Boolean) {
                Log.d("taskList", "onChange: box change state, ${id}")
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        mainDataViewModel.taskDatabase.taskDoa().updateIsDone(id, isDone)
                    }

                }

            }

        }
        )

        todoAdapter.setOnNumberPickerChangeListener(object : TodoAdapter.onNumberPickerChangeListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChange(habitId: Long, number: Int) {
                Log.d("taskList", "onChange: number picker value, ${habitId}")
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val habitsProgressListByID = habitsProgressList.filter {
                            it.habitId == habitId
                        }

                        if (habitsProgressList.isEmpty()) {
                            val progressType = mainDataViewModel.taskDatabase.habitDoa()
                                .getProgressTypeByHabitId(habitId)
                            val newProgress = HabitProgress(
                                0,
                                habitId,
                                selectedDate,
                                progressType,
                                currentNumber = number
                            )
                            mainDataViewModel.taskDatabase.habitProgressDoa().insert(newProgress)
                        } else {
                            val progressId = habitsProgressList[0].id
                            mainDataViewModel.taskDatabase.habitProgressDoa()
                                .updateNumber(number, progressId)

                        }
                    }
                }

            }

        })

        todoAdapter.setOnCheckBoxChangeListenerHabit(object : TodoAdapter.onCheckBoxChangeListenerHabit{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChange(habitId: Long, isDone: Boolean) {
                Log.d("habit", "ChangeListenerHabit: ${habitId}, ${isDone}")
                viewLifecycleOwner.lifecycleScope.launch {
                    val habitsProgressListByID = habitsProgressList.filter {
                        it.habitId == habitId
                    }
                    Log.d("habit", "ChangeListenerHabit: habitsProgressListByID: ${habitsProgressListByID}")
                    withContext(Dispatchers.IO) {
                        if (habitsProgressList.isEmpty()) {
                            Log.d("habit", "ChangeListenerHabit: no old progress present")

                            val progressType = mainDataViewModel.taskDatabase.habitDoa()
                                .getProgressTypeByHabitId(habitId)
                            val newProgress = HabitProgress(
                                0,
                                habitId,
                                selectedDate,
                                progressType,
                                isDone = isDone
                            )

                            mainDataViewModel.taskDatabase.habitProgressDoa().insert(newProgress)


                        } else {
                            Log.d("habit", "ChangeListenerHabit: updating old progress")

                            val progressId = habitsProgressList[0].id
                            mainDataViewModel.taskDatabase.habitProgressDoa()
                                .updateIsDone(isDone, progressId)

                        }
                    }
                }
            }

        })

        // item long press listeners
        todoAdapter.setOnTaskLongClickListener(object : TodoAdapter.onTaskLongClickListener{
            override fun onTaskLongClick(task: Task, position: Int) {
                val taskBottomSheetFragment = TaskBottomSheetFragment(task,mainDataViewModel)
                taskBottomSheetFragment.setOnItemDeleteCallback(object : TaskBottomSheetFragment.onItemDeleteCallback{
                    override fun onItemDeleted() {
                        todoAdapter.deleteItem(position)
                        todoAdapter.notifyDataSetChanged()
                        if(todoAdapter.itemCount == 0){
                            binding.viewStubNoData.visibility = View.VISIBLE
                        }
                    }

                })
                taskBottomSheetFragment.show(childFragmentManager,"taskBottomShetFragment")
            }

        })

        todoAdapter.setOnHabitLongClickListener(object : TodoAdapter.onHabitLongClickListener{
            override fun onHabitLongClick(habit: Habit, position: Int) {
                val habitBottomSheetFragment = HabitBottomSheetFragment(habit,mainDataViewModel)
                habitBottomSheetFragment.setOnItemDeleteCallback(object : HabitBottomSheetFragment.onItemDeleteCallback{
                    override fun onItemDeleted() {
                        todoAdapter.deleteItem(position)
                        todoAdapter.notifyDataSetChanged()
                        if(todoAdapter.itemCount == 0){
                            binding.viewStubNoData.visibility = View.VISIBLE
                        }
                    }

                })
                habitBottomSheetFragment.show(childFragmentManager,"taskBottomShetFragment")
            }

        })



        binding.recyclerViewTaskList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTaskList.adapter = todoAdapter




    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroyView() {
        datesViewModel.selectedDate.value?.let { datesViewModel.getDatesBetween(it) }
        super.onDestroyView()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpHorizontalDatePicker(viewModel: DatesViewModel) {

        //
        datesAdapter = AdapterDate(viewModel)
        binding.recyclerViewHorizontalDatePicker.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerViewHorizontalDatePicker.adapter = datesAdapter

        Log.d("adapterDate", "onCreateMainActivity centerPos  "+viewModel.centerPos.value.toString())
        binding.recyclerViewHorizontalDatePicker.scrollToPosition(viewModel.centerPos.value!! - 4)


        // to update the month and year text view
        // month and year, value are comming from the first item of recylcer view
        binding.recyclerViewHorizontalDatePicker.addOnScrollListener(object:
            RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItemPosition = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0))
                val firstVisibleItemDate = datesAdapter.getItem(firstVisibleItemPosition) // Assuming the date is stored in a property named 'date'


                viewModel.monthAndYear.value = firstVisibleItemDate.month.toString()+", "+firstVisibleItemDate.year.toString()

            }
        }
        )


        //
        viewModel.monthAndYear.observe(viewLifecycleOwner,
            Observer {
                topAppBar.title = it
            })





    }




}