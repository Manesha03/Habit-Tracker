package com.example.habittracker.ui.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.habittracker.R
import com.example.habittracker.data.Habit
import com.example.habittracker.data.PreferencesHelper
import java.util.*

class AddHabitDialog : DialogFragment() {
    
    private lateinit var preferencesHelper: PreferencesHelper
    
    private lateinit var etHabitName: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var editingHabitId: String? = null
    private val calendar: Calendar = Calendar.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_habit, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        
        initializeViews(view)
        setupListeners()
        prefillIfEditing()
    }
    
    private fun initializeViews(view: View) {
        etHabitName = view.findViewById(R.id.et_habit_name)
        etDate = view.findViewById(R.id.et_date)
        etTime = view.findViewById(R.id.et_time)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)
    }
    
    private fun setupListeners() {
        etDate.setOnClickListener { showDatePicker() }
        etTime.setOnClickListener { showTimePicker() }
        
        btnSave.setOnClickListener {
            saveHabit()
        }
        
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun prefillIfEditing() {
        editingHabitId = arguments?.getString(ARG_HABIT_ID)
        if (editingHabitId != null) {
            val habit = preferencesHelper.getHabits().firstOrNull { it.id == editingHabitId }
            if (habit != null) {
                etHabitName.setText(habit.name)
                habit.scheduledDate?.let { date ->
                    calendar.time = date
                    etDate.setText(android.text.format.DateFormat.getDateFormat(requireContext()).format(date))
                }
                if (habit.reminderTime.isNotEmpty()) {
                    etTime.setText(habit.reminderTime)
                }
            }
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(), { _, y, m, d ->
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)
            etDate.setText(android.text.format.DateFormat.getDateFormat(requireContext()).format(calendar.time))
        }, year, month, day).show()
    }

    private fun showTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        TimePickerDialog(requireContext(), { _, h, m ->
            calendar.set(Calendar.HOUR_OF_DAY, h)
            calendar.set(Calendar.MINUTE, m)
            val timeStr = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
            etTime.setText(timeStr)
        }, hour, minute, false).show()
    }
    
    private fun saveHabit() {
        val name = etHabitName.text.toString().trim()
        if (name.isEmpty()) {
            etHabitName.error = "Habit name is required"
            return
        }
        val selectedDate: Date? = if (etDate.text.isNullOrEmpty()) null else calendar.time
        val timeStr = etTime.text.toString().trim()
        
        val habit = Habit(
            id = editingHabitId ?: UUID.randomUUID().toString(),
            name = name,
            description = "",
            category = "",
            isPeriodic = false,
            frequency = 1,
            reminderTime = timeStr,
            icon = "🎯",
            scheduledDate = selectedDate
        )
        
        preferencesHelper.saveHabit(habit)
        
        // Notify parent fragment to refresh
        val resultKey = if (editingHabitId == null) RESULT_HABIT_ADDED else RESULT_HABIT_UPDATED
        parentFragmentManager.setFragmentResult(resultKey, Bundle())
        
        dismiss()
    }

    companion object {
        private const val ARG_HABIT_ID = "habit_id"
        const val RESULT_HABIT_ADDED = "habit_added"
        const val RESULT_HABIT_UPDATED = "habit_updated"

        fun newInstanceForEdit(habitId: String): AddHabitDialog {
            val dialog = AddHabitDialog()
            dialog.arguments = Bundle().apply { putString(ARG_HABIT_ID, habitId) }
            return dialog
        }
    }
}
