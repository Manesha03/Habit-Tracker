package com.example.habittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.habittracker.R
import com.example.habittracker.data.Habit
import com.example.habittracker.data.PreferencesHelper
import com.example.habittracker.ui.dialogs.AddHabitDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitsFragment : Fragment() {
    
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var habitsList: MutableList<Habit>
    private lateinit var layoutWeek: LinearLayout
    private lateinit var layoutHabits: LinearLayout
    private lateinit var layoutEmptyState: LinearLayout
    // Progress removed
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        habitsList = preferencesHelper.getHabits().toMutableList()
        
        layoutWeek = view.findViewById(R.id.layout_week)
        layoutHabits = view.findViewById(R.id.layout_habits)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)
        // Progress removed
        
        setupWeekBar()
        setupHabitsList()

        parentFragmentManager.setFragmentResultListener(AddHabitDialog.RESULT_HABIT_ADDED, viewLifecycleOwner) { _, _ ->
            habitsList = preferencesHelper.getHabits().toMutableList()
            setupHabitsList()
        }
        parentFragmentManager.setFragmentResultListener(AddHabitDialog.RESULT_HABIT_UPDATED, viewLifecycleOwner) { _, _ ->
            habitsList = preferencesHelper.getHabits().toMutableList()
            setupHabitsList()
        }
    }
    
    private fun setupHabitsList() {
        layoutHabits.removeAllViews()
        
        if (habitsList.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            return
        }
        
        layoutEmptyState.visibility = View.GONE
        // Progress removed
        
        for (habit in habitsList) {
            val habitView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_habit, layoutHabits, false)
            
            setupHabitView(habitView, habit)
            layoutHabits.addView(habitView)
        }
    }

    private fun setupWeekBar() {
        layoutWeek.removeAllViews()
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d", Locale.getDefault())
        val today = Calendar.getInstance()
        repeat(7) {
            val dayView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_day, layoutWeek, false)
            val tvDay = dayView.findViewById<TextView>(R.id.tv_day)
            val tvDate = dayView.findViewById<TextView>(R.id.tv_date)
            tvDay.text = dayFormat.format(cal.time)
            tvDate.text = dateFormat.format(cal.time)
            // Highlight today
            if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                dayView.setBackgroundResource(R.drawable.selected_day_background)
                tvDay.setTextColor(resources.getColor(android.R.color.white, null))
                tvDate.setTextColor(resources.getColor(android.R.color.white, null))
            }
            layoutWeek.addView(dayView)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    // Progress removed
    
    private fun setupHabitView(habitView: View, habit: Habit) {
        val tvHabitIcon = habitView.findViewById<TextView>(R.id.tv_habit_icon)
        val tvHabitName = habitView.findViewById<TextView>(R.id.tv_habit_name)
        val tvHabitType = habitView.findViewById<TextView>(R.id.tv_habit_type)
        val tvHabitFrequency = habitView.findViewById<TextView>(R.id.tv_habit_frequency)
        val tvHabitReminder = habitView.findViewById<TextView>(R.id.tv_habit_reminder)
        val ivCompletionStatus = habitView.findViewById<android.widget.ImageView>(R.id.iv_completion_status)
        val ivMoreOptions = habitView.findViewById<android.widget.ImageView>(R.id.iv_more_options)
        
        // Set habit data
        tvHabitIcon.text = habit.icon
        tvHabitName.text = habit.name
        tvHabitType.text = if (habit.isPeriodic) "Periodic task" else "Habit"

        if (habit.isPeriodic && habit.frequency > 1) {
            tvHabitFrequency.text = "${habit.frequency}P"
            tvHabitFrequency.visibility = View.VISIBLE
        }

        if (habit.reminderTime.isNotEmpty()) {
            tvHabitReminder.text = habit.reminderTime
            tvHabitReminder.visibility = View.VISIBLE
        }
        
        // Set completion status
        if (habit.isCompleted) {
            ivCompletionStatus.setImageResource(R.drawable.ic_check_circle)
            ivCompletionStatus.setColorFilter(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            ivCompletionStatus.setImageResource(R.drawable.ic_radio_button_unchecked)
            ivCompletionStatus.setColorFilter(resources.getColor(android.R.color.darker_gray, null))
        }
        
        // Handle completion toggle
        ivCompletionStatus.setOnClickListener {
            toggleHabitCompletion(habit)
        }
        
        // Handle more options
        ivMoreOptions.setOnClickListener { anchor ->
            showHabitOptions(anchor, habit)
        }
    }
    
    private fun toggleHabitCompletion(habit: Habit) {
        val updatedHabit = habit.copy(
            isCompleted = !habit.isCompleted,
            completedDate = if (!habit.isCompleted) java.util.Date() else null
        )
        
        preferencesHelper.saveHabit(updatedHabit)
        habitsList = preferencesHelper.getHabits().toMutableList()
        setupHabitsList()
        // Optionally trigger widget update here if needed
    }
    
    private fun showHabitOptions(anchorView: View, habit: Habit) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add(0, 1, 0, "Edit")
        popup.menu.add(0, 2, 1, "Delete")
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    val dialog = AddHabitDialog.newInstanceForEdit(habit.id)
                    dialog.show(parentFragmentManager, "EditHabitDialog")
                    true
                }
                2 -> {
                    preferencesHelper.deleteHabit(habit.id)
                    habitsList = preferencesHelper.getHabits().toMutableList()
                    setupHabitsList()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
