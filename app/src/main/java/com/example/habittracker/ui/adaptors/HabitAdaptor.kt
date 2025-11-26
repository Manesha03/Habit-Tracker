package com.example.habittracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.Habit

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onToggleHabit: (Habit) -> Unit,
    private val onMoreOptions: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHabitIcon: TextView = itemView.findViewById(R.id.tv_habit_icon)
        val tvHabitName: TextView = itemView.findViewById(R.id.tv_habit_name)
        val tvHabitType: TextView = itemView.findViewById(R.id.tv_habit_type)
        val tvHabitFrequency: TextView = itemView.findViewById(R.id.tv_habit_frequency)
        val tvHabitReminder: TextView = itemView.findViewById(R.id.tv_habit_reminder)
        val ivCompletionStatus: ImageView = itemView.findViewById(R.id.iv_completion_status)
        val ivMoreOptions: ImageView = itemView.findViewById(R.id.iv_more_options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.tvHabitIcon.text = habit.icon
        holder.tvHabitName.text = habit.name
        holder.tvHabitType.text = if (habit.isPeriodic) "Periodic task" else "Habit"

        if (habit.isPeriodic && habit.frequency > 1) {
            holder.tvHabitFrequency.text = "${habit.frequency}P"
            holder.tvHabitFrequency.visibility = View.VISIBLE
        } else {
            holder.tvHabitFrequency.visibility = View.GONE
        }

        if (habit.reminderTime.isNotEmpty()) {
            holder.tvHabitReminder.text = habit.reminderTime
            holder.tvHabitReminder.visibility = View.VISIBLE
        } else {
            holder.tvHabitReminder.visibility = View.GONE
        }

        if (habit.isCompleted) {
            holder.ivCompletionStatus.setImageResource(R.drawable.ic_check_circle)
            holder.ivCompletionStatus.setColorFilter(holder.itemView.resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            holder.ivCompletionStatus.setImageResource(R.drawable.ic_radio_button_unchecked)
            holder.ivCompletionStatus.setColorFilter(holder.itemView.resources.getColor(android.R.color.darker_gray, null))
        }

        holder.ivCompletionStatus.setOnClickListener { onToggleHabit(habit) }
        holder.ivMoreOptions.setOnClickListener { onMoreOptions(habit) }
    }

    override fun getItemCount(): Int = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}
